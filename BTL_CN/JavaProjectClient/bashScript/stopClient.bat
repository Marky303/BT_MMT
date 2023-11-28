@echo off
    echo stop| ncat localhost 1232
ncat -z --recv-only localhost 1232