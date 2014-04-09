package com.oneandone.network.snmpman.modifier;

import junit.framework.Assert;
import org.snmp4j.smi.UnsignedInteger32;
import org.snmp4j.smi.Variable;
import org.testng.annotations.Test;

import java.util.ArrayList;


public class ModifiedVariableTest {

    @Test
    public void testEquals() throws Exception {
        Variable variable = new UnsignedInteger32(20);
        ModifiedVariable modifiedVariable = new ModifiedVariable(variable, new ArrayList<VariableModifier>(0));

        Assert.assertTrue("self is not equal", modifiedVariable.equals(modifiedVariable));
        Assert.assertFalse("variable does match", variable.equals(modifiedVariable));
        Assert.assertTrue("modified doesn't match", modifiedVariable.equals(variable));
    }
}
