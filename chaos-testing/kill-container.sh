#!/bin/bash
container=( "$@" )

# Seed random generator
RANDOM=$(date +%s)
while [ 1 ]
do
    rand=${container[$RANDOM % ${#container[@]}]}
    
    sleep 60s
    echo $(date)
    echo "Killing container ${rand}"
    docker stop ${rand}

    sleep 5s
    echo "Starting container ${rand}"
    docker start ${rand}
done
