package com.oneandone.snmpman.integration;

import org.snmp4j.smi.OID;
import org.snmp4j.util.TableEvent;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test(groups = "integration-test")
public class SnmpmanIntegrationTest {

    private Snmpman snmpman;

    @BeforeMethod
    public void startSnmpman() {
        snmpman = Snmpman.start(new File("src/test/resources/configuration/configuration.yaml"));
    }

    @AfterMethod
    public void stopSnmpman() {
        snmpman.stop();
    }

    @Test
    public void testSnmpGetBulk() {
        List<TableEvent> responses = SnmpmanTest.getResponse(new OID("1.3.6.1.2.1"), 10000);
        assertEquals(responses.size(), 19);

        responses = SnmpmanTest.getResponse(new OID("1.3.6.1.2.1.31"), 10000);
        assertEquals(responses.size(), 10);

        responses = SnmpmanTest.getResponse(new OID(".1.3.6.1.2.1.2"), 10000);
        assertEquals(responses.size(), 7);
    }

    @Test
    public void testWithCommunityIndex() {
        final String oid = "1.3.6.1.2.1.17.2.4";
        List<TableEvent> responses1 = SnmpmanTest.getResponse(new OID(oid), 10009, "public@42");
        assertEquals(responses1.size(), 1);
        assertTrue(SnmpmanTest.containsColumn(responses1, oid, "150"));

        List<TableEvent> responses2 = SnmpmanTest.getResponse(new OID(oid), 10009, "public@9");
        assertEquals(responses2.size(), 1);
        assertTrue(SnmpmanTest.containsColumn(responses2, oid, "120"));

        List<TableEvent> responses3 = SnmpmanTest.getResponse(new OID(oid), 10009, "public");
        assertEquals(responses3.size(), 1);
        assertTrue(SnmpmanTest.containsColumn(responses3, oid, "0"));
    }

}
