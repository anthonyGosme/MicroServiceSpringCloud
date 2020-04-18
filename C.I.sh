#!/usr/bin/env bash
set -e
date
./gradlew clean build --info
docker-compose build
docker volume prune -f
./test-em-all.bash start stop
date
#./test-em-all.bash start