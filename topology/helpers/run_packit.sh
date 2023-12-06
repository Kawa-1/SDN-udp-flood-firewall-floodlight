#!/usr/bin/bash


trap 'echo "Exiting..."; exit' SIGINT

while true; do
    packit -t UDP -d 10.0.0.2 -c 1000 -w 0
done
