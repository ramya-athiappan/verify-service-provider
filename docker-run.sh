#!/usr/bin/env bash

cd $(dirname "${BASH_SOURCE[0]}")

./gradlew clean
./gradlew distZip -Pversion=local
docker build -t vsp:latest -f run.Dockerfile .
echo "vsp:latest"
