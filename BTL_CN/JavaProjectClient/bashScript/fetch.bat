@echo off
    echo fetch codeR.R| ncat localhost 1231
ncat -z --recv-only localhost 1231