#! /bin/bash

BASEDIR=$(cd $(dirname "$0")/..; pwd)
JAR=${BASEDIR}/lib/snmpman-cli-${project.version}.jar
if [ "$LOGXML" = "" ]; then
  export LOGXML=file:///${BASEDIR}/etc/log4j2.xml
fi

java -Dlog4j.configurationFile=${LOGXML} -jar ${JAR} "$@"
