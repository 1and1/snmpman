package com.oneandone.snmpman.configuration.modifier;

import com.oneandone.snmpman.configuration.type.ModifierProperties;
import lombok.Getter;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;

import java.util.*;

/**
 * This modifier implementation modifies {@link Counter32} variables depending on their community context.
 */
public class CommunityIndexCounter32Modifier implements CommunityContextModifier<Counter32> {
    /**
     * Mapping of SNMP community context to SNMP OID and result.
     */
    @Getter private Map<Long, Long> communityContextMapping = new HashMap<>();

    @Override
    public void init(final ModifierProperties properties) {
        communityContextMapping = new HashMap<>();
        properties.entrySet().stream().filter(property -> getUnsignedLong(property.getKey()) != -1L &&
                getUnsignedLong(property.getValue()) != -1L).forEach(property ->
                communityContextMapping.put(getUnsignedLong(property.getKey()), getUnsignedLong(property.getValue())));
    }

    private Long getUnsignedLong(final Object input) {
        try {
            if (!Optional.ofNullable(input).isPresent()) {
                // not present
                return -1L;
            }
            final String value = String.valueOf(input);
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            // s is not numeric
            return -11L;
        }
    }

    @Override
    public final Counter32 modify(final Counter32 variable) {
        if (variable == null) {
            return new Counter32(0);
        }
        return variable;
    }

    @Override
    public Map<OID, Variable> getVariableBindings(final OctetString context, final OID queryOID) {
        if (queryOID != null && context != null && context.getValue().length != 0) {
            if (!queryOID.toString().isEmpty() && !context.toString().isEmpty() && communityContextMapping.containsKey(Long.parseLong(context.toString()))) {
                return Collections.singletonMap(queryOID, new Counter32(communityContextMapping.get(Long.parseLong(context.toString()))));
            }
        } else if (queryOID != null) {
            return Collections.singletonMap(queryOID, modify(null));

        }
        return new TreeMap<>();
    }
}


