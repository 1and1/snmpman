package com.oneandone.network.snmpman.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Optional;
import com.oneandone.network.snmpman.configuration.modifier.Modifier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 */
@Slf4j @ToString(exclude = "community") @EqualsAndHashCode
public class AgentConfiguration {

    private static final DeviceFactory DEVICE_FACTORY = new DeviceFactory();

    public static class DeviceFactory {

        public static final Device DEFAULT_DEVICE = new Device("default", new Modifier[0]);

        private final Map<File, Device> devices = new HashMap<>(1);

        public Device getDevice(final File path) {
            if (path == null) {
                return DEFAULT_DEVICE;
            }

            if (devices.containsKey(path)) {
                return devices.get(path);
            } else {
                try {
                    final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    final Device device = mapper.readValue(path, Device.class);
                    devices.put(path, device);
                    return device;
                } catch (final IOException e) {
                    log.error("could not load device in path \"" + path.getAbsolutePath() + "\"", e);
                    return DEFAULT_DEVICE;
                }
            }
        }
    }

    @Getter private final String name;
    @Getter private final Address address; // e.g. 127.0.0.1/8080

    private final File deviceConfiguration;
    @Getter(lazy=true) private final Device device = initializeDevice(); // e.g. cisco
    @Getter private final File walk; // real walk: /opt/snmpman/...

    @Getter private final String community; // e.g. 'public'

    public AgentConfiguration(@JsonProperty(value = "name", required = false) final String name,
                              @JsonProperty(value = "device", required = false) final File deviceConfiguration,
                              @JsonProperty(value = "walk", required = true) final File walk,
                              @JsonProperty(value = "ip", required = true) final String ip,
                              @JsonProperty(value = "port", required = true) final int port,
                              @JsonProperty(value = "community", defaultValue = "public") final String community) {
        this.name = Optional.fromNullable(name).or(ip + ":" + port);
        this.address = GenericAddress.parse(ip + "/" + port);
        
        this.deviceConfiguration = deviceConfiguration;
        this.walk = walk;

        this.community = community;
    }

    /**
     * FIXME
     * Lazy initialization of {@link #device} required as nested use of {@link com.fasterxml.jackson.databind.ObjectMapper}
     * lead to an exception. Try to remove this call with a version update of {@code jackson}.
     *  
     * @return the lazy initialized device
     */
    private Device initializeDevice() {
        return DEVICE_FACTORY.getDevice(deviceConfiguration);         
    }
    

}
