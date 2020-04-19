#!/usr/bin/env bash
set -e
date
./gradlew clean build -x test
docker-compose build
./test-em-all.bash start skiptest
date
#./test-em-all.bash start
