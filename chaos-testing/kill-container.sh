#!/bin/bash
container=( "$@" )

# Seed random generator
RANDOM=$(date +%s)
while [ 1 ]
do
    rand=${container[$RANDOM % ${#container[@]}]}
    
    sleep 30s
    echo $(date)
    echo "Killing container ${rand}"
    docker stop ${rand}

    sleep 30s
    echo $(date)
    echo "Starting container ${rand}"
    docker start ${rand}
done
