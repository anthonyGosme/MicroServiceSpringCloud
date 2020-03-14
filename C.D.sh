#!/usr/bin/env bash
date
./gradlew build
docker-compose build
./test-em-all.bash start
date
#./test-em-all.bash start