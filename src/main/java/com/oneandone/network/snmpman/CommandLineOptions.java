package com.oneandone.network.snmpman;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.oneandone.network.snmpman.configuration.AgentConfiguration;
import lombok.Getter;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** The command-line options for this application. */
public final class CommandLineOptions {

    /** The {@code Snmpman} configuration. */
    @Getter private List<SnmpmanAgent> agents;

    /**
     * Sets the {@code Snmpman} configuration by the specified path to the configuration file.
     *
     * @param configurationFile the configuration file
     * @throws java.lang.NullPointerException if the specified configuration file is null
     * @throws java.lang.IllegalArgumentException if the specified configuration does not exist or ist not a file
     * @throws java.lang.IllegalStateException if the configuration could not be parsed
     */
    @SuppressWarnings("UnusedDeclaration")
    @Option(name = "-c", aliases = "--configuration", usage = "the path to the configuration XML", required = true)
    private void setConfiguration(final File configurationFile) {
        Preconditions.checkNotNull(configurationFile, "the configuration file may not be null");
        Preconditions.checkArgument(configurationFile.exists() && configurationFile.isFile(), "configuration does not exist or is not a file");

        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            final AgentConfiguration[] configurations = mapper.readValue(configurationFile, AgentConfiguration[].class);
            
            this.agents = Collections.unmodifiableList(
                    Arrays.stream(configurations).map(configuration -> new SnmpmanAgent(configuration)).collect(Collectors.toList())
            );
        } catch (final IOException e) {
            throw new IllegalStateException("could not parse configuration at path: " + configurationFile.getAbsolutePath(), e);
        }
    }
}
