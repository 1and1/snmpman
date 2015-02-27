package com.oneandone.network.snmpman.configuration.modifier;

import com.google.common.base.Optional;
import com.oneandone.network.snmpman.configuration.type.ModifierProperties;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.Integer32;

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

    /** The minimum allowed number for the resulting modified variable. */
    @Getter private Integer minimum;

    /** The maximum allowed number for the resulting modified variable. */
    @Getter private Integer maximum;

    /** The minimal step by which a variable will be incremented. */
    @Getter private Integer minimumStep;

    /** The maximal step by which a variable will be incremented. */
    @Getter private Integer maximumStep;

    @Override
    public void init(final ModifierProperties properties) {
        try {
            this.minimum = Optional.fromNullable(properties.getInteger("minimum")).or(Integer.MIN_VALUE);
            this.maximum = Optional.fromNullable(properties.getInteger("maximum")).or(Integer.MAX_VALUE);

            this.minimumStep = Optional.fromNullable(properties.getInteger("minimumStep")).or(-1);
            this.maximumStep = Optional.fromNullable(properties.getInteger("maximumStep")).or(1);
        } catch (final NumberFormatException e) {
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
