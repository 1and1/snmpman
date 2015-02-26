package com.oneandone.network.snmpman.configuration.type;

import java.util.Properties;

/**
 * TODO
 */
public class ModifierProperties extends Properties {

    public Long getLong(final String key) {
        if (this.containsKey(key)) {
            final Object value = this.get(key);
            if (value instanceof Number) {
                return ((Number) value).longValue();
            } else {
                throw new ClassCastException("property \"" + key + "\" is not a number");   
            }
        }
        return null;
    }
    
}
