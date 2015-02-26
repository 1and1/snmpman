package com.oneandone.network.snmpman.configuration.modifier;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.Integer32;

import java.util.Map;
import java.util.Properties;

/**
 * TODO
 *
 * @author Johann Böhler
 */
public class Integer32Modifier implements VariableModifier<Integer32> {

    /**
     * The logging instance for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Integer32Modifier.class);

    /**
     * The minimum allowed number for the resulting modified variable.
     */
    protected Integer minimum;

    /**
     * The maximum allowed number for the resulting modified variable.
     */
    protected Integer maximum;

    /**
     * The minimal step by which a variable will be incremented.
     */
    protected Integer minimumStep;

    /**
     * The maximal step by which a variable will be incremented.
     */
    protected Integer maximumStep;

    @Override
    public void init(final Properties properties) {
        /*Preconditions.checkArgument(properties.containsKey("minimum") && AbstractIntegerModifier.isNumeric(properties.get("minimum")), "minimum not set or not a number");
        Preconditions.checkArgument(properties.containsKey("maximum") && AbstractIntegerModifier.isNumeric(properties.get("maximum")), "maximum not set or not a number");
        Preconditions.checkArgument(properties.containsKey("minimumStep") && AbstractIntegerModifier.isNumeric(properties.get("minimumStep")), "minimum step not set or not a number");
        Preconditions.checkArgument(properties.containsKey("maximumStep") && AbstractIntegerModifier.isNumeric(properties.get("maximumStep")), "maximum step not set or not a number");

        try {
            this.minimum = Integer.parseInt(properties.get("minimum"));
            this.maximum = Integer.parseInt(properties.get("maximum"));

            this.minimumStep = Integer.parseInt(properties.get("minimumStep"));
            this.maximumStep = Integer.parseInt(properties.get("maximumStep"));
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("one of the parameters exceeds the legal long value range", e);
        }*/
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
