# Globe Replicator
Experimental implementation of replication mechanism in Globe distributed system

In order to deploy an EC2 instance with Gradle, the following need to be configured:
- Your credentials must be available from a [default location](https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/credentials.html) (e.g. ~/.aws/credentials) with a profile named "default"
- You must have `ssh` and `scp` installed on your local machine
- The public key for your local SSH instance must be [imported into AWS EC2 configuration](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html#how-to-generate-your-own-key-and-import-it-to-aws) as KeyPair with the name "globeReplicator"
- An EC2 Security Group with name "default" must be available and configured to allow inbound TCP traffic on port 22 (for SSH)

Since we use system tools with Gradle, this will work best on a Linux system with these tools installed.

Gradle will deploy a t2.micro instance with Ubuntu Server 19.10 to the us-east-1 region (N. Virginia)
You can deploy the EC2 instance with:
`./gradlew deployEC2Instance --info`

The instance ID of this deployed EC2 instance can be found in the AWS console or the log output of the Gradle deployEC2Instance task (which is only shown if run with `--info` parameter). This instance ID is needed for the other Gradle tasks.

The instance can be terminated through Gradle with:
`./gradlew terminateEC2Instance -PinstanceID=<instanceID of deployed EC2 instance>`

In order to deploy and start the main instance application, use the following Gradle task:
`./gradlew deployApplication -PinstanceID=<instanceID of deployed EC2 instance>`
This will build the project, create the executable jar file, upload it to the EC2 instance and launch the Globe Replicator application.
