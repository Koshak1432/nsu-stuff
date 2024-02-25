#!/bin/bash

docker compose up -d

WORKER_COUNT=$(grep WORKER_COUNT .env | tr -d '\r' | cut -d '=' -f 2)

for ((i = 1; i <= ${WORKER_COUNT}; i++));
do
  old_name="crackhash_worker_$i"
  new_name="crackhash-worker-$i"
  docker rename $old_name $new_name
done