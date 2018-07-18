#!/bin/sh
readonly SNMPMAN_GROUP="snmpman"
readonly SNMPMAN_USER="snmpman"

if getent passwd ${SNMPMAN_USER} &>/dev/null ; then
  deluser ${SNMPMAN_USER}
fi

if getent group ${SNMPMAN_GROUP} &>/dev/null ; then
  delgroup ${SNMPMAN_GROUP}
fi