@echo off
    echo discover| ncat localhost 1233
ncat -z --recv-only localhost 1233
