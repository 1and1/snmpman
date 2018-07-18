package com.oneandone.snmpman.configuration.modifier;

import org.snmp4j.smi.Integer32;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class Integer32ModifierTest {

    @Test
    public void testModify() {
        final ModifierProperties modifierProperties = new ModifierProperties();
        modifierProperties.put("minimum", Integer.MIN_VALUE);
        modifierProperties.put("maximum", Integer.MAX_VALUE);
        modifierProperties.put("minimumStep", 1);
        modifierProperties.put("maximumStep", 10);

        final Integer32Modifier modifier = new Integer32Modifier();
        modifier.init(modifierProperties);

        final Integer32 integer32 = new Integer32(0);
        assertEquals(integer32.getValue(), 0);

        final Integer32 modifiedVariable = modifier.modify(integer32);
        assertNotEquals(modifiedVariable.getValue(), 0);
    }

    @Test
    public void testProperties() {
        final ModifierProperties modifierProperties = new ModifierProperties();
        modifierProperties.put("minimum", Integer.MIN_VALUE);
        modifierProperties.put("maximum", Integer.MAX_VALUE);
        modifierProperties.put("minimumStep", 1);
        modifierProperties.put("maximumStep", 10);

        final Integer32Modifier modifier = new Integer32Modifier();
        modifier.init(modifierProperties);

        assertEquals(modifier.getMinimum(), new Integer(Integer.MIN_VALUE));
        assertEquals(modifier.getMaximum(), new Integer(Integer.MAX_VALUE));
        assertEquals(modifier.getMinimumStep(), new Integer(1));
        assertEquals(modifier.getMaximumStep(), new Integer(10));
    }

}