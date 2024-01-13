#!/bin/bash

if [ "$#" -lt 3 ]; then
  echo "Usage: $0 <source> <packet size> <nof packets>"
  exit 1
fi

arg1="$1"
arg2="$2"
arg3="$3"

interface="h1-eth1"

if [[ "$arg1" == *h2* ]]; then
    interface="h2-eth0"
elif [[ "$arg1" == *h3* ]]; then
    interface="h3-eth0"
elif [[ "$arg1" == *h4* ]]; then
    interface="h4-eth0"
elif [[ "$arg1" == *h5* ]]; then
    interface="h5-eth0"
fi
