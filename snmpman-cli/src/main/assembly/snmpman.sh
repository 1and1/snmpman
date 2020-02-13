#! /bin/bash

BASEDIR=$(cd $(dirname "$0")/..; pwd)
JAR=${BASEDIR}/lib/snmpman-cli-*.jar

java -Dlog4j.configurationFile=file:///${BASEDIR}/etc/log4j2.xml -jar ${JAR} "$@"
