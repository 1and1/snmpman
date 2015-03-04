package com.oneandone.snmpman.integration;

import com.oneandone.snmpman.SnmpmanTest;
import org.snmp4j.smi.OID;
import org.snmp4j.util.TableEvent;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

@Test(groups = "integration-test")
public class SnmpmanIntegrationTest {

    @Test
    public void testSnmpGetBulk() throws Exception {
        List<TableEvent> responses = SnmpmanTest.getResponse(new OID("1.3.6.1.2.1"));
        assertEquals(responses.size(), 18);

        responses = SnmpmanTest.getResponse(new OID("1.3.6.1.2.1.31"));
        assertEquals(responses.size(), 10);

        responses = SnmpmanTest.getResponse(new OID(".1.3.6.1.2.1.2"));
        assertEquals(responses.size(), 7);
    }

}
