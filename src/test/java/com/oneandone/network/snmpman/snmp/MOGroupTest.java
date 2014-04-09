package com.oneandone.network.snmpman.snmp;

import com.oneandone.network.snmpman.modifier.ModifiedVariable;
import com.oneandone.network.snmpman.modifier.VariableModifier;
import org.mockito.Mockito;
import org.snmp4j.PDU;
import org.snmp4j.agent.request.SubRequest;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;

public class MOGroupTest {

    private MOGroup simple;

    @BeforeTest
    public void setUp() {
        simple = new MOGroup(new OID(".1.3.3.7"), new OID(".1.3.3.7"), new ModifiedVariable(new OctetString("test"), new ArrayList<VariableModifier>(0)));
    }

    @Test
    public void testGetWithObject() throws Exception {
        final SubRequest request = Mockito.mock(SubRequest.class);
        final VariableBinding variableBinding = new VariableBinding(new OID(".1.3.3.7"));
        Mockito.when(request.getVariableBinding()).thenReturn(variableBinding);

        simple.get(request);

        Assert.assertTrue(variableBinding.getVariable() instanceof OctetString, "result of request is not type of String");
        Assert.assertEquals(variableBinding.getVariable().toString(), "test");
    }

    @Test
    public void testGetWithNoResult() throws Exception {
        final SubRequest request = Mockito.mock(SubRequest.class);
        final VariableBinding variableBinding = new VariableBinding(new OID(".6.9."));
        Mockito.when(request.getVariableBinding()).thenReturn(variableBinding);

        simple.get(request);

        Assert.assertEquals(variableBinding.getVariable(), Null.noSuchInstance);
    }

    @Test
    public void testPrepare() throws Exception {
        final SubRequest request = Mockito.mock(SubRequest.class);
        simple.prepare(request);
        Mockito.verify(request, Mockito.atLeast(1)).setErrorStatus(PDU.notWritable);
        Mockito.verify(request, Mockito.atMost(1)).setErrorStatus(PDU.notWritable);
    }

    @Test
    public void testCommit() throws Exception {
        final SubRequest request = Mockito.mock(SubRequest.class);
        simple.commit(request);
        Mockito.verify(request, Mockito.atLeast(1)).setErrorStatus(PDU.commitFailed);
        Mockito.verify(request, Mockito.atMost(1)).setErrorStatus(PDU.commitFailed);
    }

    @Test
    public void testUndo() throws Exception {
        final SubRequest request = Mockito.mock(SubRequest.class);
        simple.undo(request);
        Mockito.verify(request, Mockito.atLeast(1)).setErrorStatus(PDU.undoFailed);
        Mockito.verify(request, Mockito.atMost(1)).setErrorStatus(PDU.undoFailed);
    }
}
