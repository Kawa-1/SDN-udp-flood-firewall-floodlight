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

counter=1

while [ "$counter" -le "$arg3" ]; do
    packit_command="sudo packit -i $interface -t UDP -d 10.0.0.5 -c $arg2 -w 0"
    echo "Running command: $packit_command"
    # Uncomment the line below to execute the packit command
    $packit_command
    sleep 0.1
    ((counter++))
done