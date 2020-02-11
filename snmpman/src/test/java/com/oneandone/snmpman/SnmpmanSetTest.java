package com.oneandone.snmpman;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * This tests based on an instance of {@see com.oneandone.snmpman.snmp.MOGroup MOGroup} which is filled with
 * {@see org.snmp4j.smi.Variable VariableBindings} generated from example.txt.
 * The PDU.SET command fails when the type or the scope of the variable is unsuited.
 */
public class SnmpmanSetTest extends AbstractSnmpmanTest {

    private static final OID OID_ROW_INDEX = new OID("10101");
    private static final OID OID_OCTETSTRING = new OID(".1.3.6.1.2.1.31.1.1.1.1");
    private static final String PRIMAL_OCTECT_VALUE = "Gi0/1";
    private Snmp snmp;

    @BeforeMethod
    public void initSnmpSession() throws IOException {
        snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();
    }

    @Test
    public void testSetterForValidValue() throws Exception {
        String newValue = "New interface";
        ResponseEvent responseEvent = setVariableToOID(new OID(OID_OCTETSTRING).append(OID_ROW_INDEX), new OctetString(newValue));
        assertEquals(SnmpConstants.SNMP_ERROR_SUCCESS, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, newValue);
    }

    @Test
    public void testSetterForInvalidValue() throws Exception {
        Integer newValue = 9999;
        ResponseEvent responseEvent = setVariableToOID(new OID(OID_OCTETSTRING).append(OID_ROW_INDEX), new Integer32(newValue));
        assertEquals(SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, PRIMAL_OCTECT_VALUE);
    }


    private ResponseEvent setVariableToOID(OID oid, Variable variable) throws IOException {

        final CommunityTarget target = getCommunityTarget(COMMUNITY, GenericAddress.parse(String.format("127.0.0.1/%d", PORT)));

        PDU pdu = new PDU();
        pdu.setType(PDU.SET);

        VariableBinding variableBinding = new VariableBinding(oid);
        variableBinding.setVariable(variable);

        pdu.add(variableBinding);
        return snmp.set(pdu, target);
    }
}
