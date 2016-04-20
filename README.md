[![Travis CI](https://travis-ci.org/1and1/snmpman.svg?branch=master)](https://travis-ci.org/1and1/snmpman)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/db37a0ee28a74266a3ec4b4efb70bb8c)](https://www.codacy.com/app/1and1_NDev/snmpman)
[![Codacy Badge](https://api.codacy.com/project/badge/coverage/db37a0ee28a74266a3ec4b4efb70bb8c)](https://www.codacy.com/app/1and1_NDev/snmpman)

SNMPMAN
============

The `SNMPMAN` is a command-line application that simulates `SNMP`-capable devices and was developed to be usable during unit testing.
It may be used to test traffic monitoring applications that execute `SNMP` queries. Due to the lack of
flexibility in already existing alternatives, it was developed by the **1&1 Internet AG** to improve flexibility. Therefore the `SNMPMAN`
is highly extendable and will be enhanced over time.

Multiple `SNMP` agents may be started at once and will run in parallel. The agents running in parallel
will return the data queried and save it to a textual walk result file. This file will be created by querying a real device. It is also possible to query several 
device types at once. The query configuration may contain policies on
how to dynamically change the query responses. E.g. the response of an
[ifInOctets](http://tools.cisco.com/Support/SNMP/do/BrowseOID.do?objectInput=ifInOctets&translate=Translate&submitValue=SUBMIT")
may be incremented with each query to simulate traffic. Several variable modifiers are already in existence for different
data types.

Also checkout the `SNMPMAN` GitHub IO page [here](http://1and1.github.io/snmpman/)!

Usage
============
You can include the `SNMPMAN` as a Maven dependency from [Maven Central]().

```xml
<dependency>
    <groupId>com.oneandone</groupId>
    <artifactId>snmpman</artifactId>
    <version>1.3</version>
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

Installation
============
### Prerequisites
  * `Java JDK 1.8`
  * `Gradle 2.2`

### Packaging
In order to package the `SNMPMAN` - as either a `.deb` or `.rpm` - you have to execute the following gradle tasks

  * `buildDeb` - **We strongly advise not to use the debian package, due to bugs**
  * `buildRpm`

You will find the generated output in `./build/distributions` and can then install the built packages.

Execution
============
To execute an instance of the `SNMPMAN` you have to start the execution script and specify a value for the `-c`
or `--configuration` option that lists the path to the configuration `YAML` file that will be loaded during execution.

Configuration
============
The `SNMPMAN` configuration can be found in two main `YAML` files ...
  * The agent configuration lists all agents which will be started
  * The device type configuration defines the modifiers that will be applied to a range of OIDs

You can find some `YAML` example configuration files within the test resources of this project in the directory
```
../src/test/resources/configuration
```
The third component of the configuration are the walk files for the agents as specified in the agent configuration.

This is a basic overview and more information will be provided [on the project's Wiki pages](https://github.com/1and1/snmpman/wiki).

Contribution
------------

This section gives some pointers about contributions to the snmpman project.
The project is being developed by the community. Maintainer merge pull-requests, fix critical bugs, etc.

If you fixed or added something useful to the project, you can send a pull-request. It will be reviewed and accepted, or commented on for rework by at least two [maintainers](https://github.com/1and1/snmpman/blob/master/MAINTAINERS). Pull-requests are declined if a maintainer closes the request. 

We are using [LGTM](https://lgtm.co) to approve pull-requests. The pull-request is only accepted if two maintainers comment with ["LGTM"](https://lgtm.co) or ":+1:".
