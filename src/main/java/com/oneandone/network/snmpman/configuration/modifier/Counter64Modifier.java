package com.oneandone.network.snmpman.configuration.modifier;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedLong;

import com.oneandone.network.snmpman.configuration.type.ModifierProperties;
import org.snmp4j.smi.Counter64;

import java.util.Map;
import java.util.Properties;

/**
 * This modifier instance modifies {@link Counter64} variables by the {@link #modify(Counter64)} method.
 *
 * @author Johann Böhler
 */
public class Counter64Modifier implements VariableModifier<Counter64> {

    /**
     * The minimum allowed number for the resulting modified variable.
     */
    private UnsignedLong minimum;

    /**
     * The maximum allowed number for the resulting modified variable.
     */
    private UnsignedLong maximum;

    /**
     * The minimal step by which a variable will be incremented.
     */
    private UnsignedLong minimumStep;

    /**
     * The maximal step by which a variable will be incremented.
     */
    private UnsignedLong maximumStep;

    @Override
    public void init(final ModifierProperties properties) {
       /* Preconditions.checkArgument(properties.containsKey("minimum") && AbstractIntegerModifier.isNumeric(properties.get("minimum")), "minimum not set or not a number");
        Preconditions.checkArgument(properties.containsKey("maximum") && AbstractIntegerModifier.isNumeric(properties.get("maximum")), "maximum not set or not a number");
        Preconditions.checkArgument(properties.containsKey("minimumStep") && AbstractIntegerModifier.isNumeric(properties.get("minimumStep")), "minimum step not set or not a number");
        Preconditions.checkArgument(properties.containsKey("maximumStep") && AbstractIntegerModifier.isNumeric(properties.get("maximumStep")), "maximum step not set or not a number");

        try {
            this.minimum = UnsignedLong.valueOf(properties.get("minimum"));
            this.maximum = UnsignedLong.valueOf(properties.get("maximum"));

            this.minimumStep = UnsignedLong.valueOf(properties.get("minimumStep"));
            this.maximumStep = UnsignedLong.valueOf(properties.get("maximumStep"));
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("one of the parameters exceeds the legal long value range", e);
        }*/
    }
    
    @Override
    public Counter64 modify(final Counter64 variable) {
        UnsignedLong currentValue = UnsignedLong.valueOf(variable.toString());
        if (currentValue.compareTo(minimum) < 0 || currentValue.compareTo(maximum) > 0) {
            currentValue = minimum;
        }

        final UnsignedLong step = UnsignedLong.valueOf((long) (Math.random() * maximumStep.minus(minimumStep).longValue())).plus(minimumStep);
        final UnsignedLong newValue = currentValue.plus(step);

        return new Counter64(newValue.longValue());
    }

}
