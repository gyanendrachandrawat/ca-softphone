#!/usr/bin/env bash

docker rm -f $(docker ps -a -q --filter="ancestor=twilio-softphone")
docker build -t twilio-softphone .
docker run -p 80:8080 -e ENV_NAME=dev -d -n softphone twilio-softphone

