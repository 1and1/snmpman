#!/bin/sh
readonly SNMPMAN_GROUP="snmpman"
readonly SNMPMAN_USER="snmpman"

# check that owner group exists
if ! getent group ${SNMPMAN_GROUP} &>/dev/null ; then
  groupadd ${SNMPMAN_GROUP}
fi

# check that user exists
if ! getent passwd ${SNMPMAN_USER} &>/dev/null ; then
  useradd -M --gid ${SNMPMAN_GROUP} ${SNMPMAN_USER}
fi

# (optional) check that user belongs to group
if ! id -G -n ${SNMPMAN_USER} | grep -qF ${SNMPMAN_GROUP} ; then
  usermod -a -G ${SNMPMAN_GROUP} ${SNMPMAN_USER}
fi