@echo off
    echo ping mark| ncat localhost 1233
ncat -z --recv-only localhost 1233