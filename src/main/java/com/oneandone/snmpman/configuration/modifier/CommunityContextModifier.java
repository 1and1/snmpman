package com.oneandone.snmpman.configuration.modifier;

import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

import java.util.Map;

/**
 * This is a {@link VariableModifier}, which is able to handle the SNMP community context.
 */
public interface CommunityContextModifier<T extends Variable> extends VariableModifier<T> {
    /**
     * Getter for the mapping of the community context to value mapping.
     *
     * @return Map of context value mapping.
     */
    Map<Long, Map<OID, Long>> getCommunityContextMapping();

    /**
     * Creates a mapping of SNMP OID and a value based on the SNMP community context and the SNMP OID.
     *
     * @param context SNMP community context.
     * @param oid     SNMP OID.
     * @return variable binding of OID to Variable.
     */
    Map<OID, Variable> getVariableBindings(final OctetString context, final OID oid);
}
