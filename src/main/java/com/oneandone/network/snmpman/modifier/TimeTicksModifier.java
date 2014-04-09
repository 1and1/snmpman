package com.oneandone.network.snmpman.modifier;

import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.snmp4j.smi.TimeTicks;

import java.util.List;

/**
 * This modifier instance modifies {@link org.snmp4j.smi.TimeTicks} variables by the {@link #modify(org.snmp4j.smi.TimeTicks)} method.
 * <p/>
 * This method will return a new {@link TimeTicks} instance with the current timestamp.
 *
 * @author Johann Böhler
 */
public class TimeTicksModifier extends VariableModifier<TimeTicks> {

    private final long initTime = System.currentTimeMillis();

    /**
     * Constructs a new instance of this class.
     *
     * @param params the parameter for this modifier
     */
    public TimeTicksModifier(final List<AbstractModifier.Param> params) {
        super(params);
    }

    @Override
    public TimeTicks modify(final TimeTicks variable) {
        TimeTicks timeTicks = new TimeTicks();
        final long timeTicksInMilliseconds = variable.toMilliseconds();
        final long uptime = (System.currentTimeMillis() - initTime) + timeTicksInMilliseconds;
        timeTicks.fromMilliseconds(timeTicksInMilliseconds + uptime);
        return timeTicks;
    }
}
