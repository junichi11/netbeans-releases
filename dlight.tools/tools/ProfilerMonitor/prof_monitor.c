#include <errno.h>
#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>

#include "instruments.h"

#ifndef CLK_TCK
#define CLK_TCK (sysconf(_SC_CLK_TCK))
#endif

static int user_term = 0;

void terminate(int i) {
    user_term = 1;
}

int getmsg(int msqid, void* buf, int size, int type) {
    int got = 0;
    while (msgrcv(msqid, buf, size, type, IPC_NOWAIT) >= 0) {
        ++got;
    }
    if (user_term || errno != ENOMSG) {
        if (user_term) {
            struct ctrlmsg ctrl = {CTRLMSG, 0xf, 0xf};
            ctrl.action = 0;
            msgsnd(msqid, &ctrl, sizeof(ctrl) - sizeof(ctrl.type), IPC_NOWAIT);
        }
        exit(0);
    }
    return got;
}

int main(int argc, char** argv) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s flags pid\n", argv[0]);
        fprintf(stderr, "Flags:\n");
        fprintf(stderr, "\t-c  CPU monitoring\n");
        fprintf(stderr, "\t-m  memory monitoring\n");
        fprintf(stderr, "\t-s  synchronization monitoring\n");
        return 1;
    }

    long pid = 0;
    int monitor_cpu = 0;
    int monitor_mem = 0;
    int monitor_sync = 0;
	int i, j;
    for (i = 1; i < argc; ++i) {
        if (argv[i][0] == '-') {
            for (j = 1; argv[i][j]; ++j) {
                switch (argv[i][j]) {
                    case 'c':
                        monitor_cpu = 1;
                        break;
                    case 'm':
                        monitor_mem = 1;
                        break;
                    case 's':
                        monitor_sync = 1;
                        break;
                    default:
                        fprintf(stderr, "Unknown flag %c\n", argv[i][j]);
                        return 1;
                }
            }
        } else if (!pid) {
            pid = strtol(argv[i], NULL, 10);
        } else {
            fprintf(stderr, "Ignoring extra pid %s\n", argv[i]);
        }
    }

    if (monitor_cpu + monitor_mem + monitor_sync == 0) {
        fprintf(stderr, "No flags specified, exiting\n");
        return 1;
    }

    if (pid == 0) {
        fprintf(stderr, "No pid specified, exiting\n");
        return 1;
    }

    int msqid = -1;

    if (pid) {
        // Get the message queue id for the "pid"
        msqid = msgget((int) pid, IPC_CREAT | 0666);
    }
    if (msqid < 0) {
        fprintf(stderr, "Can not create IPC channel to process %ld\n", pid);
        return -2;
    }

    struct timespec res;
    long resolution = DEF_RES;
    if (clock_getres(CLOCK_REALTIME, &res) == 0) {
        resolution = (resolution > res.tv_nsec) ? resolution : res.tv_nsec;
    }
    long per_sec = 1000000000L / (GRANULARITY * resolution);

    signal(SIGINT, terminate);

    struct memmsg membuf = {MEMMSG, 0};
    struct syncmsg syncbuf = {SYNCMSG, 0, 0};
    struct cpumsg cpubuf = {CPUMSG, 0, 0};
    struct ctrlmsg ctrl = {CTRLMSG, 0xf, 0xf};
    struct ackrequestmsg ackrequest = {ACKREQUEST};
    struct ackreplymsg ackreply = {ACKREPLY};
    if (msgsnd(msqid, &ctrl, sizeof(ctrl) - sizeof(ctrl.type), IPC_NOWAIT) < 0) {
        fprintf(stderr, "Handshake with process %ld failed\n", pid);
        return -4;
    }

    int msgwant;
    int silence = 0;
    while (msgwant = monitor_cpu + monitor_mem + monitor_sync) {
        int msggot = 0;
        int ack = getmsg(msqid, &ackrequest, sizeof(ackrequest) - sizeof(ackrequest.type), ACKREQUEST);
        if (monitor_sync) {
            if (getmsg(msqid, &syncbuf, sizeof(syncbuf) - sizeof(syncbuf.type), SYNCMSG)) {
                ++msggot;
                printf("sync: %lf\t%d\n",
                        ((double) syncbuf.lock_ticks) / per_sec,
                        syncbuf.thr_count);
                fflush(stdout);
            }
        }
        if (monitor_mem) {
            if (getmsg(msqid, &membuf, sizeof(membuf) - sizeof(membuf.type), MEMMSG)) {
                ++msggot;
                printf("mem: %d\n", membuf.heapused);
                fflush(stdout);
            }
        }
        if (monitor_cpu) {
            if (getmsg(msqid, &cpubuf, sizeof(cpubuf) - sizeof(cpubuf.type), CPUMSG)) {
                ++msggot;
                printf("cpu: %d\t%d\n", cpubuf.user, cpubuf.sys);
                fflush(stdout);
            }
        }
        if (ack) {
            msgsnd(msqid, &ackreply, sizeof(ackreply) - sizeof(ackreply.type), IPC_NOWAIT);
            break;
        }
        if (msggot) {
            silence = 0;
        } else {
            if (++silence > 5) {
                // looks like agent has been killed and queue remains alive
                struct msqid_ds msbuf;
                msgctl(msqid, IPC_RMID, &msbuf);
                fprintf(stderr, "Communication with process %ld has been lost\n", pid);
                return -4;
            }
        }
        if (msggot < msgwant) {
            struct failmsg fbuf;
            if (msgrcv(msqid, &fbuf, sizeof (fbuf) - sizeof (fbuf.type), FAILMSG, IPC_NOWAIT) == 0) {
                // something happened in agent
                if (fbuf.type == MEMMSG) {
                    monitor_mem = 0;
                    printf("mem: failure!\n");
                }
                if (fbuf.type == CPUMSG) {
                    monitor_cpu = 0;
                    printf("cpu: failure!\n");
                }
                if (fbuf.type == SYNCMSG) {
                    monitor_sync = 0;
                    printf("sync: failure!\n");
                }
            } else {
                // give agent some more time to send messages
                sleep(1);
            }
        }
    }


    return 0;
}
