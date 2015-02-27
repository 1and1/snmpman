package com.oneandone.network.snmpman.configuration.type;

import com.google.common.primitives.UnsignedLong;

import java.util.Optional;
import java.util.Properties;

/**
 * TODO
 */
public class ModifierProperties extends Properties {

    public Integer getInteger(final String key) {
        final Optional<Number> number = getNumber(key);
        return number.isPresent() ? number.get().intValue() : null;
    }
    
    public Long getLong(final String key) {
        final Optional<Number> number = getNumber(key);
        return number.isPresent() ? number.get().longValue() : null;
    }
    
    public UnsignedLong getUnsignedLong(final String key) {
        final Optional<Number> number = getNumber(key);
        return number.isPresent() ? UnsignedLong.valueOf(number.get().longValue()) : null;
    }
    
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
