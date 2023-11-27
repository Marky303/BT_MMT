@echo off
    echo publish ahc abc| ncat localhost 1232
ncat -z --recv-only localhost 1232