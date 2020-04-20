#!/usr/bin/env bash
source .env
set -e
date
docker-compose down & docker volume prune -f & ./gradlew clean build --info

wait
docker-compose build
./test-em-all.bash start stop
date
#./test-em-all.bash start
