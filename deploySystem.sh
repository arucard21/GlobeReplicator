#!/usr/bin/env bash
if [ -z $1 ] 
then
	echo "Please provide the number of replicas as argument, e.g. ./deploySystem 3"
	exit -1
fi

echo "Deploying Lookup Service "
./gradlew deployLookupService
for i in $(seq 1 $1)
do
	echo "Deploying Distributed Object $i"
	sleep 1
	./gradlew deployDistributedObject
done
