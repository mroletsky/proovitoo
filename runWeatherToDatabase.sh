#!/bin/bash
cd /mnt/c/users/marku/IdeaProjects/proovitoo # path to project
IP="192.168.1.183" # ip address of device that has the database stored on it
mvn exec:java -Dexec.args="$IP" # execute