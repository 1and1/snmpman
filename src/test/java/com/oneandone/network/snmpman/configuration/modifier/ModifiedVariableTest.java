package com.oneandone.network.snmpman.configuration.modifier;

import com.oneandone.network.snmpman.configuration.type.ModifierProperties;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.Variable;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;

public class ModifiedVariableTest {

    private Variable variable;
    private Variable otherVariable;
    private ModifiedVariable modifiedVariable;
    
    @BeforeMethod
    public void setUp() throws Exception {
        variable = new Integer32(0);
        otherVariable = new Integer32(1);
        
        List<VariableModifier> modifiers = new ArrayList<>(1);

        final ModifierProperties modifierProperties = new ModifierProperties();
        modifierProperties.put("minimum", Integer.MIN_VALUE);
        modifierProperties.put("maximum", Integer.MAX_VALUE);
        modifierProperties.put("minimumStep", 1);
        modifierProperties.put("maximumStep", 10);

        final Integer32Modifier modifier = new Integer32Modifier();
        modifier.init(modifierProperties);
        
        modifiers.add(modifier);
        
        modifiedVariable = new ModifiedVariable(variable, modifiers);
    }
    
    @Test
    public void testCompareTo() throws Exception {
        assertEquals(variable.compareTo(otherVariable), modifiedVariable.compareTo(otherVariable));
    }

    @Test
    public void testClone() throws Exception {
        final Object clonedVariable = modifiedVariable.clone();
        assertNotSame(clonedVariable, modifiedVariable);
        assertTrue(clonedVariable instanceof Integer32);
        
        assertNotEquals(((Integer32) clonedVariable).getValue(), ((Integer32) variable).getValue());
    }

    @Test
    public void testGetSyntax() throws Exception {
        assertEquals(modifiedVariable.getSyntax(), variable.getSyntax());
    }

    @Test
    public void testIsException() throws Exception {
        assertEquals(modifiedVariable.isException(), variable.isException());
    }

    @Test
    public void testToString() throws Exception {
        assertEquals(modifiedVariable.toString(), variable.toString());
    }

    @Test
    public void testToInt() throws Exception {
        assertEquals(modifiedVariable.toInt(), variable.toInt());
    }

    @Test
    public void testToLong() throws Exception {
        assertEquals(modifiedVariable.toLong(), variable.toLong());
    }

    @Test
    public void testGetSyntaxString() throws Exception {
        assertEquals(modifiedVariable.getSyntaxString(), variable.getSyntaxString());
    }

    @Test
    public void testToSubIndex() throws Exception {
        assertEquals(modifiedVariable.toSubIndex(true), variable.toSubIndex(true));
        assertEquals(modifiedVariable.toSubIndex(false), variable.toSubIndex(false));
    }

    @Test
    public void testIsDynamic() throws Exception {
        assertEquals(modifiedVariable.isDynamic(), variable.isDynamic());
    }

    @Test
    public void testGetBERLength() throws Exception {
        assertEquals(modifiedVariable.getBERLength(), variable.getBERLength());
    }

    @Test
    public void testGetBERPayloadLength() throws Exception {
        assertEquals(modifiedVariable.getBERPayloadLength(), variable.getBERPayloadLength());
    }

    @Test
    public void testEquals() throws Exception {
        assertTrue(modifiedVariable.equals(modifiedVariable));
        assertTrue(modifiedVariable.equals(variable));
    }

    @Test
    public void testHashCode() throws Exception {
        assertEquals(modifiedVariable.hashCode(), variable.hashCode());
    }
}