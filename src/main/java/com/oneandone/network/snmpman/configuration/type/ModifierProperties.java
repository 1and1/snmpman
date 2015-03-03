package com.oneandone.network.snmpman.configuration.type;

import com.google.common.primitives.UnsignedLong;

import java.util.Optional;
import java.util.Properties;

/** The modifier properties as used in {@link com.oneandone.network.snmpman.configuration.Device} for initialization. */
public class ModifierProperties extends Properties {

    /**
     * Returns the integer value for the specified property {@code key}.
     *  
     * @param key the property key
     * @return the integer value for the specified key
     * @throws java.lang.ClassCastException thrown if the value for the specified key could not be casted
     */
    public Integer getInteger(final String key) {
        final Optional<Number> number = getNumber(key);
        return number.isPresent() ? number.get().intValue() : null;
    }

    /**
     * Returns the long value for the specified property {@code key}.
     *
     * @param key the property key
     * @return the long value for the specified key
     * @throws java.lang.ClassCastException thrown if the value for the specified key could not be casted
     */
    public Long getLong(final String key) {
        final Optional<Number> number = getNumber(key);
        return number.isPresent() ? number.get().longValue() : null;
    }

    /**
     * Returns the unsigned long value for the specified property {@code key}.
     *
     * @param key the property key
     * @return the unsigned long value for the specified key
     * @throws java.lang.ClassCastException thrown if the value for the specified key could not be casted
     */
    public UnsignedLong getUnsignedLong(final String key) {
        final Optional<Number> number = getNumber(key);
        return number.isPresent() ? UnsignedLong.valueOf(number.get().longValue()) : null;
    }

    /**
     * Returns the number for the specified property {@code key}.
     *  
     * @param key the property key
     * @return the number for the specified key
     * @throws java.lang.ClassCastException thrown if the value for the specified key could not be casted
     */
    private Optional<Number> getNumber(final String key) {
        if (this.containsKey(key)) {
            final Object value = this.get(key);
            if (value instanceof Number) {
                return Optional.of((Number) value);
            } else {
                throw new ClassCastException("property \"" + key + "\" is not a number");
            }
        }
        return Optional.empty();
    }
}
