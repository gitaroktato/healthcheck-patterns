#!/bin/bash

# Seed random generator
RANDOM=$(date +%s)

while true
do
    echo "Chosing a pod to kill..."
    PODS=( $(kubectl get pods -n test -l killable=true -o custom-columns=NAME:metadata.name --no-headers) )
    POD_COUNT=${#PODS[@]}

    RANDOM_ID=${PODS[$RANDOM % ${#PODS[@]}]}
    echo "Killing pod ${RANDOM_ID}"
    kubectl delete pod -n test $RANDOM_ID

    sleep 15s
done