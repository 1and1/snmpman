package com.oneandone.snmpman;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableUtils;
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

    private static final OID OID_TABLE = new OID(".1.3.6.1.2.1.31.1.1.1.1.0");
    private static final OID OID_ROW_INDEX = new OID("10101");
    private static final OID OID_OCTETSTRING = new OID(".1.3.6.1.2.1.31.1.1.1.1");
    private static final String PRIMAL_OCTECT_VALUE = "Gi0/1";
    private static final OID OID_GAUGE32 = new OID(".1.3.6.1.2.1.31.1.1.1.15");
    private static final String PRIMAL_GAUGE32_VALUE = "1000";
    private static final OID OID_UNCOVERED_FROM_SCOPE = new OID("1.3.6.1.2.3");
    private Snmp snmp;

    @BeforeMethod
    public void initSnmpSession() throws IOException {
        snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();
    }

    @Test
    public void testSnmpCreateRowValidVariable() throws Exception {
        String newValue = "New interface";
        VariableBinding[] bindings = {new VariableBinding(OID_OCTETSTRING, new OctetString(newValue))};
        ResponseEvent responseEvent = requestSetOidValues(OID_OCTETSTRING, OID_ROW_INDEX, bindings);
        assertEquals(SnmpConstants.SNMP_ERROR_SUCCESS, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, newValue);
    }

    @Test
    public void testSnmpCreateRowValidVariableOnUncoveredOID() throws Exception {
        VariableBinding[] bindings = {new VariableBinding(OID_UNCOVERED_FROM_SCOPE, new OctetString("not scoped variable"))};
        ResponseEvent responseEvent = requestSetOidValues(OID_UNCOVERED_FROM_SCOPE, new OID("0"), bindings);
        assertEquals(SnmpConstants.SNMP_ERROR_NO_CREATION, responseEvent.getResponse().getErrorStatus());
        assertNoOidHasChanged();
    }

    @Test
    public void testSnmpCreateRowInvalidVariable() throws Exception {
        long invalidValue = 9000;
        VariableBinding[] bindings = {new VariableBinding(OID_OCTETSTRING, new Gauge32(invalidValue))};
        ResponseEvent responseEvent = requestSetOidValues(OID_OCTETSTRING, OID_ROW_INDEX, bindings);
        assertEquals(SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE, responseEvent.getResponse().getErrorStatus());
        assertNoOidHasChanged();
    }

    @Test
    public void testSnmpCreateRowMultipleVariable() throws Exception {
        VariableBinding[] bindings = {
                new VariableBinding(OID_OCTETSTRING, new OctetString("New interface label")),
                new VariableBinding(OID_GAUGE32, new Gauge32(999))};

        ResponseEvent responseEvent = requestSetOidValues(OID_TABLE, OID_ROW_INDEX, bindings);
        assertEquals(SnmpConstants.SNMP_ERROR_SUCCESS, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, "New interface label");
        assertThatOidHasValue(OID_GAUGE32, "999");
    }

    @Test
    public void testSnmpCreateRowMultipleVariablesWithInvalidValueDontSetAny() throws Exception {
        VariableBinding[] bindings = {new VariableBinding(OID_OCTETSTRING, new OctetString("new interface label")),
                new VariableBinding(OID_GAUGE32, new OctetString("invalid variable"))};
        ResponseEvent responseEvent = requestSetOidValues(OID_TABLE, OID_ROW_INDEX, bindings);
        assertEquals(responseEvent.getResponse().getErrorStatus(), SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE);
        assertNoOidHasChanged();
    }

    @Test
    public void testSnmpCreateRowNotPresentVariablesWithInvalidValueDontSetAny() throws Exception {
        VariableBinding[] bindings = {new VariableBinding(new OID(".1.3.6.1.2.1.31.1.1.1.100"), new OctetString("new interface label")),
                new VariableBinding(OID_GAUGE32, new OctetString("invalid variable"))};
        ResponseEvent responseEvent = requestSetOidValues(OID_TABLE, OID_ROW_INDEX, bindings);
        assertEquals(responseEvent.getResponse().getErrorStatus(), SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE);
        assertThatOidHasValue(new OID(".1.3.6.1.2.1.31.1.1.1.100"), "null");
        assertNoOidHasChanged();
    }

    @Test
    public void testSnmpCreateRowMultipleVariablesWithInvalidValueAndUncoveredOIDDontSetAny() throws Exception {
        VariableBinding[] bindings = {
                new VariableBinding(OID_OCTETSTRING, new OctetString("New Interface")),
                new VariableBinding(OID_GAUGE32, new Integer32(999)),
                new VariableBinding(OID_UNCOVERED_FROM_SCOPE, new OctetString("not covered scope"))};

        ResponseEvent responseEvent = requestSetOidValues(OID_TABLE, OID_ROW_INDEX, bindings);
        assertEquals(responseEvent.getResponse().getErrorStatus(), SnmpConstants.SNMP_ERROR_NO_CREATION);
        assertNoOidHasChanged();
    }

    private void assertNoOidHasChanged() throws Exception {
        assertThatOidHasValue(OID_OCTETSTRING, PRIMAL_OCTECT_VALUE);
        assertThatOidHasValue(OID_GAUGE32, PRIMAL_GAUGE32_VALUE);
    }

    ResponseEvent requestSetOidValues(OID rowStatusColumnOID, OID rowIndex, VariableBinding[] bindings) throws IOException {
        final Snmp snmp = new Snmp(new DefaultUdpTransportMapping());

        snmp.listen();
        final CommunityTarget target = getCommunityTarget(COMMUNITY, GenericAddress.parse(String.format("127.0.0.1/%d", PORT)));

        // creating PDU
        final PDUFactory pduFactory = new DefaultPDUFactory(PDU.SET);
        final TableUtils utils = new TableUtils(snmp, pduFactory);
        return utils.createRow(target, rowStatusColumnOID, rowIndex, bindings);
    }

    @Test
    public void testSetterForValidValue() throws Exception {
        String newValue = "New interface";
        ResponseEvent responseEvent = setPduWrapper(new OID(OID_OCTETSTRING).append(OID_ROW_INDEX), new OctetString(newValue));
        assertEquals(SnmpConstants.SNMP_ERROR_SUCCESS, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, newValue);
    }

    @Test
    public void testSetterForInvalidValue() throws Exception {
        Integer newValue = 9999;
        ResponseEvent responseEvent = setPduWrapper(new OID(OID_OCTETSTRING).append(OID_ROW_INDEX), new Integer32(newValue));
        assertEquals(SnmpConstants.SNMP_ERROR_INCONSISTENT_VALUE, responseEvent.getResponse().getErrorStatus());
        assertThatOidHasValue(OID_OCTETSTRING, PRIMAL_OCTECT_VALUE);
    }


    private ResponseEvent setPduWrapper(OID oid, Variable variable) throws IOException {

        final CommunityTarget target = getCommunityTarget(COMMUNITY, GenericAddress.parse(String.format("127.0.0.1/%d", PORT)));

        PDU pdu = new PDU();
        pdu.setType(PDU.SET);

        VariableBinding variableBinding = new VariableBinding(oid);
        variableBinding.setVariable(variable);

        pdu.add(variableBinding);
        return snmp.set(pdu, target);
    }
}
