package com.oneandone.snmpman.configuration.modifier;

import com.oneandone.snmpman.configuration.type.ModifierProperties;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class ModifierTest {

    @Test
    public void testIsApplicable() throws Exception {        
        final Modifier modifier = new Modifier(".1.3.6.*", "com.oneandone.snmpman.configuration.modifier.Counter32Modifier", new ModifierProperties());
        assertTrue(modifier.isApplicable(new OID(".1.3.6.1")));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModify() throws Exception {
        final ModifierProperties modifierProperties = new ModifierProperties();
        modifierProperties.put("minimum", 0L);
        modifierProperties.put("maximum", 3000L);
        modifierProperties.put("minimumStep", 1L);
        modifierProperties.put("maximumStep", 10L);

        final Modifier modifier = new Modifier(".1.3.6.*", "com.oneandone.snmpman.configuration.modifier.Counter32Modifier", modifierProperties);

        final Counter32 counter32 = new Counter32(0L);
        assertEquals(counter32.getValue(), 0L);

        final Variable modifiedVariable = modifier.modify(counter32);
        assertTrue(modifiedVariable instanceof Counter32);
        assertNotEquals(((Counter32) modifiedVariable).getValue(), 0L);
    }
}