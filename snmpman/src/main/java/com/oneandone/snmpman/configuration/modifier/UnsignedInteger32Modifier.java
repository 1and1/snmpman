package com.oneandone.snmpman.configuration.modifier;

import org.snmp4j.smi.UnsignedInteger32;

/** This modifier instance modifies {@link org.snmp4j.smi.UnsignedInteger32} variables. */
public class UnsignedInteger32Modifier extends AbstractIntegerModifier<UnsignedInteger32> {

    @Override
    protected UnsignedInteger32 cast(long value) {
        return new UnsignedInteger32(value);
    }
}
