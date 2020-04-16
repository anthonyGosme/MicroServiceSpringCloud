#!/usr/bin/env bash

  ./gradlew clean build -x test
  echo usage ./C.I.sh docker-compose-custo
  yaml=docker-compose.yaml

  if [ "$1" ]; then
  yaml=$1
  fi
 if [  "$2" = "down" ]
  then
     docker-compose -f $yaml down
     exit
  fi
  echo $yaml
  set -e
  date
  docker-compose -f $yaml build
  echo "Restarting the test environment..."
  echo "$ docker-compose down --remove-orphans"
  docker-compose -f $yaml down --remove-orphans
  echo "$ docker-compose up -d" -f $yaml
  docker-compose -f $yaml up -d
  docker-compose -f $yaml ps
  sleep 5
  date
  watch docker-compose -f $yaml ps

#./test-em-all.bash start