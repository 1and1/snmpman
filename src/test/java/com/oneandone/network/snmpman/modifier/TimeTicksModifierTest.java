package com.oneandone.network.snmpman.modifier;

import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.snmp4j.smi.TimeTicks;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;


public class TimeTicksModifierTest {

    @Test
    public void testModify() throws Exception {
        TimeTicks timeTicks = new TimeTicks(0L);
        TimeTicksModifier modifier = new TimeTicksModifier(new ArrayList<AbstractModifier.Param>(0));

        // simulate some uptime, sleep a bit (10ms at least to see a change for sure)
        Thread.sleep(16);

        TimeTicks newTimeTicks = (TimeTicks) modifier.modify(timeTicks).clone();
        Assert.assertTrue(newTimeTicks.getValue() > timeTicks.getValue(), "new timestamp is older");
    }
}
