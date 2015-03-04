package com.oneandone.snmpman.configuration.modifier;

import org.snmp4j.smi.TimeTicks;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TimeTicksModifierTest {

    @Test
    public void testModify() throws Exception {
        final TimeTicksModifier timeTicksModifier = new TimeTicksModifier();

        final TimeTicks timeTicks = new TimeTicks();
        final long initialTimeTicks = timeTicks.getValue();

        Thread.sleep(11L);
        
        final TimeTicks modifiedVariable = timeTicksModifier.modify(timeTicks);
        final long newTimeTicks = modifiedVariable.getValue();
        
        assertNotEquals(newTimeTicks, initialTimeTicks);
    }
}