package com.oneandone.network.snmpman.modifier;

import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.snmp4j.smi.Variable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A variable modifier modifies a variable on each call of {@link #modify(org.snmp4j.smi.Variable)}.
 * <p/>
 * <i>Note:</i> Each implementation of this class should have a constructor like {@link #VariableModifier(java.util.List)} with
 * a list of parameters. Otherwise an exception will occur in the creation in the {@link ModifierFactory}.
 *
 * @author Johann Böhler
 */
public abstract class VariableModifier<T extends Variable> {

    /**
     * The modification parameter.
     */
    protected final Map<String, String> parameter = new HashMap<>(0);

    /**
     * Constructs a new instance of this class.
     *
     * @param params the parameter for this modifier
     */
    public VariableModifier(final List<AbstractModifier.Param> params) {
        for (final AbstractModifier.Param param : params) {
            parameter.put(param.getName(), param.getValue());
        }
    }

    /**
     * Modifies the value of the specified variable and returns it after modification.
     *
     * @param variable the variable to modify
     * @return the modified variable
     */
    public abstract T modify(final T variable);

}
