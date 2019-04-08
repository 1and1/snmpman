package com.oneandone.snmpman.configuration.modifier;

import com.google.common.primitives.UnsignedLong;
import com.oneandone.snmpman.configuration.type.ModifierProperties;
import lombok.Getter;
import org.snmp4j.smi.Counter64;

import java.util.Optional;

/** This modifier instance modifies {@link Counter64} variables. */
public class Counter64Modifier implements VariableModifier<Counter64> {

    /** The minimum allowed number for the resulting modified variable. */
    @Getter private UnsignedLong minimum;

    /** The maximum allowed number for the resulting modified variable. */
    @Getter private UnsignedLong maximum;

    /** The minimal step by which a variable will be incremented. */
    @Getter private UnsignedLong minimumStep;

    /** The maximal step by which a variable will be incremented. */
    @Getter private UnsignedLong maximumStep;

    @Override
    public void init(final ModifierProperties properties) {
        this.minimum = Optional.ofNullable(properties.getUnsignedLong("minimum")).orElse(UnsignedLong.ZERO);
        this.maximum = Optional.ofNullable(properties.getUnsignedLong("maximum")).orElse(UnsignedLong.MAX_VALUE);

        this.minimumStep = Optional.ofNullable(properties.getUnsignedLong("minimumStep")).orElse(UnsignedLong.ZERO);
        this.maximumStep = Optional.ofNullable(properties.getUnsignedLong("maximumStep")).orElse(UnsignedLong.ONE);
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
