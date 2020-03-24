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
gnome-terminal --working-directory="/media/lex/Evo/Delft/Distributed/GlobeReplicator" --command "./gradlew :LookupService:run" & 
sleep 5
port=8081
for i in {1..10}
  do
  gnome-terminal --working-directory="/media/lex/Evo/Delft/Distributed/GlobeReplicator" --command "./gradlew :DistributedObject:run -Pdistributedobject.url=http://localhost:$port" &
  ((port++))
  sleep 1
done

# Give the system some time to ensure it's fully started and ready for testing
# Run the evaluation tests multiple times on this deployment
for iteration in {1..20}
do
./gradlew systemTest --rerun-tasks
done


