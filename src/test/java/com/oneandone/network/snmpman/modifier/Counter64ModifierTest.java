package com.oneandone.network.snmpman.modifier;

import com.google.common.primitives.UnsignedLong;
import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.snmp4j.smi.Counter64;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class Counter64ModifierTest {

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testFailingIfMinimumNotSet() {
        List<AbstractModifier.Param> params = new ArrayList<>(3);

        AbstractModifier.Param maximum = new AbstractModifier.Param();
        maximum.setName("maximum");
        maximum.setValue("0");
        params.add(maximum);

        AbstractModifier.Param minimumStep = new AbstractModifier.Param();
        minimumStep.setName("minimumStep");
        minimumStep.setValue("0");
        params.add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("0");
        params.add(maximumStep);

        new Counter64Modifier(params);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testFailingIfMaximumNotSet() {
        List<AbstractModifier.Param> params = new ArrayList<>(3);

        AbstractModifier.Param minimum = new AbstractModifier.Param();
        minimum.setName("minimum");
        minimum.setValue("0");
        params.add(minimum);

        AbstractModifier.Param minimumStep = new AbstractModifier.Param();
        minimumStep.setName("minimumStep");
        minimumStep.setValue("0");
        params.add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("0");
        params.add(maximumStep);

        new Counter64Modifier(params);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testFailingIfMinimumStepNotSet() {
        List<AbstractModifier.Param> params = new ArrayList<>(3);

        AbstractModifier.Param minimum = new AbstractModifier.Param();
        minimum.setName("minimum");
        minimum.setValue("0");
        params.add(minimum);

        AbstractModifier.Param maximum = new AbstractModifier.Param();
        maximum.setName("maximum");
        maximum.setValue("0");
        params.add(maximum);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("0");
        params.add(maximumStep);

        new Counter64Modifier(params);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testFailingIfMaximumStepNotSet() {
        List<AbstractModifier.Param> params = new ArrayList<>(3);

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
        minimumStep.setValue("0");
        params.add(minimumStep);

        new Counter64Modifier(params);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testFailingIfMinimumIsNotANumber() {
        List<AbstractModifier.Param> params = new ArrayList<>(4);

        AbstractModifier.Param minimum = new AbstractModifier.Param();
        minimum.setName("minimum");
        minimum.setValue("notanumber");
        params.add(minimum);

        AbstractModifier.Param maximum = new AbstractModifier.Param();
        maximum.setName("maximum");
        maximum.setValue("0");
        params.add(maximum);

        AbstractModifier.Param minimumStep = new AbstractModifier.Param();
        minimumStep.setName("minimumStep");
        minimumStep.setValue("0");
        params.add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("0");
        params.add(maximumStep);

        new Counter64Modifier(params);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testFailingIfFailingIfMinimumExceedsUnsignedLongRange() {
        List<AbstractModifier.Param> params = new ArrayList<>(4);

        AbstractModifier.Param minimum = new AbstractModifier.Param();
        minimum.setName("minimum");
        minimum.setValue(UnsignedLong.MAX_VALUE.toString() + "1");
        params.add(minimum);

        AbstractModifier.Param maximum = new AbstractModifier.Param();
        maximum.setName("maximum");
        maximum.setValue("0");
        params.add(maximum);

        AbstractModifier.Param minimumStep = new AbstractModifier.Param();
        minimumStep.setName("minimumStep");
        minimumStep.setValue("0");
        params.add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("0");
        params.add(maximumStep);

        new Counter64Modifier(params);
    }

    @Test
    public void testSuccessfulConstruction() {
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
        minimumStep.setValue("0");
        params.add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("0");
        params.add(maximumStep);

        Counter64Modifier modifier = new Counter64Modifier(params);
        Assert.assertNotNull(modifier, "constructed modifier should not be null");
    }

    @Test(dependsOnMethods = "testSuccessfulConstruction")
    public void testModificationOverflow() {
        List<AbstractModifier.Param> params = new ArrayList<>(4);

        AbstractModifier.Param minimum = new AbstractModifier.Param();
        minimum.setName("minimum");
        minimum.setValue("0");
        params.add(minimum);

        AbstractModifier.Param maximum = new AbstractModifier.Param();
        maximum.setName("maximum");
        maximum.setValue(UnsignedLong.MAX_VALUE.toString());
        params.add(maximum);

        AbstractModifier.Param minimumStep = new AbstractModifier.Param();
        minimumStep.setName("minimumStep");
        minimumStep.setValue("1");
        params.add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("1");
        params.add(maximumStep);

        Counter64Modifier modifier = new Counter64Modifier(params);

        Counter64 counter = new Counter64(UnsignedLong.MAX_VALUE.longValue());
        Assert.assertEquals(counter.toString(), UnsignedLong.MAX_VALUE.toString(), "values are not equal");

        Counter64 modified = (Counter64) modifier.modify(counter).clone();
        Assert.assertEquals(modified.toString(), UnsignedLong.ZERO.toString(), "new value is not zero as expected");
    }

}
