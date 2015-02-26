package com.oneandone.network.snmpman.configuration.modifier;

import com.google.common.base.Preconditions;
import org.snmp4j.smi.UnsignedInteger32;

import java.util.Map;
import java.util.Properties;

/**
 * This modifier has all utility methods to construct for unsigned integer variable modifiers.
 *
 * @author Johann Böhler
 */
abstract class AbstractIntegerModifier<T extends UnsignedInteger32> implements VariableModifier<T> {

    /** The minimum allowed number for the resulting modified variable. */
    protected Long minimum;

    /** The maximum allowed number for the resulting modified variable. */
    protected Long maximum;

    /** The minimal step by which a variable will be incremented. */
    protected Long minimumStep;

    /** The maximal step by which a variable will be incremented. */
    protected Long maximumStep;

    @Override
    public void init(final Properties properties) {
        Preconditions.checkArgument(properties.containsKey("minimum") && properties.get("minimum") instanceof Number, "minimum not set or not a number");
        Preconditions.checkArgument(properties.containsKey("maximum") && properties.get("maximum") instanceof Number, "maximum not set or not a number");
        Preconditions.checkArgument(properties.containsKey("minimumStep") && properties.get("minimumStep") instanceof Number, "minimum step not set or not a number");
        Preconditions.checkArgument(properties.containsKey("maximumStep") && properties.get("maximumStep") instanceof Number, "maximum step not set or not a number");

        try {
            this.minimum = ((Number) properties.get("minimum")).longValue();
            this.maximum = ((Number) properties.get("maximum")).longValue();

            this.minimumStep = ((Number) properties.get("minimumStep")).longValue();
            this.maximumStep = ((Number) properties.get("maximumStep")).longValue();

            Preconditions.checkArgument(minimum >= 0, "minimum should not be negative");
            Preconditions.checkArgument(maximum >= 0, "maximum should not be negative");

            Preconditions.checkArgument(minimum <= 4294967295L, "minimum should not exceed 2^32-1 (4294967295 decimal)");
            Preconditions.checkArgument(maximum <= 4294967295L, "maximum should not exceed 2^32-1 (4294967295 decimal)");
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("one of the parameters exceeds the legal long value range", e);
        }
    }

    protected abstract T cast(long value);
    
    @Override
    public final T modify(final T variable) {
        long currentValue = variable.getValue();
        if (currentValue < minimum || currentValue > maximum) {
            currentValue = minimum;
        }
        long step = (Math.round(Math.random() * (maximumStep - minimumStep)) + minimumStep);

        long stepUntilMaximum = maximum - currentValue;
        long newValue;
        if (Math.abs(step) > Math.abs(stepUntilMaximum)) {
            newValue = minimum + (step - stepUntilMaximum - 1);
        } else {
            newValue = currentValue + step;
        }

        if (newValue < minimum) {
            newValue = minimum;
        } else if (newValue > maximum) {
            newValue = maximum;
        }
        
        return cast(newValue);
    }
}
