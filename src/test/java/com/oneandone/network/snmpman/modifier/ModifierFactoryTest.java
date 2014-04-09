package com.oneandone.network.snmpman.modifier;

import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.testng.Assert;
import org.testng.annotations.Test;


public class ModifierFactoryTest {

    @Test
    public void testCreate() throws Exception {
        ModifierFactory modifierFactory = new ModifierFactory();
        modifierFactory.setModifierClass("com.oneandone.network.snmpman.modifier.Counter32Modifier");

        AbstractModifier.Param minimum = new AbstractModifier.Param();
        minimum.setName("minimum");
        minimum.setValue("0");
        modifierFactory.getParam().add(minimum);

        AbstractModifier.Param maximum = new AbstractModifier.Param();
        maximum.setName("maximum");
        maximum.setValue("0");
        modifierFactory.getParam().add(maximum);

        AbstractModifier.Param minimumStep = new AbstractModifier.Param();
        minimumStep.setName("minimumStep");
        minimumStep.setValue("0");
        modifierFactory.getParam().add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("0");
        modifierFactory.getParam().add(maximumStep);

        VariableModifier modifier = modifierFactory.create();
        Assert.assertNotNull(modifier, "modifier should not be null");
        Assert.assertTrue(modifier instanceof Counter32Modifier, "modifier is not an instance of the expected modifier");
    }
}
