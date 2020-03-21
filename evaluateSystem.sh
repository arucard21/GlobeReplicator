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
# deploy the system with different amounts of Distributed Object replicas
for replicas in 2 3 5 10
do
  ./deploySystem.sh $replicas
  # Give the system some time to ensure it's fully started and ready for testing
  sleep 60
  # Run the evaluation tests multiple times on this deployment
  for iteration in {1..20}
  do
    ./gradlew systemTest --rerun-tasks
    # Give the system time to fully finish the system test and become ready for the next iteration of testing
    sleep 30
  done
  # Shut down the system and remove the EC2 instances
  ./gradlew terminateEC2Instances
done
