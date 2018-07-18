package com.oneandone.snmpman.configuration.type;

import org.snmp4j.smi.OID;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class WildcardOIDTest {

    @Test(expectedExceptions = { IllegalArgumentException.class })
    public void testIllegalWildcard() {
        new WildcardOID(".1.2.3.*.*.4");
    }

    @Test
    public void testStartingMatcher() {
        final WildcardOID wildcard = new WildcardOID(".1.2.3.*");

        assertTrue(wildcard.matches(new OID("1.2.3.4")));
        assertTrue(wildcard.matches(new OID("1.2.3.4.5")));
        assertFalse(wildcard.matches(new OID("1.2.4")));

        assertEquals(wildcard.toString(), "1.2.3.*");
    }

    @Test
    public void testGeneralMatcher() {
        final WildcardOID wildcard = new WildcardOID(".1.2.3.*.5");

        assertTrue(wildcard.matches(new OID("1.2.3.4.5")));
        assertTrue(wildcard.matches(new OID("1.2.3.1337.5")));
        assertFalse(wildcard.matches(new OID("1.2.3.4")));
        assertFalse(wildcard.matches(new OID("1.2.3.4.4")));

        assertEquals(wildcard.toString(), "1.2.3.*.5");
    }
}