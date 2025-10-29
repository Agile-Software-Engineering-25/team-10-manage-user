#!/bin/bash
set -euo pipefail

if [ -z $1 ]; then
  echo "E: no token"
  exit 1
fi

no=1

ids=()

time for i in $(seq 51); do
  data="{\"firstName\": \"testactivated\",\"lastName\": \"test\",\"email\": \"activated$no@test.mail\",\"groups\": []}"

  echo "sending $no"
  time id=$(curl -s -X POST "http://localhost:8080/userapi/v1/user" \
    -H "Authorization: Bearer $1" \
    -H "Content-Type: application/json" \
    -d "$data" | jq .[0].id)

    echo $id
  
    ids+=($id)

    no=$((no + 1))
done

echo "-- cleaning --"

for id in "${ids[@]}"; do
  echo "deleting $id"
  trunc=$(echo $id | tr -d '"')
  curl -X DELETE "http://localhost:8080/userapi/v1/user/$trunc" -H "Authorization: Bearer $1"
done
