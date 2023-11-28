@echo off
    echo fetch codeR.R| ncat localhost 1232
ncat -z --recv-only localhost 1232