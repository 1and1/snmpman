package com.oneandone.snmpman;

import com.oneandone.snmpman.exception.InitializationException;
import org.mockito.Mockito;
import org.snmp4j.*;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

public class SnmpmanTest {

    private Snmpman snmpman;

    @BeforeMethod
    public void startSnmpman() throws Exception {
        snmpman = Snmpman.start(new File("src/test/resources/configuration/configuration.yaml"));
    }

    @AfterMethod
    public void stopSnmpman() throws Exception {
        snmpman.stop();
    }

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
    public void startWithAlreadyStoppedAgent() throws Exception {
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


    public static boolean containsColumn(final List<TableEvent> responses, final String oid, final String result) {
        for (final TableEvent e : responses){
            if (Arrays.toString(e.getColumns()).contains(oid) && Arrays.toString(e.getColumns()).contains("= " + result)){
                return true;
            }
        }
        return false;
    }

    public static List<TableEvent> getResponse(final OID query, int port, final String community) throws Exception {
        final Address targetAddress = GenericAddress.parse(String.format("127.0.0.1/%d", port));
        final Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();

        final CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);

        // creating PDU
        final PDUFactory pduFactory = new DefaultPDUFactory(PDU.GETBULK);
        final TableUtils utils = new TableUtils(snmp, pduFactory);

        return utils.getTable(target, new OID[]{ query }, null, null);
    }

    public static List<TableEvent> getResponse(final OID query, int port) throws Exception {
        return getResponse(query, port, "public");
    }
}