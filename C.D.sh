#!/usr/bin/env bash
set -e
date
./gradlew build
docker-compose build
./test-em-all.bash start
date
#./test-em-all.bash start