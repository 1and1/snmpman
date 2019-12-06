package com.oneandone.snmpman;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableUtils;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

/**
 * This tests based on an instance of {@see com.oneandone.snmpman.snmp.MOGroup MOGroup} which is filled with
 * {@see org.snmp4j.smi.Variable VariableBindings} generated from example.txt.
 * The PDU.SET command fails when the type or the scope of the variable is unsuited.
 */
public class SnmpmanSetTest extends AbstractSnmpmanTest {

    private final OID OID_TABLE = new OID(".1.3.6.1.2.1.31.1.1.1.1.0");
    private final OID OID_ROW_INDEX = new OID("10101");
    private final OID OID_OCTETSTRING = new OID(".1.3.6.1.2.1.31.1.1.1.1");
    private final OID OID_GAUGE32 = new OID(".1.3.6.1.2.1.31.1.1.1.15");
    private final OID OID_UNCOVERED_FROM_SCOPE = new OID("1.3.6.1.2.3");
    private String COMMUNITY = "public";
    private final int PORT = 10009;


    @Test
    public void testSnmpSetValidVariable() throws Exception {
        String newValue = "New interface";
        VariableBinding[] bindings = {new VariableBinding(OID_OCTETSTRING, new OctetString(newValue))};
        ResponseEvent responseEvent = getResponseEvent(OID_OCTETSTRING, OID_ROW_INDEX, bindings);
        assertEquals(SnmpConstants.SNMP_ERROR_SUCCESS, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, newValue);
    }


    @Test
    public void testSnmpSetValidVariableOnUncoveredOID() throws Exception {
        VariableBinding[] bindings = {new VariableBinding(OID_UNCOVERED_FROM_SCOPE, new OctetString("not scoped variable"))};
        ResponseEvent responseEvent = getResponseEvent(OID_UNCOVERED_FROM_SCOPE, new OID("0"), bindings);
        assertEquals(SnmpConstants.SNMP_ERROR_NO_CREATION, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, "Gi0/1");
    }

    @Test
    public void testSnmpSetInvalidVariable() throws Exception {
        long invalidValue = 9000;
        VariableBinding[] bindings = {new VariableBinding(OID_OCTETSTRING, new Gauge32(invalidValue))};
        ResponseEvent responseEvent = getResponseEvent(OID_OCTETSTRING, OID_ROW_INDEX, bindings);
        assertEquals(SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, "Gi0/1");
    }

    @Test
    public void testSnmpSetMultipleVariable() throws Exception {
        VariableBinding[] bindings = {
                new VariableBinding(OID_OCTETSTRING, new OctetString("New interface label")),
                new VariableBinding(OID_GAUGE32, new Gauge32(999))};

        ResponseEvent responseEvent = getResponseEvent(OID_TABLE, OID_ROW_INDEX, bindings);
        assertEquals(SnmpConstants.SNMP_ERROR_SUCCESS, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, "New interface label");
        assertThatOidHasValue(OID_GAUGE32, "999");
    }

    @Test
    public void testSnmpSetMultipleVariablesWithInvalidValueDontSetAny() throws Exception {
        VariableBinding[] bindings = {new VariableBinding(OID_OCTETSTRING, new OctetString("new interface label")),
                new VariableBinding(OID_GAUGE32, new OctetString("invalid variable"))};
        ResponseEvent responseEvent = getResponseEvent(OID_TABLE, OID_ROW_INDEX, bindings);
        assertEquals(responseEvent.getResponse().getErrorStatus(), SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE);
        assertThatOidHasValue(OID_OCTETSTRING, "Gi0/1");
        assertThatOidHasValue(OID_GAUGE32, "1000");
    }

    @Test
    public void testSnmpSetMultipleVariablesWithInvalidValueAndUncoveredOIDDontSetAny() throws Exception {
        VariableBinding[] bindings = {
                new VariableBinding(new OID(".1.3.6.1.2.1.31.1.1.1.1"), new OctetString("New Interface")),
                new VariableBinding(new OID(".1.3.6.1.2.1.31.1.1.1.15"), new Gauge32(999)),
                new VariableBinding(OID_UNCOVERED_FROM_SCOPE, new OctetString("not covered scope"))};

        ResponseEvent responseEvent = getResponseEvent(OID_TABLE, OID_ROW_INDEX, bindings);
        assertEquals(responseEvent.getResponse().getErrorStatus(), SnmpConstants.SNMP_ERROR_NO_CREATION);
        assertThatOidHasValue(new OID(".1.3.6.1.2.1.31.1.1.1.1"), "Gi0/1");
        assertThatOidHasValue(new OID(".1.3.6.1.2.1.31.1.1.1.15"), "1000");
    }

    ResponseEvent getResponseEvent(OID rowStatusColumnOID, OID rowIndex, VariableBinding[] bindings) throws IOException {
        final Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();

        final CommunityTarget target = getCommunityTarget(COMMUNITY, GenericAddress.parse(String.format("127.0.0.1/%d", PORT)));

        // creating PDU
        final PDUFactory pduFactory = new DefaultPDUFactory(PDU.SET);
        final TableUtils utils = new TableUtils(snmp, pduFactory);
        return utils.createRow(target, rowStatusColumnOID, rowIndex, bindings);
    }

}
