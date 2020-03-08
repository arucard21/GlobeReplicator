## Globe Replicator
Experimental implementation of replication mechanism in Globe distributed system

### Required Configuration
In order to deploy an EC2 instance with Gradle, the following need to be configured:
- Your credentials must be available from a [default location](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/credentials.html) (e.g. ~/.aws/credentials) with a profile named "default"
- You must have `ssh` and `scp` installed on your local machine
- The public key for your local SSH instance must be [imported into AWS EC2 configuration](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html#how-to-generate-your-own-key-and-import-it-to-aws) as KeyPair with the name "globeReplicator"
- An EC2 Security Group with name "default" must be available and configured to allow inbound TCP traffic on port 22 (for SSH)

Since we use system tools with Gradle, this will work best on a Linux system with these tools installed.

### Usage
In order to deploy and start the Lookup Service, use the following Gradle task:
```
./gradlew deployLookupService
```
This will build the project, create the executable jar file, create an EC2 instance, upload the jar file to the EC2 instance and launch the Lookup Service application.

In order to deploy and start a Distributed Object, use the following Gradle task:
```
./gradlew deployDistributedObject
```
This will build the project, create the executable jar file, create an EC2 instance, upload the jar file to the EC2 instance and launch the Distributed Object application.

The EC2 instances can be terminated through Gradle with:
```
./gradlew terminateEC2Instances
```
This will terminate all EC2 instances that were created. The instance IDs of the created EC2 instances are written to a file named `instanceIds`. This file is removed after the EC2 instances are terminated.

#### Quick Start
There is a Linux shell script to deploy the entire distributed system with a configurable amount of replicas for the Distributed Object. This can be called with:
```
./deploySystem <number of replicas for Distributed Object>
``` 
This will deploy the Lookup Service and deploy the configured amount of Distributed Object instances. For example, `./deploySystem 3` will deploy 1 Lookup Service instance and 3 Distributed Object instances for a total of 4 EC2 instances. 
Since this is a Bash-based script, it requires `bash` to be installed on the local machine.