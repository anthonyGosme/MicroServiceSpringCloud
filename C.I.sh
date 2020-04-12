#!/usr/bin/env bash
set -e
date
./gradlew clean build --info
docker-compose build
./test-em-all.bash start stop
date
#./test-em-all.bash start