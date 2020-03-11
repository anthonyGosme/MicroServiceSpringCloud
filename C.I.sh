#!/usr/bin/env bash
date
./gradlew clean build
docker-compose build
./test-em-all.bash start stop
date