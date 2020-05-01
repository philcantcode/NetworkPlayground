/* gcc -shared -o mac.so mac.c -I /Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/include/ -I /Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/include/darwin/ -lpcap */
#include <jni.h>
#include "engine_mac.h"

#include <stdio.h>
#include <pcap.h>
#include <math.h>
#include <stdlib.h>
#include <string.h>
#include <net/ethernet.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define WIFIBEACONS "link[0] == 0x80" // type mgt subtype beacon

JNIEnv *env_g;
jobject *job_g;
/*
	0 = Stop capture
	1 = Regular packet capture, PROMISC off
	2 = Regular packet capture, PROMISC on
	3 = Monitor mode capture, WIFIBEACONS
    4 = Monitor mode capture, ALL
    5 = Send Packet, Normal mode
    6 = Send Packet, Monitor mode
*/
int status = 0;
int promisc = 0;
pcap_t *handle;
char *filter = "";
struct bpf_program fp;
char errbuf[PCAP_ERRBUF_SIZE];        /* error buffer */
bpf_u_int32 mask;            /* subnet mask */
bpf_u_int32 net;            /* ip */
int num_packets = 0;            /* number of packets to capture */

void notify(char *data);
void got_packet(u_char *args, const struct pcap_pkthdr *header, const u_char *packet);
void deliver_packet(char *data[], int size);
char *timestamp_string(struct timeval ts);
void updateBPF();
void enterMonitorMode();
void sendPacket(const char* dev);

const char *bit_rep[16] = {
    [ 0] = "0000", [ 1] = "0001", [ 2] = "0010", [ 3] = "0011",
    [ 4] = "0100", [ 5] = "0101", [ 6] = "0110", [ 7] = "0111",
    [ 8] = "1000", [ 9] = "1001", [10] = "1010", [11] = "1011",
    [12] = "1100", [13] = "1101", [14] = "1110", [15] = "1111",
};

void notify(char *data)
{
	jclass cls = (*env_g)->GetObjectClass(env_g, *job_g);
    jmethodID notifyMsg = (*env_g)->GetMethodID(env_g, cls, "notify", "(Ljava/lang/String;)I");
    status = (int) (*env_g)->CallObjectMethod(env_g, *job_g, notifyMsg, (*env_g) -> NewStringUTF(env_g, data));
}

void deliver_packet(char *data[], int size)
{
    jstring str;
    jobjectArray dataArray = 0;
    int arraySize = size;
    jsize len = size;
    int i;

    dataArray = (*env_g) -> NewObjectArray(env_g, len, (*env_g) -> FindClass(env_g, "java/lang/String"), 0);

    for(i=0; i < arraySize ;i++)
    {
      str = (*env_g) -> NewStringUTF(env_g, data[i]);
      (*env_g) -> SetObjectArrayElement(env_g, dataArray, i, str);
    }

	jclass cls = (*env_g) -> GetObjectClass(env_g, *job_g);
    jmethodID method = NULL;

    method = (*env_g) -> GetMethodID(env_g, cls, "deliverHex", "([Ljava/lang/String;)I");
    status = (int) (*env_g)->CallObjectMethod(env_g, *job_g, method, dataArray);

    if (status == 0)
    	pcap_breakloop(handle);
}

void got_packet(u_char *args, const struct pcap_pkthdr *header, const u_char *packet)
{
	if (status == 0)
        pcap_breakloop(handle);

    char dlt[32]; // Data Link Type, defines packet structure
	snprintf(dlt, 32, "%d", pcap_datalink(handle));

	//char hexStr[header -> len * 2 + 1];
	char binStr[header -> len * 8 + 1];
	int k;

	for(k = 0 ; k < header -> len; k++)
	{
		//sprintf(&hexStr[k * 2], "%02X", packet[k]);
		sprintf(&binStr[k * 8], "%s%s", bit_rep[packet[k] >> 4], bit_rep[packet[k] & 0x0F]);
	}

	char *data[] =
	{
		dlt, binStr//, hexStr
	};

	deliver_packet(data, (sizeof(data) / sizeof(data[0])));
    
    return;
}

JNIEXPORT void JNICALL Java_engine_NetHandler_init(JNIEnv *env, jobject job, jstring jstr)
{
    const char *dev = (*env)->GetStringUTFChars(env, jstr, NULL);

    env_g = env;
    job_g = &job;

	notify("Starting capture");

    if((handle = pcap_create(dev, errbuf)) == NULL)
    {
        notify("pcap create error");
        exit(EXIT_FAILURE);
    }

    if (status == 1)
    {
		notify("Starting regular capture, PROMISC off");
		promisc = 0;
	}
	else if (status == 2)
    {
		notify("Starting regular capture, PROMISC on");
		promisc = 1;
	}
	else if (status == 3 || status == 4)
    {
		notify("Starting monitor mode capture, WIFIBEACONS");

        if (status == 3)
            filter = WIFIBEACONS;

        enterMonitorMode();
	}
    else if (status == 5 || status == 6)
    {
        notify("Sending Packet");

        if (status == 5)
            enterMonitorMode();

        sendPacket(dev);
        exit(0);
    }

    pcap_set_snaplen(handle, 65535);  // Set the snapshot length to 2048
    pcap_set_promisc(handle, promisc); // Turn promiscuous mode off
    pcap_set_timeout(handle, 512);

    if(pcap_activate(handle) != 0)
    {
        notify("pcap_activate() failed\n");
        exit(EXIT_FAILURE);
    }

    if(pcap_compile(handle, &fp, filter, 0, PCAP_NETMASK_UNKNOWN) == -1)
    {
        notify("filter compile error");
        exit(EXIT_FAILURE);
    }

    if(pcap_setfilter(handle, &fp) == -1)
    {
        notify("set filter error");
        exit(EXIT_FAILURE);
    }

    pcap_loop(handle, -1, got_packet, NULL);

    pcap_set_rfmon(handle, 0);
    pcap_freecode(&fp);
    pcap_close(handle);
}

/* Update Functions *********************************************************************/

void updateBPF()
{
    jmethodID method = NULL;
    const char *str;
    char filter[1024];

    jclass cls = (*env_g) -> GetObjectClass(env_g, *job_g);
    method = (*env_g) -> GetMethodID(env_g, cls, "updateBPF", "()Ljava/lang/String;");
    jobject ret = (*env_g)->CallObjectMethod(env_g, *job_g, method);
    str = (*env_g)->GetStringUTFChars(env_g, (jstring)ret, NULL);
    snprintf(filter, 1024, "%s", str);

    if (pcap_compile(handle, &fp, filter, 0, net) == -1) {
        char msg[200];
        snprintf(msg, 200, "[Error]: Couldn't parse filter %s: %s\n", filter, pcap_geterr(handle));
        notify(msg);
        exit(EXIT_FAILURE);
    }

    if (pcap_setfilter(handle, &fp) == -1)
    {
        char msg[200];
        snprintf(msg, 200, "[Error]: Couldn't install filter %s: %s\n", filter, pcap_geterr(handle));
        notify(msg);
        exit(EXIT_FAILURE);
    }
    else
    {
        char msg[200];
        snprintf(msg, 200, "[Status]: Installed new filter: %s\n", filter);

        notify(msg);
    }
}

/* Utilities *******************************************************************/

char *timestamp_string(struct timeval ts)
{
    static char timestamp_string_buf[256];

    sprintf(timestamp_string_buf, "%d.%06d",
            (int) ts.tv_sec, (int) ts.tv_usec);

    return timestamp_string_buf;
}

void enterMonitorMode()
{
    if(pcap_can_set_rfmon(handle) == 0)
    {
        notify("Monitor mode can not be set.\n");
        exit(EXIT_FAILURE);
    }

    if(pcap_set_rfmon(handle, 1) != 0)
    {
        notify("Failed to set monitor mode.\n");
        exit(EXIT_FAILURE);
    }
    else
    {
        notify("Monitor Mode Set.\n");
    }
}

unsigned char* hexstr_to_char(const char* hexstr)
{
    size_t len = strlen(hexstr);
    size_t final_len = len / 2;
    unsigned char* chrs = (unsigned char*)malloc((final_len+1) * sizeof(*chrs));
    for (size_t i=0, j=0; j<final_len; i+=2, j++)
        chrs[j] = (hexstr[i] % 32 + 9) % 25 * 16 + (hexstr[i+1] % 32 + 9) % 25;
    chrs[final_len] = '\0';
    return chrs;
}

void sendPacket(const char* dev)
{
    jmethodID method = NULL;
    const char *str;

    jclass cls = (*env_g) -> GetObjectClass(env_g, *job_g);
    method = (*env_g) -> GetMethodID(env_g, cls, "sendPacket", "()Ljava/lang/String;");
    jobject ret = (*env_g)->CallObjectMethod(env_g, *job_g, method);
    str = (*env_g)->GetStringUTFChars(env_g, (jstring)ret, NULL);

    unsigned char* hexConverted = hexstr_to_char(str);

    char packet[strlen(str)];
    snprintf(packet, strlen(str), "%s", str);
    int size = strlen(packet);

    char pcap_errbuf[PCAP_ERRBUF_SIZE];
    pcap_errbuf[0] = '\0';
    pcap_t* pcap = pcap_open_live(dev, size, 0, 0, pcap_errbuf);

    if (pcap_errbuf[0]!='\0')
        fprintf(stderr, "SendPacket Error: %s", pcap_errbuf);

    if (!pcap)
        exit(1);

    if (pcap_inject(pcap, hexConverted, size) == -1)
    {
        pcap_perror(pcap,0);
        pcap_close(pcap);
        exit(1);
    }

    pcap_close(pcap);
}
