#!/usr/bin/env bash

docker rm -f $(docker ps -a -q --filter="ancestor=twilio-softphone")
docker build -t twilio-softphone .
docker run --log-driver=awslogs --log-opt awslogs-region=us-east-1 --log-opt awslogs-group=/ec2/softphone-dev --log-opt awslogs-create-group=true -p 8080:8080 -e ENV_NAME=dev -d --name softphone twilio-softphone