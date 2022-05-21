# easy-batch Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Easy Batch and Quarkus

This project aims to demonstrate the integration of Quarkus with Easy-Batch (https://github.com/j-easy/easy-batch/wiki).
Easy-Batch is intended to make simple batch jobs.
Quarkus provides the ability to package them natively.

This project uses the "exporting data"(https://github.com/j-easy/easy-batch/tree/master/easy-batch-tutorials/src/main/java/org/jeasy/batch/tutorials/intermediate/extract) tutorial from Easy-batch and the quarkus guides.

The "exporting data" tutorial is very simple. It consists of defining a "Tweet" data type and a Job to execute. The rest of the code is there only to facilitate the demonstration. So the initialization of the database is done with Spring.

In order to guarantee the best possible integrations with the Native mode, each dependency present in Easy-Batch having an equivalent in Quarkus, the Quarkus dependency has been preferred.

Only the job is dependent on Easy-Batch.

### Change in the Easy-Batch code

The Tweet class is carried over without any changes.

Launcher class is transformed to conform to quarkus command-mode-reference guide

Added "@QuarkusMain" annotation and "implements QuarkusApplication" interface

Using CDI and Microprofile Config to get the default database and path
```java
     @Inject
     AgroalDataSource dataSource;

     @ConfigProperty(name="batch.default.path", defaultValue="./tweets.csv")
     String path;
```

### Results
The Easy-Batch project offers no point of comparison. All the initialization and end phase being left to the developer who can thus use a simple java or spring code or any other means to create the environment necessary to start the job.

Quarkus offers enough documentation on the difference of execution in java or native mode not to have to come back to it.

What is interesting to measure is the execution time of the job itself and there easy-batch makes it possible to compare.

the execution of a job returns a jobReport which gives the duration of execution. the example only handles very little data and the tests were run 30 times so as not to be in one case with the postgresql caches and in the other without.

For only 2 lines of text to extract the difference is significant. Which suggests that this is relevant for large volumes.

java -jar target/quarkus-app/quarkus-run.jar foo.txt
```log
2022-05-21 15:26:58,203 INFO  [io.quarkus] (main) easy-batch 1.0.0-SNAPSHOT on JVM (powered by Quarkus 2.9.1.Final) started in 0.373s. 
2022-05-21 15:26:58,227 INFO  [io.quarkus] (main) Profile prod activated. 
2022-05-21 15:26:58,228 INFO  [io.quarkus] (main) Installed features: [agroal, cdi, hibernate-validator, jdbc-postgresql, narayana-jta, smallrye-context-propagation]
2022-05-21 15:26:58,252 INFO  [org.jea.bat.cor.job.BatchJob] (pool-5-thread-1) Job 'job' starting
2022-05-21 15:26:58,396 INFO  [org.jea.bat.cor.job.BatchJob] (pool-5-thread-1) Job 'job' started
2022-05-21 15:26:58,397 INFO  [org.jea.bat.cor.job.BatchJob] (pool-5-thread-1) Job 'job' stopping
2022-05-21 15:26:58,402 INFO  [org.jea.bat.cor.job.BatchJob] (pool-5-thread-1) Job 'job' finished with status COMPLETED in 142ms
2022-05-21 15:26:58,404 INFO  [org.jea.bat.tut.int.ext.Launcher] (main) Job Report:
===========
Name: job
Status: COMPLETED
Parameters:
	Batch size = 100
	Error threshold = N/A
	Jmx monitoring = false
	Batch scanning = false
Metrics:
	Start time = 2022-05-21T15:26:58.255808428
	End time = 2022-05-21T15:26:58.398506605
	Duration = 142ms
	Read count = 2
	Write count = 2
	Filter count = 0
	Error count = 0
2022-05-21 15:26:58,411 INFO  [io.quarkus] (main) easy-batch stopped in 0.007s
```
./target/easy-batch-1.0.0-SNAPSHOT-runner foo.txt
```log
2022-05-21 15:28:20,019 INFO  [io.quarkus] (main) easy-batch 1.0.0-SNAPSHOT native (powered by Quarkus 2.9.1.Final) started in 0.014s. 
2022-05-21 15:28:20,020 INFO  [io.quarkus] (main) Profile prod activated. 
2022-05-21 15:28:20,020 INFO  [io.quarkus] (main) Installed features: [agroal, cdi, hibernate-validator, jdbc-postgresql, narayana-jta, smallrye-context-propagation]
2022-05-21 15:28:20,021 INFO  [org.jea.bat.cor.job.BatchJob] (pool-6-thread-1) Job 'job' starting
2022-05-21 15:28:20,034 INFO  [org.jea.bat.cor.job.BatchJob] (pool-6-thread-1) Job 'job' started
2022-05-21 15:28:20,034 INFO  [org.jea.bat.cor.job.BatchJob] (pool-6-thread-1) Job 'job' stopping
2022-05-21 15:28:20,034 INFO  [org.jea.bat.cor.job.BatchJob] (pool-6-thread-1) Job 'job' finished with status COMPLETED in 13ms
2022-05-21 15:28:20,034 INFO  [org.jea.bat.tut.int.ext.Launcher] (main) Job Report:
===========
Name: job
Status: COMPLETED
Parameters:
	Batch size = 100
	Error threshold = N/A
	Jmx monitoring = false
	Batch scanning = false
Metrics:
	Start time = 2022-05-21T15:28:20.021405
	End time = 2022-05-21T15:28:20.034886
	Duration = 13ms
	Read count = 2
	Write count = 2
	Filter count = 0
	Error count = 0
2022-05-21 15:28:20,035 INFO  [io.quarkus] (main) easy-batch stopped in 0.000s
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Pnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Pnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/easy-batch-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

