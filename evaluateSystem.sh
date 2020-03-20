#!/usr/bin/env bash
# move previous measurements
if [ -f ./DistributedObject/responseTimesScalability.csv ]
then
  mv -v ./DistributedObject/responseTimesScalability.csv ./DistributedObject/responseTimesScalability_prev.csv
fi
if [ -f ./DistributedObject/responseTimesConcurrency.csv ]
then
  mv -v ./DistributedObject/responseTimesConcurrency.csv ./DistributedObject/responseTimesConcurrency_prev.csv
fi
# deploy the system and run the evaluation multiple times
for replicas in 2 3 5 10
do
  ./deploySystem.sh $replicas
  sleep 60
# Assumes lookup server id is at the first line of the file
  lookupID=$(head -n 1 instanceIds) 
  lookupDNS=$(aws ec2 describe-instances \
	--instance-ids $lookupID \
	--query "Reservations[*].Instances[*].[PublicDnsName]" \
	--output text)
  for iteration in {1..20}
  do
    ./gradlew systemTest --rerun-tasks -Plookupservice.url=http://$lookupDNS:8080
  done
  sleep 30
  ./gradlew terminateEC2Instances
done
