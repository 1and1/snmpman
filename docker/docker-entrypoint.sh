#!/bin/bash
set -e

if [[ "$SNMPMAN_PORT" = "" ]]; then
  export SNMPMAN_PORT="10000"
fi

if [[ "$SNMPMAN_COMMUNITY" = "" ]]; then
  export SNMPMAN_COMMUNITY="public"
fi

if [[ "$SNMPMAN_WALK" = "" ]]; then
  export SNMPMAN_WALK="/snmpman/etc/walk/example1.walk"
fi

if [[ "$SNMPMAN_CONFIG" = "" ]]; then
    export SNMPMAN_CONFIG=/snmpman/etc/configuration.yaml
    echo "Creating dummy config in $SNMPMAN_CONFIG UDP port $SNMPMAN_PORT, override with env var SNMPMAN_CONFIG"
    cat >> $SNMPMAN_CONFIG << CONFIG
- name: "example1"
  device: "/snmpman/etc/devices/ios.yaml"
  walk: "$SNMPMAN_WALK"
  ip: "0.0.0.0"
  port: $SNMPMAN_PORT
  community: $SNMPMAN_COMMUNITY
CONFIG
fi

#exec /snmpman/bin/snmpman.sh --configuration "$SNMPMAN_CONFIG"
exec java -Dlog4j.configurationFile=file:////snmpman/etc/log4j2.xml -jar /snmpman/lib/snmpman-cli-*.jar --configuration "$SNMPMAN_CONFIG"