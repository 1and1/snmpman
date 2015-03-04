package com.oneandone.snmpman.configuration.modifier;

import org.snmp4j.smi.TimeTicks;

/**
 * This modifier instance modifies {@link TimeTicks} variables by the {@link #modify(TimeTicks)} method.
 * <p/>
 * This method will return a new {@link TimeTicks} instance with the current timestamp.
 */
public class TimeTicksModifier implements VariableModifier<TimeTicks> {

    /** The initialization time of {@code this} modifier. */
    private final long initTime = System.currentTimeMillis();

    @Override
    public TimeTicks modify(final TimeTicks variable) {
        TimeTicks timeTicks = new TimeTicks();
        final long timeTicksInMilliseconds = variable.toMilliseconds();
        final long upTime = (System.currentTimeMillis() - initTime) + timeTicksInMilliseconds;
        timeTicks.fromMilliseconds(timeTicksInMilliseconds + upTime);
        return timeTicks;
    }
}
