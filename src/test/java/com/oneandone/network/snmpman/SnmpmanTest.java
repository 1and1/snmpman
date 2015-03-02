package com.oneandone.network.snmpman;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.assertEquals;

public class SnmpmanTest {

    private Snmpman snmpman;

    @BeforeMethod
    public void startSnmpman() throws Exception {
        snmpman = Snmpman.start(new File("src/test/resources/configuration/configuration.yaml"));
    }

    @Test
    public void testSnmpGetBulk() throws Exception {
        PDU responsePDU = getResponse(new OID("1.3.6.1.2.1"));
        assertEquals(responsePDU.getVariableBindings().size(), 19); // 18 + endOfMibView

        responsePDU = getResponse(new OID("1.3.6.1.2.1.31"));
        assertEquals(responsePDU.getVariableBindings().size(), 11); // 10 + endOfMibView

        responsePDU = getResponse(new OID(".1.3.6.1.2.1.2"));
        assertEquals(responsePDU.getVariableBindings().size(), 18); // 17 + endOfMibView
    }

    @AfterMethod
    public void stopSnmpman() throws Exception {
        snmpman.stop();
    }

    private static PDU getResponse(final OID query) throws Exception {
        final Address targetAddress = GenericAddress.parse("127.0.0.1/10000");
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

        final PDU pdu = new PDU();
        pdu.add(new VariableBinding(query));
        pdu.setType(PDU.GETBULK);
        pdu.setMaxRepetitions(Short.MAX_VALUE);
        pdu.setNonRepeaters(0);
        // sending request
        
        final ResponseEvent responseEvent = snmp.send(pdu, target, transport);
        return responseEvent.getResponse();
    }
}