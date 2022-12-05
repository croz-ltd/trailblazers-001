#How to setup Kafka development environment <7 minutes

Repository containing code samples from Trailblazers meetup #1

#Usage

## Prerequisites
1. Install Openshift local
2. Create project (trailblazers)
3. Install Strimzi operator

## Steps

Execute 01_crc/setup_env.ps1 to prepare local powershell environment

Login to CRC using kubeadmin. `crc console --credentials`
`oc login ....`

Apply yaml config files from folder `02_kafkaCluster` as ordered in folder.

### Optional: Install kafka-ui

Apply yaml config files from folder `03_kafkaui` as ordered in folder.

## Sample clients

Sample clients are provided in SampleClient folder. This is a maven project.

To run producer run `<SampleClient/src/main/java/hr/trailblazers/producer/SampleProducerMain.java`

To run client run  `SampleClient/src/main/java/hr/trailblazers/consumer/SampleConsumerMain.java`

Note: In order for clients to work `.kube/config` with appropriate privileges must be accessible during app runtime.
`oc login ....` will create one during execution.
