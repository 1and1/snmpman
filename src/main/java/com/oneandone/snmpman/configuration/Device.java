package com.oneandone.snmpman.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.oneandone.snmpman.configuration.modifier.Modifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** 
 * Represents a device type. 
 * <p />
 * You can find example configurations within the test resources of this project.
 */
@ToString(of = "name") @EqualsAndHashCode
public class Device {

    /** 
     * The device name.
     * 
     * @return the device name 
     */
    @Getter private final String name;
    
    /**
     * The unmodifiable list of modifier definitions.
     * <p />
     * See {@link Modifier} and {@link com.oneandone.snmpman.configuration.modifier.VariableModifier}.
     *
     * @return list of modifier definitions
     */
    @Getter private final List<Modifier> modifiers;

    /**
     * Constructs a new device type.
     *
     * @param name the name of the device
     * @param modifiers the modifiers
     */
    public Device(@JsonProperty("name") final String name, @JsonProperty("modifiers") final Modifier[] modifiers) {
        this.name = name;
        if (modifiers != null) {
            this.modifiers = Collections.unmodifiableList(Arrays.asList(modifiers));
        } else {
            this.modifiers = Collections.emptyList();
        }
    }
    
}
