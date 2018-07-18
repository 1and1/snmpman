package com.oneandone.snmpman.configuration.modifier;

import org.snmp4j.smi.Gauge32;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Gauge32ModifierTest {

    @Test
    public void testModify() {
        final ModifierProperties modifierProperties = new ModifierProperties();
        modifierProperties.put("minimum", 0);
        modifierProperties.put("maximum", 3000);
        modifierProperties.put("minimumStep", 1);
        modifierProperties.put("maximumStep", 10);

        final Gauge32Modifier modifier = new Gauge32Modifier();
        modifier.init(modifierProperties);

        final Gauge32 gauge32 = new Gauge32(0);
        assertEquals(gauge32.getValue(), 0);

        final Gauge32 modifiedVariable = modifier.modify(gauge32);
        assertNotEquals(modifiedVariable.getValue(), 0);
    }

}