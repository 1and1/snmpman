#!/bin/bash
set -e

if [[ "$SNMPMAN_CONFIG" = "" ]]; then
    export SNMPMAN_CONFIG=/snmpman/etc/configuration.yaml
    echo "Creating dummy config in $SNMPMAN_CONFIG UDP port 10000, override with env var SNMPMAN_CONFIG"
    cat >> $SNMPMAN_CONFIG << CONFIG
- name: "example1"
  device: "/snmpman/etc/devices/ios.yaml"
  walk: "/snmpman/etc/walk/example1.walk"
  ip: "0.0.0.0"
  port: 10000
  community: public
CONFIG
fi

#exec /snmpman/bin/snmpman.sh --configuration "$SNMPMAN_CONFIG"
exec java -Dlog4j.configurationFile=file:////snmpman/etc/log4j2.xml -jar /snmpman/lib/snmpman-cli-*.jar --configuration "$SNMPMAN_CONFIG"