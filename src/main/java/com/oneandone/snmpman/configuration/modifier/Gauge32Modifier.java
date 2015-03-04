package com.oneandone.snmpman.configuration.modifier;

import org.snmp4j.smi.Gauge32;

/** This modifier instance modifies {@link org.snmp4j.smi.Gauge32} variables. */
public class Gauge32Modifier extends AbstractIntegerModifier<Gauge32> {

    @Override
    protected Gauge32 cast(long value) {
        return new Gauge32(value);
    }
}
