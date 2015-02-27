package com.oneandone.network.snmpman.configuration.modifier;

import com.oneandone.network.snmpman.configuration.type.ModifierProperties;
import org.testng.annotations.Test;

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
    
}