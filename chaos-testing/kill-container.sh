#!/bin/bash
CONTAINERS=( $(docker ps -q --filter label=killable) )

# Seed random generator
RANDOM=$(date +%s)
while [ 1 ]
do
    rand=${CONTAINERS[$RANDOM % ${#CONTAINERS[@]}]}
    
    sleep 30s
    echo $(date)
    echo "Killing container ${rand}"
    docker stop ${rand}

    sleep 60s
    echo $(date)
    echo "Starting container ${rand}"
    docker start ${rand}
done
