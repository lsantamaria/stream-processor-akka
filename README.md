# Stream processor

This is a simple application for processing streamed transactions.


### Requirements

- Java JDK 11

### Installation

Download and install Java JDK 11 from the [Java official webpage](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html). 

Set JAVA_HOME environment variable to point to this installation.

In order to compile and test the project, execute next command:


```
../mvnw clean install
```


### Run
Run the project using command below:
```
./mvnw exec:java -Dexec.mainClass=com.lsantamaria.StreamProcessorProgram
```
