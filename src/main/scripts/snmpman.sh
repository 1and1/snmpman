#!/bin/sh
# This shell script will execute the SNMPMAN
# ------------------------------------------------------------------

# The main class
readonly MAIN_CLASS="com.oneandone.network.snmpman.Snmpman"

[ -r "/etc/default/snmpman" ] && . "/etc/default/snmpman"

cygwin=false;
darwin=false;
linux=false;
case "`uname`" in
    CYGWIN*)
        cygwin=true
        ;;
    Darwin*)
        darwin=true
        ;;
    Linux)
        linux=true
        ;;
esac

if ${cygwin} ; then
    [ -n "${SNMPMAN_HOME}" ] &&
        SNMPMAN_HOME=`cygpath --unix "${SNMPMAN_HOME}"`
    [ -n "${JAVA_HOME}" ] &&
        JAVA_HOME=`cygpath --unix "${JAVA_HOME}"`
fi

if [ "x${SNMPMAN_HOME}" = "x" ]; then
    if [ -d "/opt/snmpman" ]; then
        SNMPMAN_HOME=/opt/snmpman
    else
        echo "SNMPMAN_HOME is not set, but should point to root directory of the installation"
        exit 1;
    fi
fi
export SNMPMAN_HOME

if [ "x${JAVA}" = "x" ]; then
    if [ "x${JAVA_HOME}" != "x" ]; then
        JAVA="${JAVA_HOME}/bin/java"
    else
        JAVA="java"
    fi
fi

ARGS="${@}"
if [ -f /opt/snmpman/etc/configuration.xml ] && [ "${ARGS}" != *-c* ]; then
    ARGS="-c ${SNMPMAN_HOME}/etc/configuration.yaml $ARGS"
fi

# Set the log4j configuration if not defined by the call
LOG_PARAMETER=""
if [ "${ARGS}" != *-Dlog4j.configuration* ] && [ -f /opt/snmpman/etc/log4j.xml ]; then
    LOG_PARAMETER="-Dlog4j.configuration=file:${SNMPMAN_HOME}/etc/log4j.xml"
elif [ "${ARGS}" != *-Dlog4j.configuration* ] && [ -f /opt/snmpman/etc/log4j.properties ]; then
    LOG_PARAMETER="-Dlog4j.configuration=file:${SNMPMAN_HOME}/etc/log4j.properties"
fi

eval \"${JAVA}\" ${JAVA_OPTS} "${LOG_PARAMETER}" -jar "${SNMPMAN_HOME}/lib/snmpman*.jar" "${ARGS}"
