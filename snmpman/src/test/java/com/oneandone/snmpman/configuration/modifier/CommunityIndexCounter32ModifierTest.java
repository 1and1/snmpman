package com.oneandone.snmpman.configuration.modifier;

import com.oneandone.snmpman.configuration.type.ModifierProperties;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CommunityIndexCounter32ModifierTest {
    private ModifierProperties modifierProperties;
    private long context1;
    private long contextValue1;
    private long context2;
    private long contextValue2;
    private static final String OID = "1.3.6.1.2.1.17.2.4.0";

    @BeforeTest
    private void setUp() {
        context1 = 20;
        contextValue1 = 150;
        context2 = 9;
        contextValue2 = 120;
        modifierProperties = new ModifierProperties();
        modifierProperties.put(context1, contextValue1);
        modifierProperties.put(context2, contextValue2);
    }

    @Test
    public void testInit() {
        final CommunityIndexCounter32Modifier modifier = new CommunityIndexCounter32Modifier();
        modifier.init(modifierProperties);

        final Map<Long, Long> communityIndexValues = modifier.getCommunityContextMapping();
        assertEquals(communityIndexValues.size(), 2);
        assertTrue(communityIndexValues.containsKey(context1) && communityIndexValues.containsValue(contextValue1));
        assertTrue(communityIndexValues.containsKey(context2) && communityIndexValues.containsValue(contextValue2));

        // unchanged
        modifierProperties.put("maximum", Collections.singletonMap("1.3.6", 172L));
        final CommunityIndexCounter32Modifier modifier2 = new CommunityIndexCounter32Modifier();
        modifier2.init(modifierProperties);

        final Map<Long, Long> communityIndexValues2 = modifier.getCommunityContextMapping();
        assertEquals(communityIndexValues2.size(), 2);
        assertTrue(communityIndexValues2.containsKey(context1) && communityIndexValues2.containsValue(contextValue1));
        assertTrue(communityIndexValues2.containsKey(context2) && communityIndexValues2.containsValue(contextValue2));
    }

    @Test
    public void testModify() {
        final CommunityIndexCounter32Modifier modifier = new CommunityIndexCounter32Modifier();
        modifier.init(modifierProperties);

        final Counter32 counter32 = new Counter32(contextValue1);
        assertEquals(counter32.getValue(), contextValue1);

        final Counter32 modifiedVariable = modifier.modify(counter32);
        assertEquals(modifiedVariable.getValue(), contextValue1);

        final Counter32 modifiedVariable2 = modifier.modify(null);
        assertEquals(modifiedVariable2, new Counter32(0L));
    }

    @Test
    public void testGetVariableBindings() {
        final CommunityIndexCounter32Modifier modifier = new CommunityIndexCounter32Modifier();
        modifier.init(modifierProperties);

        final OctetString context1 = new OctetString("20");
        final OID queryOID1 = new OID(OID);
        final Map<OID, Variable> variableBindings1 = modifier.getVariableBindings(context1, queryOID1);

        assertEquals(variableBindings1.size(), 1);
        assertEquals(variableBindings1.get(queryOID1), new Counter32(150L));

        final OctetString context2 = new OctetString("23");
        final OID queryOID2 = new OID("1.3.6");
        final Map<OID, Variable> variableBindings2 = modifier.getVariableBindings(context2, queryOID2);

        assertEquals(variableBindings2.size(), 0, "bindings with not initialized context should be empty");

        final OctetString context3 = null;
        final OID queryOID3 = new OID("1.3.6");
        final Map<OID, Variable> variableBindings3 = modifier.getVariableBindings(context3, queryOID3);

        assertEquals(variableBindings3.size(), 1);
        assertEquals(variableBindings3.get(queryOID3), new Counter32(0L), "bindings with null context should be 0");

        final OctetString context4 = new OctetString();
        final OID queryOID4 = new OID("1.3.6");
        final Map<OID, Variable> variableBindings4 = modifier.getVariableBindings(context4, queryOID4);

        assertEquals(variableBindings4.size(), 1);
        assertEquals(variableBindings4.get(queryOID4), new Counter32(0L), "bindings with empty context should be 0");

        final OctetString context5 = new OctetString("20");
        final OID queryOID5 = null;
        final Map<OID, Variable> variableBindings5 = modifier.getVariableBindings(context5, queryOID5);

        assertEquals(variableBindings5.size(), 0, "bindings with null query OID should be empty");
    }
}