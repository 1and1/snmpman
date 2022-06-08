![SNMPMAN](http://1and1.github.io/snmpman/images/snmpman.png
 "SNMPMAN")
============
[![Java CI with Maven](https://github.com/1and1/snmpman/actions/workflows/maven.yml/badge.svg)](https://github.com/1and1/snmpman/actions/workflows/maven.yml)
[![javadoc](https://javadoc.io/badge2/com.oneandone/snmpman/javadoc.svg)](https://javadoc.io/doc/com.oneandone/snmpman)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.oneandone/snmpman/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.oneandone/snmpman) 
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

The `SNMPMAN` is a command-line application that simulates `SNMP`-capable devices and was developed to be usable during unit testing.
It may be used to test traffic monitoring applications that execute `SNMP` queries. Due to the lack of
flexibility in already existing alternatives, it was developed by the **IONOS SE** to improve flexibility. Therefore the `SNMPMAN`
is highly extendable and will be enhanced over time.

Multiple `SNMP` agents may be started at once and will run in parallel. The agents running in parallel
will return the data queried and save it to a textual walk result file. This file will be created by querying a real device. It is also possible to query several 
device types at once. The query configuration may contain policies on
how to dynamically change the query responses. E.g. the response of an
[ifInOctets](http://tools.cisco.com/Support/SNMP/do/BrowseOID.do?objectInput=ifInOctets&translate=Translate&submitValue=SUBMIT")
may be incremented with each query to simulate traffic. Several variable modifiers are already in existence for different
data types.

Usage
============
`SNMPMAN` can either be used as

* Java library
* Standalone command line program
* Docker image

Java library usage
-----------

You can include the `SNMPMAN` as a Maven dependency from [Maven Central]().

```xml
<dependency>
    <groupId>com.oneandone</groupId>
    <artifactId>snmpman</artifactId>
    <version>2.1.2</version>
</dependency>
```

In order to start and stop the simulation use this code snippet as an example:

```Java
/* 
 * creates a new instance of the SNMPMAN with the specified configuration file 
 * and executes all agents 
 */
Snmpman snmpman = Snmpman.start(new File("configuration.yaml"));
/* ... do something with the agents */

/* stop the SNMPMAN and all started agents */
snmpman.stop();
```

Standalone commandline usage
-----------
For standalone commandline usage, you need to pass a YAML file as a configuration.

The commandline options are:

```
 -c (--configuration) DATEI : the path to the configuration YAML
 -h (--help)                : print the help message (Vorgabe: false)
```

A YAML configuration specifying one SNMP agent, binding to IP 127.0.0.1, UDP port 10000 and
SNMP community 'public' is given here:

```
- name: "example1"
  device: "/opt/snmpman/etc/devices/ios.yaml"
  walk: "/opt/snmpman/etc/walk/example1.walk"
  ip: "127.0.0.1"
  port: 10000
  community: public
```

The walk can be a SNMP walk extracted with the 'snmpwalk' command line program with the options `-On`.

Docker usage
-----------
There's an experimental docker image available that can be used to simulate SNMP agents.

Available configuration options:

* `SNMPMAN_CONFIG`: The location of the default configuration can be changed using the env var `SNMPMAN_CONFIG`, it defaults to a
sample config at `/snmpman/etc/configuration.yaml`.

Sample call mounting a walk inside the container and binding the container towards the standard SNMP port 161:

```
docker run -v ABSOLUTE/PATH/TO/walk.txt:/snmpman/etc/walk/example1.walk -p 161:10000/udp stephanfuhrmannionos/snmpman
```

Installation and building
============
### Prerequisites
  * `Java JDK 9+`
  * `Apache Maven 3.5.2`

### Building a fat Jar

In order to build the `SNMPMAN`  you can use Apache Maven to build a far JAR:

```
mvn clean package
```

After that you find your JAR in `snmpman-cli/target/snmpman-cli-$VERSION-SNAPSHOT-jar-with-dependencies.jar`.

Execution
============
To execute an instance of the `SNMPMAN` you have to start the execution script and specify a value for the `-c`
or `--configuration` option that lists the path to the configuration `YAML` file that will be loaded during execution.

Configuration
============
The `SNMPMAN` configuration can be found in two main `YAML` files ...
  * The agent configuration lists all agents which will be started
  * The device type configuration defines the modifiers that will be applied to a range of OIDs

You can find some `YAML` example configuration files within the test resources of this project in [this](https://github.com/1and1/snmpman/tree/master/snmpman-cli/src/test/resources/configuration
) directory.

The third component of the configuration are the walk files for the agents as specified in the agent configuration.

This is a basic overview and more information will be provided [on the project's Wiki pages](https://github.com/1and1/snmpman/wiki).

Contribution
------------

This section gives some pointers about contributions to the snmpman project.
The project is being developed by the community. Maintainers merge pull-requests, fix critical bugs, etc.

If you fixed or added something useful to the project, you can send a pull-request. It will be reviewed and accepted, or commented on for rework by [maintainers](https://github.com/1and1/snmpman/blob/master/MAINTAINERS). Pull-requests are declined if a maintainer closes the request. 
