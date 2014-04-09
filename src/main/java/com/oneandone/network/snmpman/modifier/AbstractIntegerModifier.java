package com.oneandone.network.snmpman.modifier;

import com.google.common.base.Preconditions;
import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.Variable;

import java.util.List;

/**
 * This modifier has all utility methods to construct for unsigned integer variable modifiers.
 *
 * @author Johann Böhler
 */
abstract class AbstractIntegerModifier<T extends Variable> extends VariableModifier<T> {

    /**
     * The logging instance for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractIntegerModifier.class);

    /**
     * The minimum allowed number for the resulting modified variable.
     */
    protected final Long minimum;

    /**
     * The maximum allowed number for the resulting modified variable.
     */
    protected final Long maximum;

    /**
     * The minimal step by which a variable will be incremented.
     */
    protected final Long minimumStep;

    /**
     * The maximal step by which a variable will be incremented.
     */
    protected final Long maximumStep;

    /**
     * Constructs a new instance of this class.
     *
     * @param params the parameter for this modifier
     */
    protected AbstractIntegerModifier(List<AbstractModifier.Param> params) {
        super(params);

        Preconditions.checkArgument(this.parameter.containsKey("minimum") && AbstractIntegerModifier.isNumeric(this.parameter.get("minimum")), "minimum not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("maximum") && AbstractIntegerModifier.isNumeric(this.parameter.get("maximum")), "maximum not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("minimumStep") && AbstractIntegerModifier.isNumeric(this.parameter.get("minimumStep")), "minimum step not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("maximumStep") && AbstractIntegerModifier.isNumeric(this.parameter.get("maximumStep")), "maximum step not set or not a number");

        try {
            this.minimum = Long.parseLong(this.parameter.get("minimum"));
            this.maximum = Long.parseLong(this.parameter.get("maximum"));

            this.minimumStep = Long.parseLong(this.parameter.get("minimumStep"));
            this.maximumStep = Long.parseLong(this.parameter.get("maximumStep"));

            Preconditions.checkArgument(minimum >= 0, "minimum should not be negative");
            Preconditions.checkArgument(maximum >= 0, "maximum should not be negative");

            Preconditions.checkArgument(minimum <= 4294967295L, "minimum should not exceed 2^32-1 (4294967295 decimal)");
            Preconditions.checkArgument(maximum <= 4294967295L, "maximum should not exceed 2^32-1 (4294967295 decimal)");
        } catch (final NumberFormatException e) {
            LOG.error("one of the parameters in {} for this modifier instance exceeds the long range between {} and {}", this.parameter, Long.MIN_VALUE, Long.MAX_VALUE);
            throw new IllegalArgumentException("one of the parameters exceeds the legal long value range", e);
        }
    }

    /**
     * Increments the current value by a random number between the minimum and maximum step.
     * <p/>
     * An overflow can occur and will be considered in the minimum and maximum interval.
     *
     * @param currentValue the current value to modify
     * @param minimum      {@link #minimum}
     * @param maximum      {@link #maximum}
     * @param minimumStep  {@link #minimumStep}
     * @param maximumStep  {@link #maximumStep}
     * @return the modified variable value
     */
    protected long modify(long currentValue, final long minimum, final long maximum, final long minimumStep, final long maximumStep) {
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

        return newValue;
    }

    @Override
    public abstract T modify(T variable);

    /**
     * Checks if the specified string is number.
     * <p/>
     * The characters in the string must all be decimal digits, except the first character may be an ASCII minus sign '-'
     * (\u002D') to indicate a negative value or an ASCII plus sign '+' ('\u002B') to indicate a positive value.
     *
     * @param string a {@link String} containing a number to be parsed
     * @return {@code true} if the specified string is a number, otherwise {@code false}
     */
    protected static boolean isNumeric(final String string) {
        return string.matches("[+-]?[0-9]+");
    }
}
