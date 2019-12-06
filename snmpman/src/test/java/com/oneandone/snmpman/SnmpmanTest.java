package com.oneandone.snmpman;

import com.oneandone.snmpman.exception.InitializationException;
import org.mockito.Mockito;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.smi.OID;
import org.snmp4j.util.TableEvent;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class SnmpmanTest extends AbstractSnmpmanTest {

    @Test
    public void testSnmpGetBulk() throws Exception {
        assertEquals(snmpman.getAgents().size(), 11);

        List<TableEvent> responses = getResponse(new OID("1.3.6.1.2.1"), 10000);
        assertEquals(responses.size(), 19);

        responses = getResponse(new OID("1.3.6.1.2.1.31"), 10000);
        assertEquals(responses.size(), 10);

        responses = getResponse(new OID(".1.3.6.1.2.1.2"), 10000);
        assertEquals(responses.size(), 7);

        responses = getResponse(new OID(".1.3"), 10010);
        assertEquals(responses.size(), 30);

        responses = getResponse(new OID(".1.0"), 10010);
        assertEquals(responses.size(), 8);
    }

    @Test
    public void testWithCommunityIndex() throws Exception {
        assertEquals(snmpman.getAgents().size(), 11);

        final String oid = "1.3.6.1.2.1.17.2.4";
        List<TableEvent> responses1 = getResponse(new OID(oid), 10009, "public@42");
        assertEquals(responses1.size(), 1);
        assertTrue(containsColumn(responses1, oid, "150"));

        List<TableEvent> responses2 = getResponse(new OID(oid), 10009, "public@9");
        assertEquals(responses2.size(), 1);
        assertTrue(containsColumn(responses2, oid, "120"));

        List<TableEvent> responses3 = getResponse(new OID(oid), 10009, "public");
        assertEquals(responses3.size(), 1);
        assertTrue(containsColumn(responses3, oid, "0"));
    }

    @Test(expectedExceptions = InitializationException.class)
    public void startWithAlreadyStoppedAgent() {
        final SnmpmanAgent mock = Mockito.mock(SnmpmanAgent.class);
        Mockito.when(mock.getAgentState()).thenReturn(BaseAgent.STATE_STOPPED);

        final List<SnmpmanAgent> snmpmanAgents = Collections.singletonList(mock);
        Snmpman.start(snmpmanAgents);
    }

    @Test
    public void testModifier() throws Exception {
        final String oid = "1.3.6.1.2.1.2.2.1.13";
        List<TableEvent> responses1 = SnmpmanTest.getResponse(new OID(oid), 10009, "public");
        List<TableEvent> responses2 = SnmpmanTest.getResponse(new OID(oid), 10009, "public");

        assertNotEquals(responses1.get(0).getColumns(), responses2.get(0).getColumns(),
                "repeated call should return a different result");
    }

}