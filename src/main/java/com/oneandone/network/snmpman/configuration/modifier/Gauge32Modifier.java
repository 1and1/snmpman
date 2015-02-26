package com.oneandone.network.snmpman.configuration.modifier;

import org.snmp4j.smi.Gauge32;

public class Gauge32Modifier extends AbstractIntegerModifier<Gauge32> {

    @Override
    protected Gauge32 cast(long value) {
        return new Gauge32(value);
    }
}
