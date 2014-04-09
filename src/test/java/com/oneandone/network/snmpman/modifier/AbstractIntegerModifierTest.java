package com.oneandone.network.snmpman.modifier;

import com.google.common.primitives.UnsignedInteger;
import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.snmp4j.smi.UnsignedInteger32;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class AbstractIntegerModifierTest {

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

        new UnsignedInteger32Modifier(params);
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

        new UnsignedInteger32Modifier(params);
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

        new UnsignedInteger32Modifier(params);
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

        new UnsignedInteger32Modifier(params);
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

        new UnsignedInteger32Modifier(params);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testFailingIfFailingIfMinimumExceedsLongRange() {
        List<AbstractModifier.Param> params = new ArrayList<>(4);

        AbstractModifier.Param minimum = new AbstractModifier.Param();
        minimum.setName("minimum");
        minimum.setValue(Long.MAX_VALUE + "1");
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

        new UnsignedInteger32Modifier(params);
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

        UnsignedInteger32Modifier modifier = new UnsignedInteger32Modifier(params);
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
        maximum.setValue(UnsignedInteger.MAX_VALUE.toString());
        params.add(maximum);

        AbstractModifier.Param minimumStep = new AbstractModifier.Param();
        minimumStep.setName("minimumStep");
        minimumStep.setValue("1");
        params.add(minimumStep);

        AbstractModifier.Param maximumStep = new AbstractModifier.Param();
        maximumStep.setName("maximumStep");
        maximumStep.setValue("1");
        params.add(maximumStep);

        UnsignedInteger32Modifier modifier = new UnsignedInteger32Modifier(params);

        UnsignedInteger32 integer = new UnsignedInteger32(UnsignedInteger.MAX_VALUE.longValue());
        Assert.assertEquals(integer.toString(), UnsignedInteger.MAX_VALUE.toString(), "values are not equal");

        UnsignedInteger32 modified = (UnsignedInteger32) modifier.modify(integer).clone();
        Assert.assertEquals(modified.toString(), UnsignedInteger.ZERO.toString(), "new value is not zero as expected");
    }


}
