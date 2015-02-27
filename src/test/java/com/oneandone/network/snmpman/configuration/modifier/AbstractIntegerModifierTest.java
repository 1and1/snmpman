package com.oneandone.network.snmpman.configuration.modifier;

import com.oneandone.network.snmpman.configuration.type.ModifierProperties;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class AbstractIntegerModifierTest {

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testIllegalInitParameterMinimum() {
        final ModifierProperties modifierProperties = new ModifierProperties();
        modifierProperties.put("minimum", -1);
        modifierProperties.put("maximum", 3000);
        modifierProperties.put("minimumStep", 1);
        modifierProperties.put("maximumStep", 10);

        final Gauge32Modifier modifier = new Gauge32Modifier();
        modifier.init(modifierProperties);
    }

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testIllegalInitParameterMaximum() {
        final ModifierProperties modifierProperties = new ModifierProperties();
        modifierProperties.put("minimum", 0);
        modifierProperties.put("maximum", -1);
        modifierProperties.put("minimumStep", 1);
        modifierProperties.put("maximumStep", 10);

        final Gauge32Modifier modifier = new Gauge32Modifier();
        modifier.init(modifierProperties);
    }

    @Test
    public void testProperties() throws Exception {
        final ModifierProperties modifierProperties = new ModifierProperties();
        modifierProperties.put("minimum", 0);
        modifierProperties.put("maximum", 10);
        modifierProperties.put("minimumStep", 1);
        modifierProperties.put("maximumStep", 10);

        final Gauge32Modifier modifier = new Gauge32Modifier();
        modifier.init(modifierProperties);

        assertEquals(modifier.getMinimum(), new Long(0));
        assertEquals(modifier.getMaximum(), new Long(10));
        assertEquals(modifier.getMinimumStep(), new Long(1));
        assertEquals(modifier.getMaximumStep(), new Long(10));
    }
    
}