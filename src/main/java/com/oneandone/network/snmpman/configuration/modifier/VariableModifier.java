package com.oneandone.network.snmpman.configuration.modifier;

import com.oneandone.network.snmpman.configuration.type.ModifierProperties;
import org.snmp4j.smi.Variable;

import java.util.Map;
import java.util.Properties;

/** Abstract definition of a variable modifier. */
public interface VariableModifier<T extends Variable> {

    /**
     * Modifies the specified {@code variable} and returns a copy of it.
     * 
     * @param variable the variable to modify
     * @return the modified variable
     */
    public T modify(final T variable);

    /**
     * Initialize {@code this} variable modifier by the specified {@code properties}.
     *
     * @param properties the initialization properties
     */
    public default void init(final ModifierProperties properties) { }
}
