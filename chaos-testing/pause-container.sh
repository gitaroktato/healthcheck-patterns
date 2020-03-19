#!/bin/bash
container=( "$@" )

# Seed random generator
RANDOM=$(date +%s)
while [ 1 ]
do
    rand=${container[$RANDOM % ${#container[@]}]}
    
    sleep 15s
    echo $(date)
    echo "Pausing container ${rand}"
    docker pause ${rand}

    sleep 30s
    echo "Unpausing container ${rand}"
    docker unpause ${rand}
done
