package com.oneandone.network.snmpman.modifier;

import com.oneandone.network.snmpman.configuration.device.AbstractModifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.smi.Gauge32;

import java.util.List;

/**
 * This modifier instance modifies {@link Gauge32} variables by the {@link #modify(org.snmp4j.smi.Gauge32)} method.
 *
 * @author Johann Böhler
 */
public class Gauge32Modifier extends AbstractIntegerModifier<Gauge32> {

    /**
     * The logging instance for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Counter32Modifier.class);

    /**
     * Constructs a new {@link Gauge32} modifier.
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
    public Gauge32Modifier(final List<AbstractModifier.Param> params) {
        super(params);
    }

    @Override
    public Gauge32 modify(final Gauge32 variable) {
        final long newValue = this.modify(variable.getValue(), minimum, maximum, minimumStep, maximumStep);
        LOG.trace("Gauge32 variable {} will be tuned to {}", variable.getValue(), newValue);
        return new Gauge32(newValue);
    }

}
