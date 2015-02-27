package com.oneandone.network.snmpman.configuration.modifier;

import org.snmp4j.smi.UnsignedInteger32;

/**
 * This modifier instance modifies {@link UnsignedInteger32} variables by the {@link #modify(UnsignedInteger32)} method.
 */
public class UnsignedInteger32Modifier extends AbstractIntegerModifier<UnsignedInteger32> {

    @Override
    protected UnsignedInteger32 cast(long value) {
        return new UnsignedInteger32(value);
    }
}
