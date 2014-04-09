package com.oneandone.network.snmpman.modifier;

import com.google.common.base.Preconditions;
import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.Integer32;

import java.util.List;

/**
 * TODO
 *
 * @author Johann Böhler
 */
public class Integer32Modifier extends VariableModifier<Integer32> {

    /**
     * The logging instance for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Integer32Modifier.class);

    /**
     * The minimum allowed number for the resulting modified variable.
     */
    protected final Integer minimum;

    /**
     * The maximum allowed number for the resulting modified variable.
     */
    protected final Integer maximum;

    /**
     * The minimal step by which a variable will be incremented.
     */
    protected final Integer minimumStep;

    /**
     * The maximal step by which a variable will be incremented.
     */
    protected final Integer maximumStep;

    /**
     * Constructs a new instance of this class.
     *
     * @param params the parameter for this modifier
     */
    public Integer32Modifier(List<AbstractModifier.Param> params) {
        super(params);

        Preconditions.checkArgument(this.parameter.containsKey("minimum") && AbstractIntegerModifier.isNumeric(this.parameter.get("minimum")), "minimum not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("maximum") && AbstractIntegerModifier.isNumeric(this.parameter.get("maximum")), "maximum not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("minimumStep") && AbstractIntegerModifier.isNumeric(this.parameter.get("minimumStep")), "minimum step not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("maximumStep") && AbstractIntegerModifier.isNumeric(this.parameter.get("maximumStep")), "maximum step not set or not a number");

        try {
            this.minimum = Integer.parseInt(this.parameter.get("minimum"));
            this.maximum = Integer.parseInt(this.parameter.get("maximum"));

            this.minimumStep = Integer.parseInt(this.parameter.get("minimumStep"));
            this.maximumStep = Integer.parseInt(this.parameter.get("maximumStep"));
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
    protected int modify(int currentValue, final int minimum, final int maximum, final int minimumStep, final int maximumStep) {
        if (currentValue < minimum || currentValue > maximum) {
            currentValue = minimum;
        }
        int step = (int) (Math.round(Math.random() * (maximumStep - minimumStep)) + minimumStep);

        int stepUntilMaximum = maximum - currentValue;
        int newValue;
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
    public Integer32 modify(final Integer32 variable) {
        final int newValue = this.modify(variable.getValue(), minimum, maximum, minimumStep, maximumStep);
        LOG.trace("Counter32 variable {} will be tuned to {}", variable.getValue(), newValue);
        return new Integer32(newValue);
    }
}
