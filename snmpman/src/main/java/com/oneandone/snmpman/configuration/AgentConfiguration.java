package com.oneandone.snmpman.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.oneandone.snmpman.Snmpman;
import com.oneandone.snmpman.SnmpmanAgent;
import com.oneandone.snmpman.configuration.modifier.Modifier;
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
import java.util.Optional;

/** Representation of the configuration for a {@link SnmpmanAgent}. */
@Slf4j
@ToString(exclude = "community") @EqualsAndHashCode
public class AgentConfiguration {

    /** The device factory. */
    private static final DeviceFactory DEVICE_FACTORY = new DeviceFactory();

    /**
     * Returns the name of the agent.
     * <br>
     * If the {@code name} wasn't set on construction, the name will be defined by the {@link #address}.
     *
     * @return the name of the agent
     */
    @Getter private final String name;

    /**
     * Returns the address of the agent.
     *
     * @return the address of the agent
     */
    @Getter private final Address address; // e.g. 127.0.0.1/8080

    /** The device configuration file path. */
    private final File deviceConfiguration;

    /**
     * Returns the {@link Device} representation for the agent.
     * <br>
     * Will be set to {@link DeviceFactory#DEFAULT_DEVICE} by default.
     *
     * @return the device representation for the agent
     */
    @Getter(lazy=true) private final Device device = initializeDevice(); // e.g. cisco

    /**
     * Returns the base walk file (e.g. dump of SNMP walks) for the agent.
     *
     * @return the base walk file for the agent
     */
    @Getter private final File walk; // real walk: /opt/snmpman/...

    /**
     * Returns the community for the agent.
     * <br>
     * The community is {@code public} by default.
     *
     * @return the community for the agent
     */
    @Getter private final String community; // e.g. 'public'

    /**
     * The device factory creates all {@link Device} representations.
     * <br>
     * It is required to instantiate the representations only one time application-wide.
     */
    // TODO each Snmpman instance should have its own device factory instance
    public static class DeviceFactory {

        /** The default device which will be returned if no configuration specified. */
        public static final Device DEFAULT_DEVICE = new Device("default", new Modifier[0], null);

        /** The map of all available devices. */
        private final Map<File, Device> devices = new HashMap<>();

        /**
         * Returns the device representation for the specified {@code path}.
         * <br>
         * If the {@code path} is {@code null}, or the parsing of the configuration failed, the {@link #DEFAULT_DEVICE} will be returned instead.
         *
         * @param path the path of the device configuration
         * @return the {@link Device} representation for the specified configuration in {@code path}
         */
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

    /**
     * Constructs a new agent configuration.
     * <br>
     * The list of agent configurations will be parsed from within {@link Snmpman}.
     *
     * @param name the name of the agent or {@code null} to set the address as the name
     * @param deviceConfiguration the device configuration or {@code null} will set it to
     *                            {@link DeviceFactory#DEFAULT_DEVICE}
     * @param walk the base walk file (e.g. dump of SNMP walks)
     * @param ip the IP the agent should bind to
     * @param port the port of the agent
     * @param community the community of the agent or {@code null} will set it to {@code public}
     */
    public AgentConfiguration(@JsonProperty(value = "name", required = false) final String name,
                              @JsonProperty(value = "device", required = false) final File deviceConfiguration,
                              @JsonProperty(value = "walk", required = true) final File walk,
                              @JsonProperty(value = "ip", required = true) final String ip,
                              @JsonProperty(value = "port", required = true) final int port,
                              @JsonProperty(value = "community", required = false) final String community) {
        this.name = Optional.ofNullable(name).orElse(ip + ":" + port);
        this.address = GenericAddress.parse(ip + "/" + port);

        this.deviceConfiguration = deviceConfiguration;
        this.walk = walk;

        this.community = Optional.ofNullable(community).orElse("public");
    }

    /**
     * FIXME
     * Lazy initialization of {@link #device} required as nested use of {@link ObjectMapper}
     * lead to an exception. Try to remove this call with a version update of {@code jackson}.
     *
     * @return the lazy initialized device
     */
    private Device initializeDevice() {
        return DEVICE_FACTORY.getDevice(deviceConfiguration);
    }


}
