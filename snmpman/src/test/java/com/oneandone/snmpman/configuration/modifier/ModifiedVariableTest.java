package com.oneandone.snmpman.configuration.modifier;

import com.oneandone.snmpman.configuration.type.ModifierProperties;
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
    public void setUp() {
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
    public void testCompareTo() {
        assertEquals(variable.compareTo(otherVariable), modifiedVariable.compareTo(otherVariable));
    }

    @Test
    public void testClone() {
        final Object clonedVariable = modifiedVariable.clone();
        assertNotSame(clonedVariable, modifiedVariable);
        assertTrue(clonedVariable instanceof Integer32);
        
        assertNotEquals(((Integer32) clonedVariable).getValue(), ((Integer32) variable).getValue());
    }

    @Test
    public void testGetSyntax() {
        assertEquals(modifiedVariable.getSyntax(), variable.getSyntax());
    }

    @Test
    public void testIsException() {
        assertEquals(modifiedVariable.isException(), variable.isException());
    }

    @Test
    public void testToString() {
        assertEquals(modifiedVariable.toString(), variable.toString());
    }

    @Test
    public void testToInt() {
        assertEquals(modifiedVariable.toInt(), variable.toInt());
    }

    @Test
    public void testToLong() {
        assertEquals(modifiedVariable.toLong(), variable.toLong());
    }

    @Test
    public void testGetSyntaxString() {
        assertEquals(modifiedVariable.getSyntaxString(), variable.getSyntaxString());
    }

    @Test
    public void testToSubIndex() {
        assertEquals(modifiedVariable.toSubIndex(true), variable.toSubIndex(true));
        assertEquals(modifiedVariable.toSubIndex(false), variable.toSubIndex(false));
    }

    @Test
    public void testIsDynamic() {
        assertEquals(modifiedVariable.isDynamic(), variable.isDynamic());
    }

    @Test
    public void testGetBERLength() {
        assertEquals(modifiedVariable.getBERLength(), variable.getBERLength());
    }

    @Test
    public void testGetBERPayloadLength() {
        assertEquals(modifiedVariable.getBERPayloadLength(), variable.getBERPayloadLength());
    }

    @Test
    public void testEquals() {
        assertTrue(modifiedVariable.equals(modifiedVariable));
        assertTrue(modifiedVariable.equals(variable));
    }

    @Test
    public void testHashCode() {
        assertEquals(modifiedVariable.hashCode(), variable.hashCode());
    }
}