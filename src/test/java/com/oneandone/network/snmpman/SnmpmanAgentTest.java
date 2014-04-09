package com.oneandone.network.snmpman;

import junit.framework.Assert;
import org.snmp4j.smi.*;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class SnmpmanAgentTest {

    @Test
    public void testGetStringVariable() throws Exception {
        Method getMethod = SnmpmanAgent.class.getDeclaredMethod("getVariable", String.class, String.class);
        getMethod.setAccessible(true);

        Variable result = (Variable) getMethod.invoke(null, "STRING", "\"test\"");
        Assert.assertTrue(result instanceof OctetString);
        Assert.assertEquals("resulting string of variable does not match expected string", result.toString(), "test");
    }

    @Test
    public void testGetOIDVariable() throws Exception {
        Method getMethod = SnmpmanAgent.class.getDeclaredMethod("getVariable", String.class, String.class);
        getMethod.setAccessible(true);

        Variable result = (Variable) getMethod.invoke(null, "OID", ".1.3.3.7");
        Assert.assertTrue(result instanceof OID);
        Assert.assertEquals("resulting oid of variable does not match expected oid", result, new OID("1.3.3.7"));
    }

    @Test
    public void testGetGauge32Variable() throws Exception {
        Method getMethod = SnmpmanAgent.class.getDeclaredMethod("getVariable", String.class, String.class);
        getMethod.setAccessible(true);

        Variable result = (Variable) getMethod.invoke(null, "Gauge32", "0");
        Assert.assertTrue(result instanceof Gauge32);
        Assert.assertEquals("resulting value of variable does not match expected value", ((Gauge32) result).getValue(), 0L);
    }

    @Test
    public void testGetTimeTicksVariable() throws Exception {
        Method getMethod = SnmpmanAgent.class.getDeclaredMethod("getVariable", String.class, String.class);
        getMethod.setAccessible(true);

        Variable result = (Variable) getMethod.invoke(null, "Timeticks", "(1642971636) 190 days, 3:48:36.36");
        Assert.assertTrue(result instanceof TimeTicks);
        Assert.assertEquals("resulting value of variable does not match expected value", ((TimeTicks) result).getValue(), 1642971636L);
    }

    @Test
    public void testGetCounter32Variable() throws Exception {
        Method getMethod = SnmpmanAgent.class.getDeclaredMethod("getVariable", String.class, String.class);
        getMethod.setAccessible(true);

        Variable result = (Variable) getMethod.invoke(null, "Counter32", "0");
        Assert.assertTrue(result instanceof Counter32);
        Assert.assertEquals("resulting value of variable does not match expected value", ((Counter32) result).getValue(), 0L);
    }

    @Test
    public void testGetCounter64Variable() throws Exception {
        Method getMethod = SnmpmanAgent.class.getDeclaredMethod("getVariable", String.class, String.class);
        getMethod.setAccessible(true);

        Variable result = (Variable) getMethod.invoke(null, "Counter64", "0");
        Assert.assertTrue(result instanceof Counter64);
        Assert.assertEquals("resulting value of variable does not match expected value", ((Counter64) result).getValue(), 0L);
    }

    @Test
    public void testGetInteger32Variable() throws Exception {
        Method getMethod = SnmpmanAgent.class.getDeclaredMethod("getVariable", String.class, String.class);
        getMethod.setAccessible(true);

        Variable result = (Variable) getMethod.invoke(null, "INTEGER", "0");
        Assert.assertTrue(result instanceof Integer32);
        Assert.assertEquals("resulting value of variable does not match expected value", ((Integer32) result).getValue(), 0);
    }

    @Test
    public void testGetHexStringVariable() throws Exception {
        Method getMethod = SnmpmanAgent.class.getDeclaredMethod("getVariable", String.class, String.class);
        getMethod.setAccessible(true);

        Variable result = (Variable) getMethod.invoke(null, "Hex-STRING", "FF FF FF FF ");
        Assert.assertTrue(result instanceof OctetString);
        Assert.assertEquals("resulting string of variable does not match expected string", ((OctetString) result).toString(), "ff:ff:ff:ff");
    }

}
