@echo off
    echo publish| ncat localhost 1232
ncat -z --recv-only localhost 1232