package com.oneandone.snmpman.configuration.modifier;

import org.snmp4j.smi.Counter32;

/** This modifier instance modifies {@link org.snmp4j.smi.Counter32} variables. */
public class Counter32Modifier extends AbstractIntegerModifier<Counter32> {

    @Override
    protected Counter32 cast(long value) {
        return new Counter32(value);
    }
}
