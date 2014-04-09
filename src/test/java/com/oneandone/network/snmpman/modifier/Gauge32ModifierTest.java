package com.oneandone.network.snmpman.modifier;

import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class Gauge32ModifierTest {

    @Test
    public void testConstructionWithNegativeStepSizes() throws Exception {
        List<AbstractModifier.Param> params = new ArrayList<>(4);

        AbstractModifier.Param minimum = new AbstractModifier.Param();
        minimum.setName("minimum");
        minimum.setValue("0");
        params.add(minimum);

        AbstractModifier.Param maximum = new AbstractModifier.Param();
        maximum.setName("maximum");
        maximum.setValue("0");
        params.add(maximum);

        AbstractModifier.Param minimumStep = new AbstractModifier.Param();
        minimumStep.setName("minimumStep");
        minimumStep.setValue("-1");
        params.add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("-1");
        params.add(maximumStep);

        new Gauge32Modifier(params);
    }

}
