package com.oneandone.network.snmpman.configuration.modifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneandone.network.snmpman.configuration.WildcardOID;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;

import java.util.Properties;

public class Modifier<T extends Variable> implements VariableModifier<T> {

    private final WildcardOID oid;

    private final VariableModifier<T> modifier;

    public Modifier(@JsonProperty("oid") final String oid,
                    @JsonProperty("class") final String myClass,
                    @JsonProperty("properties") final Properties properties) {
        this.oid = new WildcardOID(oid);

        try {
            // TODO try catch hier is ugly
            this.modifier = (VariableModifier) Class.forName(myClass).newInstance();
            this.modifier.init(properties);
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isApplicable(final OID oid) {
        return this.oid.matches(oid);
    }
    
    @Override
    public T modify(T variable) {
        return modifier.modify(variable);
    }
}
