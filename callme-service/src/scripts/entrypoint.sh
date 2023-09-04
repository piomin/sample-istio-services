#!/bin/bash

java -XX:CRaCCheckpointTo=/crac -jar /app/callme-service-1.1.0.jar&
sleep 10
jcmd /app/callme-service-1.1.0.jar JDK.checkpoint
sleep 10