@echo off
    echo discover mark| ncat localhost 1233
ncat -z --recv-only localhost 1233