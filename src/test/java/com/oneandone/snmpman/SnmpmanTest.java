package com.oneandone.snmpman;

import org.snmp4j.*;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class SnmpmanTest {

    private Snmpman snmpman;

    @BeforeMethod
    public void startSnmpman() throws Exception {
        snmpman = Snmpman.start(new File("src/test/resources/configuration/configuration.yaml"));
    }

    @Test
    public void testSnmpGetBulk() throws Exception {
        assertEquals(snmpman.getAgents().size(), 11);

        List<TableEvent> responses = getResponse(new OID("1.3.6.1.2.1"), 10000);
        assertEquals(responses.size(), 18);

        responses = getResponse(new OID("1.3.6.1.2.1.31"), 10000);
        assertEquals(responses.size(), 10);

        responses = getResponse(new OID(".1.3.6.1.2.1.2"), 10000);
        assertEquals(responses.size(), 7);

        responses = getResponse(new OID(".1.3"), 10010);
        assertEquals(responses.size(), 30);

        responses = getResponse(new OID(".1.0"), 10010);
        assertEquals(responses.size(), 8);
    }
    
    @AfterMethod
    public void stopSnmpman() throws Exception {
        snmpman.stop();
    }

    public static List<TableEvent> getResponse(final OID query, int port) throws Exception {
        final Address targetAddress = GenericAddress.parse(String.format("127.0.0.1/%d", port));
        final TransportMapping transport = new DefaultUdpTransportMapping();
        final Snmp snmp = new Snmp(transport);
        snmp.listen();

        final CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        
        // creating PDU
        final PDUFactory pduFactory = new DefaultPDUFactory(PDU.GETBULK);
        final TableUtils utils = new TableUtils(snmp, pduFactory);

        return utils.getTable(target, new OID[]{ query }, null, null);
    }
}