package com.oneandone.network.snmpman.configuration.type;

import com.google.common.primitives.UnsignedLong;
import com.oneandone.network.snmpman.configuration.AgentConfiguration;
import com.oneandone.network.snmpman.configuration.Device;
import com.oneandone.network.snmpman.configuration.modifier.Counter64Modifier;
import com.oneandone.network.snmpman.configuration.modifier.Modifier;
import org.snmp4j.smi.OID;
import org.testng.annotations.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigInteger;

import static org.testng.Assert.*;

public class ModifierPropertiesTest {

    @Test
    public void testParsing() throws Exception {
        final AgentConfiguration.DeviceFactory deviceFactory = new AgentConfiguration.DeviceFactory();
        final Device device = deviceFactory.getDevice(new File("src/test/resources/configuration/cisco.yaml"));

        assertFalse(device.getModifiers().isEmpty());

        boolean counter64found = false;
        for (final Modifier modifier : device.getModifiers()) {
            // counter64
            if (modifier.isApplicable(new OID(".1.3.6.1.2.1.31.1.1.1.11.1"))) {
                final Field variableModifierField = Modifier.class.getDeclaredField("modifier");
                variableModifierField.setAccessible(true);

                final Counter64Modifier counter64Modifier = (Counter64Modifier) variableModifierField.get(modifier);
                assertEquals(counter64Modifier.getMaximum(), UnsignedLong.valueOf(new BigInteger("1844674407370955161")));
                counter64found = true;
            }
        }
        
        if (!counter64found) {
            fail("no modifier for unsigned long found");
        }
    }
    
}