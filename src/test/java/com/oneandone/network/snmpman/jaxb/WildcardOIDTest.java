package com.oneandone.network.snmpman.jaxb;

import org.snmp4j.smi.OID;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WildcardOIDTest {

    @Test
    public void testMatchesWithWildcard() throws Exception {
        WildcardOID wildcardOID = new WildcardOID(new OID(".1.3.3.7"), new OID(".1"));

        OID[] legal = {new OID("1.3.3.7.1"), new OID(".1.3.3.7.5.5.1"), new OID(".1.3.3.7.1.2.3.4.5.6.7.8.9.1"), new OID(".1.3.3.7.1")};
        for (OID oid : legal) {
            Assert.assertTrue(wildcardOID.matches(oid));
        }


        OID[] illegal = {new OID(".1.3.3.7"), new OID(".1"), new OID(".1.3.3.7.5.5.5.5"), new OID(".5.6.1.3.3.7.5.1")};
        for (OID oid : illegal) {
            Assert.assertFalse(wildcardOID.matches(oid));
        }
    }

    @Test
    public void testMatchesWithoutWildcard() throws Exception {
        WildcardOID wildcardOID = new WildcardOID(new OID(".1.3.3.7"), null);

        OID[] legal = {new OID("1.3.3.7"), new OID("1.3.3.7.1"), new OID(".1.3.3.7.5.5.1"), new OID(".1.3.3.7.1.2.3.4.5.6.7.8.9.1")};
        for (OID oid : legal) {
            Assert.assertTrue(wildcardOID.matches(oid));
        }


        OID[] illegal = {new OID(".1"), new OID(".5.6.1.3.3.7.5.1")};
        for (OID oid : illegal) {
            Assert.assertFalse(wildcardOID.matches(oid));
        }
    }

    @Test(expectedExceptions = {NullPointerException.class})
    public void testConstructorWithNullString() throws Exception {
        new WildcardOID(null);
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testConstructionWithMultipleWildcards() throws Exception {
        new WildcardOID(".1.3.*.3.*.7");
    }

    @Test
    public void testConstructorEquality() throws Exception {
        WildcardOID a = new WildcardOID(".1.3.3.7.*.1");
        WildcardOID b = new WildcardOID(new OID(".1.3.3.7"), new OID(".1"));
        Assert.assertTrue(a.equals(b));

        a = new WildcardOID(".1.3.3.7");
        b = new WildcardOID(new OID(".1.3.3.7"), null);
        Assert.assertTrue(a.equals(b));
    }

}
