@echo off
    echo discover allFile| ncat localhost 1233
ncat -z --recv-only localhost 1233