SNMPMAN
============

The `SNMPMAN` is a command-line application that simulates `SNMP`-capable devices.
It may used e.g. to test traffic monitoring applications that execute `SNMP` queries. Due to to the lack in
flexibility of already existing alternatives, it was developed by the **1&1 Internet AG** to avoid those deficits. Therefore the `SNMPMAN`
is highly extensible and will feature and will be enhanced in time.

Multiple `SNMP` agents may be started at once and will run in parallel on different parts. These agents
will return the data defined in a textual walk file. This file may be created by querying a real device. In addition,
several device types may be defined and mapped by the agents. This device type configuration may contain policies on
how to dynamically change the query responses. E.g. the response of an
[ifInOctets](http://tools.cisco.com/Support/SNMP/do/BrowseOID.do?objectInput=ifInOctets&translate=Translate&submitValue=SUBMIT")
may be incremented with each query to simulate traffic. Several variable modifiers are already existing for different
data types.

Installation
============
### Prequisites
  * `Java JRE 1.7`
### Packaging
The `SNMPMAN` can be packaged for Debian installations by executing the Maven profile `debian`. All resources will be installed in the root directory `/opt/snmpman`.

Execution
------------
To execute an instance of the `SNMPMAN` you have to start the execution script and specify a value for the `-c`
or `--configuration` option that lists the path to the configuration file that should be loaded in the execution context.

Configuration
------------
The `SNMPMAN` is configured in two main `XML` files ...
  * The agent configuration _(Namespace: http://www.1and1.com/snmpman/configuration)_
  * The device type configuration _(Namespace: http://www.1and1.com/snmpman/device)_

The `XML` schema files can be found in this project in the directory
```
../src/main/resources/schema
```
The third component of the configuration are the walk files for the agents as specified in the agent configuration.

This is a basic overview and more information will be provided [on the projects Wiki pages](https://github.com/1and1/snmpman/wiki).

Contributing
------------
To contribute, use the GitHub way - fork, hack, and submit a pull request!
