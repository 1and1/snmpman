package com.oneandone.network.snmpman.modifier;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedLong;
import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.Counter64;

import java.util.List;

/**
 * This modifier instance modifies {@link Counter64} variables by the {@link #modify(org.snmp4j.smi.Counter64)} method.
 *
 * @author Johann Böhler
 */
public class Counter64Modifier extends VariableModifier<Counter64> {

    /**
     * The logging instance for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Counter64Modifier.class);

    /**
     * The minimum allowed number for the resulting modified variable.
     */
    private final UnsignedLong minimum;

    /**
     * The maximum allowed number for the resulting modified variable.
     */
    private final UnsignedLong maximum;

    /**
     * The minimal step by which a variable will be incremented.
     */
    private final UnsignedLong minimumStep;

    /**
     * The maximal step by which a variable will be incremented.
     */
    private final UnsignedLong maximumStep;

    /**
     * Constructs a new {@link Counter64} modifier.
     * <p/>
     * The expected parameters are
     * <table>
     * <tr>
     * <th>Parameter</th>
     * <th>Description</th>
     * </tr>
     * <tr>
     * <td>minimum</td>
     * <td>The minimum allowed number for the resulting modified variable</td>
     * </tr>
     * <tr>
     * <td>maximum</td>
     * <td>The maximum allowed number for the resulting modified variable</td>
     * </tr>
     * <tr>
     * <td>minimumStep</td>
     * <td>The minimal step by which a variable will be incremented</td>
     * </tr>
     * <tr>
     * <td>maximumStep</td>
     * <td>The maximal step by which a variable will be incremented</td>
     * </tr>
     * </table>
     *
     * @param params the parameters for this modifier
     * @throws IllegalArgumentException if one of the parameters is not set or is not a valid value: {@code minimum}, {@code maximum}, {@code minimumStep}, {@code maximumStep}
     */
    public Counter64Modifier(final List<AbstractModifier.Param> params) {
        super(params);

        Preconditions.checkArgument(this.parameter.containsKey("minimum") && Counter64Modifier.isNumeric(this.parameter.get("minimum")), "minimum not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("maximum") && Counter64Modifier.isNumeric(this.parameter.get("maximum")), "maximum not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("minimumStep") && Counter64Modifier.isNumeric(this.parameter.get("minimumStep")), "minimum step not set or not a number");
        Preconditions.checkArgument(this.parameter.containsKey("maximumStep") && Counter64Modifier.isNumeric(this.parameter.get("maximumStep")), "maximum step not set or not a number");

        try {
            this.minimum = UnsignedLong.valueOf(this.parameter.get("minimum"));
            this.maximum = UnsignedLong.valueOf(this.parameter.get("maximum"));

            this.minimumStep = UnsignedLong.valueOf(this.parameter.get("minimumStep"));
            this.maximumStep = UnsignedLong.valueOf(this.parameter.get("maximumStep"));
        } catch (final IllegalArgumentException e) {
            LOG.error("one of the parameters in {} for this modifier instance exceeds the unsigned long range between {} and {}", this.parameter, UnsignedLong.ZERO, UnsignedLong.MAX_VALUE);
            throw new IllegalArgumentException("one of the parameters exceeds the legal unsigned long value range", e);
        }
    }

    @Override
    public Counter64 modify(final Counter64 variable) {
        UnsignedLong currentValue = UnsignedLong.valueOf(variable.toString());
        if (currentValue.compareTo(minimum) < 0 || currentValue.compareTo(maximum) > 0) {
            currentValue = minimum;
        }

        final UnsignedLong step = UnsignedLong.valueOf((long) (Math.random() * maximumStep.minus(minimumStep).longValue())).plus(minimumStep);
        final UnsignedLong newValue = currentValue.plus(step);

        LOG.trace("Counter64 variable {} will be tuned to {}", variable.toString(), newValue);
        return new Counter64(newValue.longValue());
    }

    /**
     * Checks if the specified string is number.
     * <p/>
     * The characters in the string must all be decimal digits, except the first character may be an ASCII minus sign '-'
     * (\u002D') to indicate a negative value or an ASCII plus sign '+' ('\u002B') to indicate a positive value.
     *
     * @param string a {@link String} containing a number to be parsed
     * @return {@code true} if the specified string is a number, otherwise {@code false}
     */
    private static boolean isNumeric(final String string) {
        return string.matches("[+-]?[0-9]+");
    }

}
