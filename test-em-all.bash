#!/usr/bin/env bash
#
# ./gradlew clean build
# docker-compose build
# docker-compose up -d
#
# Sample usage:
#
#   HOST=localhost PORT=7000 ./test-em-all.bash
#
#: ${HOST=minikube.me}
#: ${PORT=443}:
: ${HOST=192.168.99.101}
: ${PORT=8444}
: ${PROD_ID_REVS_RECS=2}
: ${PROD_ID_NOT_FOUND=14}
: ${PROD_ID_NO_RECS=114}
: ${PROD_ID_NO_REVS=214}
: ${NAMESPACE=hands-on}

RED='\033[0;31m'
GREEN='\033[0;32m'
OTHER='\033[0;33m'
NC='\033[0m' # No Color

function assertCurl() {

  local expectedHttpCode=$1
  local curlCmd="$2 -i"
  echo curl
  result=$(eval $curlCmd)
  status=$(echo "$result" | head -n 2 | tail -n 1)
  httpCode=$(echo "$result" | head -n 1 | awk '{print  $2}')

  RESPONSE=$(echo "$result" | tail -n 1)

  # RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"
  #echo "==$result"
  #echo "--$status"
  #echo "++$httpCode"
  #echo "**$RESPONSE"

  if [ "$httpCode" = "$expectedHttpCode" ]; then
    if [ "$httpCode" = "200" ]; then
      printf "${GREEN}OK: $httpCode${NC}\n"
    else
      printf "${GREEN}OK: $httpCode${NC}\n $status => $RESPONSE\n"
    fi
    return 0
  else
    printf "${RED}KO: got $httpCode and expected $expectedHttpCode${NC}\n"
    echo "- Failing command: $curlCmd"
    echo "- response status:  $status"
    echo "- Response Body: $RESPONSE"
    return 1
  fi
}

function assertEqual() {

  local expected=$1
  local actual=$2

  if [ "$actual" = "$expected" ]; then
    printf "${GREEN} OK ${NC} (actual value: $actual)\n"
  else
    printf "${RED}Test FAILED, EXPECTED VALUE: $expected, ACTUAL VALUE: $actual, WILL ABORT${NC}\n"
    return 1
  fi
}

function testUrl() {
  url=$@
  if $url --connect-timeout 2 --max-time 10 -ks -f -o /dev/null; then
    return 0
  else
    return 1
  fi
}

function testCompositeCreated() {

  echo "# Expect that the Product Composite for productId $PROD_ID_REVS_RECS has been created with three recommendations and three reviews"
  if ! assertCurl 200 "curl $AUTH -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS -s"; then
    echo -n "FAIL"
    return 1
  fi

  set +e
  assertEqual "$PROD_ID_REVS_RECS" $(echo $RESPONSE | jq .productId)
  if [ "$?" -eq "1" ]; then return 1; fi

  assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
  if [ "$?" -eq "1" ]; then return 1; fi

  assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")
  if [ "$?" -eq "1" ]; then return 1; fi

  set -e
}

function waitForMessageProcessing() {
  echo "Wait for messages to be processed... "

  # Give background processing some time to complete...
  sleep 1

  n=0
  until testCompositeCreated; do
    n=$((n + 1))
    if [[ $n == 100 ]]; then
      echo " Give up"
      exit 1
    else
      sleep 6
      echo -n ", retry #$n "
    fi
  done
  echo "All messages are now processed!"
}

function waitForService() {
  url=$@
  echo -n "Wait for: $url... "
  n=0
  until testUrl $url; do
    n=$((n + 1))
    if [[ $n == 20 ]]; then
      echo " Give up"
      exit 1
    else
      sleep 6
      echo -n ", retry #$n "
    fi
  done
}

function recreateComposite() {
  local productId=$1
  local composite=$2

  assertCurl 200 "curl $AUTH -X DELETE -k https://$HOST:$PORT/product-composite/${productId} -s"
  curl -X POST -k https://$HOST:$PORT/product-composite -H "Content-Type: application/json" -H "Authorization: Bearer $ACCESS_TOKEN" --data "$composite"
}

function setupTestdata() {

  body="{\"productId\":$PROD_ID_NO_RECS"
  body+=',"name":"product name A","weight":100, "reviews":[
    {"reviewId":1,"author":"author 1","subject":"subject 1","content":"content 1"},
    {"reviewId":2,"author":"author 2","subject":"subject 2","content":"content 2"},
    {"reviewId":3,"author":"author 3","subject":"subject 3","content":"content 3"}
]}'
  recreateComposite "$PROD_ID_NO_RECS" "$body"

  body="{\"productId\":$PROD_ID_NO_REVS"
  body+=',"name":"product name B","weight":200, "recommendations":[
    {"recommendationId":1,"author":"author 1","rate":1,"content":"content 1"},
    {"recommendationId":2,"author":"author 2","rate":2,"content":"content 2"},
    {"recommendationId":3,"author":"author 3","rate":3,"content":"content 3"}
]}'
  recreateComposite "$PROD_ID_NO_REVS" "$body"

  body="{\"productId\":$PROD_ID_REVS_RECS"
  body+=',"name":"product name C","weight":300, "recommendations":[
        {"recommendationId":1,"author":"author 1","rate":1,"content":"content 1"},
        {"recommendationId":2,"author":"author 2","rate":2,"content":"content 2"},
        {"recommendationId":3,"author":"author 3","rate":3,"content":"content 3"}
    ], "reviews":[
        {"reviewId":1,"author":"author 1","subject":"subject 1","content":"content 1"},
        {"reviewId":2,"author":"author 2","subject":"subject 2","content":"content 2"},
        {"reviewId":3,"author":"author 3","subject":"subject 3","content":"content 3"}
    ]}'
  recreateComposite 1 "$body"

}

function testCircuitBreaker() {
  printf "${OTHER}\n\nCircuit breaker \n=============\n${NC}\n"

  echo "Start Circuit Breaker tests!"

  # Assume we are using Docker Compose if we are running on localhost, otherwise Kubernetes
  if [ "$HOST" = "localhost" ]; then
    EXEC="docker run --rm -it --network=my-network alpine"
  else
    echo "Restarting alpine-client..."
    local ns=$NAMESPACE
    if kubectl -n $ns get pod alpine-client >/dev/null; then
      kubectl -n $ns delete pod alpine-client --grace-period=1
    fi
    kubectl -n $ns run --restart=Never alpine-client --image=alpine --command -- sleep 600
    echo "Waiting for alpine-client to be ready..."
    kubectl -n $ns wait --for=condition=Ready pod/alpine-client
    EXEC="kubectl -n $ns exec alpine-client --"
  fi

  echo "# First, use the health - endpoint to verify that the circuit breaker is closed"
  ###assertEqual "CLOSED" "$($EXEC wget product-composite:8080/actuator/health -qO - | jq -r .details.productCircuitBreaker.details.state)"
  #assertEqual "CLOSED" "$($EXEC wget product-composite:8080/actuator/health -qO - | jq -r .components.circuitBreakers.details.product.details.state)"
  assertEqual "CLOSED" "$($EXEC wget product-composite/actuator/health -qO - | jq -r .components.circuitBreakers.details.product.details.state)"

  echo "# Open the circuit breaker by running three slow calls in a row, i.e. that cause a timeout exception"
  echo "# Also, verify that we get 500 back and a timeout related error message"
  for ((n = 0; n < 3; n++)); do
    assertCurl 500 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS?delay=3 $AUTH -s"
    message=$(echo $RESPONSE | jq -r .message)
    assertEqual "Did not observe any item or terminal signal within 2000ms" "${message:0:57}"
  done

  echo "#Verify that the circuit breaker now is open by running the slow call again, verify it gets 200 (or 500) back, i.e. fail fast works, and a response from the fallback method."
  echo "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS?delay=3 $AUTH -s"
  assertCurl 500 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS?delay=3 $AUTH -s"
  ### assertEqual "Fallback product2" "$(echo "$RESPONSE")"

  echo "# Also, verify that the circuit breaker is open by running a normal call, verify it also gets 200 back and a response from the fallback method."
  assertCurl 500 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS $AUTH -s"
  # assertEqual "Fallback product2" "$(echo "$RESPONSE" | jq -r .name)"

   echo "# Verify that a 404 (Not Found) error is returned for a non existing productId ($PROD_ID_NOT_FOUND) from the fallback method."
  assertCurl 500 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_NOT_FOUND $AUTH -s"
  #  assertEqual "Product Id: $PROD_ID_NOT_FOUND not found in fallback cache!" "$(echo $RESPONSE | jq -r .message)"

  echo "Wait for the circuit breaker to transition to the half open state (i.e. max 10 sec)"
  echo "Will sleep for 10 sec waiting for the CB to go Half Open..."
  sleep 10

  echo "Verify that the circuit breaker is in half open state"
  ##echo "$($EXEC wget product-composite:8080/actuator/health -qO - | jq -r .components.circuitBreakers.details.product.details.state)"
   assertEqual "HALF_OPEN" "$($EXEC wget product-composite/actuator/health -qO - | jq -r .components.circuitBreakers.details.product.details.state)"


  assertEqual "HALF_OPEN" "$(curl http://localhost:7000/actuator/health | jq -r .components.circuitBreakers.details.product.details.state)"


  echo "# Close the circuit breaker by running three normal calls in a row"
  echo "# Also, verify that we get 200 back and a response based on information in the product database"
  for ((n = 0; n < 3; n++)); do
    assertCurl 200 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS $AUTH -s"
    assertEqual "product name C" "$(echo "$RESPONSE" | jq -r .name)"
  done

  echo "# Verify that the circuit breaker is in closed state again"


    assertEqual "CLOSED" "$($EXEC wget product-composite/actuator/health -qO - | jq -r .details.productCircuitBreaker.details.state)"

      echo "#Verify that the expected state transitions happened in the circuit breaker
    assertEqual "CLOSED_TO_OPEN"      "$($EXEC wget product-composite/actuator/circuitbreakerevents/product/STATE_TRANSITION -qO - | jq -r .circuitBreakerEvents[-3].stateTransition)"
    assertEqual "OPEN_TO_HALF_OPEN"   "$($EXEC wget product-composite/actuator/circuitbreakerevents/product/STATE_TRANSITION -qO - | jq -r .circuitBreakerEvents[-2].stateTransition)"
    assertEqual "HALF_OPEN_TO_CLOSED" "$($EXEC wget product-composite/actuator/circuitbreakerevents/product/STATE_TRANSITION -qO - | jq -r .circuitBreakerEvents[-1].stateTransition)"

    echo "# Shutdown the client pod if we are using Kubernetes, i.e. not runnig on localhost.
    if [ "$HOST" != "localhost" ]
    then
        kubectl -n $ns delete pod alpine-client --grace-period=1
    fi


 # assertEqual "CLOSED" "$(curl http://localhost:7000/actuator/health | jq -r .components.circuitBreakers.details.product.details.state)"

#  echo "# Verify that the expected state transitions happened in the circuit breaker"
 # assertEqual '"CLOSED_TO_OPEN"' "$(curl http://localhost:7000/actuator/circuitbreakerevents/product/STATE_TRANSITION | jq '.circuitBreakerEvents[-3].stateTransition')"
 # assertEqual '"OPEN_TO_HALF_OPEN"' "$(curl http://localhost:7000/actuator/circuitbreakerevents/product/STATE_TRANSITION | jq '.circuitBreakerEvents[-2].stateTransition')"
 # assertEqual '"HALF_OPEN_TO_CLOSED"' "$(curl http://localhost:7000/actuator/circuitbreakerevents/product/STATE_TRANSITION | jq '.circuitBreakerEvents[-1].stateTransition')"
}

set -e

echo "Start Tests:" $(date)

echo "HOST=${HOST}"
echo "PORT=${PORT}"

if [[ $@ == *"start"* ]]; then
  echo "Restarting the test environment..."
  echo "$ docker-compose down --remove-orphans"
  docker volume prune -f
  docker-compose down --remove-orphans
  echo "$ docker-compose up -d"
  docker-compose up -d
fi

if [[ $@ == *"skiptest"* ]]; then
  echo "We are done starting"
  docker ps -a
  exit
fi
waitForService curl -k https://$HOST:$PORT/actuator/health

ACCESS_TOKEN=$(curl -k https://writer:secret@$HOST:$PORT/oauth/token -d grant_type=password -d username=anthony -d password=password -s | jq .access_token -r)
AUTH="-H \"Authorization: Bearer $ACCESS_TOKEN\""

setupTestdata
waitForMessageProcessing

echo -e "\n# Verify that a normal request works, expect three recommendations and three reviews"
assertCurl 200 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS $AUTH -s"
assertEqual "$PROD_ID_REVS_RECS" $(echo $RESPONSE | jq .productId)
assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")

echo -e "\n\n Verify that a 404 (Not Found) error is returned for a non existing productId ($PROD_ID_NOT_FOUND)"
assertCurl 404 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_NOT_FOUND $AUTH -s"

echo -e "\n\nVerify that no recommendations are returned for productId $PROD_ID_NO_RECS"
assertCurl 200 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_NO_RECS $AUTH -s"
assertEqual "$PROD_ID_NO_RECS" $(echo $RESPONSE | jq .productId)
assertEqual 0 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 3 $(echo $RESPONSE | jq ".reviews | length")

echo -e "\n\nVerify that no reviews are returned for productId $PROD_ID_NO_REVS"
assertCurl 200 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_NO_REVS $AUTH -s"
assertEqual $PROD_ID_NO_REVS $(echo $RESPONSE | jq .productId)
assertEqual 3 $(echo $RESPONSE | jq ".recommendations | length")
assertEqual 0 $(echo $RESPONSE | jq ".reviews | length")

echo -e "\n\nVerify that a 422 (Unprocessable Entity) error is returned for a productId that is out of range (-1)"
assertCurl 422 "curl -k https://$HOST:$PORT/product-composite/-1 $AUTH -s"
assertEqual "\"can't get Invalid productId: -1\"" "$(echo $RESPONSE | jq .message)"

echo -e "\n\n Verify that a 400 (Bad Request) error error is returned for a productId that is not a number, i.e. invalid format"
assertCurl 400 "curl -k https://$HOST:$PORT/product-composite/invalidProductId $AUTH -s"
assertEqual "\"Type mismatch.\"" "$(echo $RESPONSE | jq .message)"

echo -e "\n\n Verify that a request without access token fails on 401, Unauthorized"
assertCurl 401 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS -s"

echo -e "\n Verify that the reader - client with only read scope can call the read API but not delete API."
READER_ACCESS_TOKEN=$(curl -k https://reader:secret@$HOST:$PORT/oauth/token -d grant_type=password -d username=anthony -d password=password -s | jq .access_token -r)
READER_AUTH="-H \"Authorization: Bearer $READER_ACCESS_TOKEN\""

assertCurl 200 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS $READER_AUTH -s"
assertCurl 403 "curl -k https://$HOST:$PORT/product-composite/$PROD_ID_REVS_RECS $READER_AUTH -X DELETE -s"

testCircuitBreaker

printf "${GREEN}\nEnd, all tests OK:" $(date)

#if [[ $@ == *"stop"* ]]
#then
#    echo "Stopping the test environment..."
#    echo "$ docker-compose down --remove-orphans"
#    docker-compose down --remove-orphans
#fi

echo "End:" $(date)
