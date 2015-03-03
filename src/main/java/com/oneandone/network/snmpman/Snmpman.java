package com.oneandone.network.snmpman;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.oneandone.network.snmpman.configuration.AgentConfiguration;
import com.oneandone.network.snmpman.exception.InitializationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import javax.xml.bind.UnmarshalException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the main-class for this application.
 * <p/>
 * A mandatory argument for execution of the {@link #main(String...)} method is the path to the configuration file.
 * See {@link com.oneandone.network.snmpman.CommandLineOptions} for information on available command line options.
 * <p />
 * Each configuration list item represents an instance of the {@link com.oneandone.network.snmpman.configuration.AgentConfiguration}.
 * The constructor {@link com.oneandone.network.snmpman.configuration.AgentConfiguration#AgentConfiguration(String, java.io.File, java.io.File, String, int, String)}
 * lists all available properties, which may or may not be required.
 * <p />
 * An entry may look like the following:
 * <pre>
 * {@code         
 *     - name: "example1"
 *       device: "src/test/resources/configuration/cisco.yaml"
 *       walk: "src/test/resources/configuration/example.txt"
 *       ip: "127.0.0.1"
 *       port: 10000
 * }
 * </pre>
 * You can find more example within the test resources of this project.
 * <p /> 
 * The configuration {@code YAML} file defines a list of all agents that should be simulated by the {@code Snmpman}.
 */
@Slf4j
public final class Snmpman {

    /**
     * Returns the list of SNMP agents for {@code this} instance.
     * 
     * @return the list of SNMP agents 
     */
    @Getter private final List<SnmpmanAgent> agents;

    /**
     * Constructs an instance by the specified list of agents.
     *
     * @param agents the agents for {@code this} instance
     */
    private Snmpman(final List<SnmpmanAgent> agents) {
        this.agents = agents;
    }
    
    /**
     * The application entry-point.
     * <p/>
     * All available command-line arguments are documented in the {@link CommandLineOptions} class.
     * <p/>
     * If illegal command-line options were specified for execution, a usage help message will be printed out
     * on the {@link System#err} stream and the application will terminate. Otherwise the configuration will
     * be read and used for execution.
     *
     * @param args the command-line arguments
     */
    public static void main(final String... args) {
        final CommandLineOptions commandLineOptions = new CommandLineOptions();
        final CmdLineParser cmdLineParser = new CmdLineParser(commandLineOptions);
        try {
            cmdLineParser.parseArgument(args);

            if (commandLineOptions.isShowHelp()) {
                cmdLineParser.printUsage(System.out);
            } else {
                Snmpman.start(commandLineOptions.getConfigurationFile());
            }
        } catch (final InitializationException | CmdLineException e) {
            log.error("could not parse or process command-line arguments", e);
            if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                System.err.println("could not start application because of following error: ");
                System.err.println(e.getMessage());
            } else {
                System.err.println("failed to start application, check the logs for more information");
            }
            cmdLineParser.printUsage(System.err);
        }
    }

    /**
     * Creates an {@code Snmpman} instance by the specified configuration in the {@code configurationFile} and starts all agents.
     *
     * @param configurationFile the configuration
     * @return the {@code Snmpman} instance
     * @throws com.oneandone.network.snmpman.exception.InitializationException thrown if any agent, as specified in the configuration, could not be started
     */
    public static Snmpman start(final File configurationFile) {
        Preconditions.checkNotNull(configurationFile, "the configuration file may not be null");
        Preconditions.checkArgument(configurationFile.exists() && configurationFile.isFile(), "configuration does not exist or is not a file");

        log.debug("started with configuration in path {}", configurationFile.getAbsolutePath());
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            final AgentConfiguration[] configurations = mapper.readValue(configurationFile, AgentConfiguration[].class);
            
            return Snmpman.start(Arrays.stream(configurations).map(configuration -> new SnmpmanAgent(configuration)).collect(Collectors.toList()));
        } catch (final IOException e) {
            throw new InitializationException("could not parse configuration at path: " + configurationFile.getAbsolutePath(), e);
        }
    }

    /**
     * Creates an {@code Snmpman} instance by the specified list of agents and starts all agents.
     *
     * @param agents the list of agents
     * @return the {@code Snmpman} instance
     * @throws com.oneandone.network.snmpman.exception.InitializationException thrown if any agent, as specified in the configuration, could not be started
     */
    public static Snmpman start(final List<SnmpmanAgent> agents) {
        final Snmpman snmpman = new Snmpman(Collections.unmodifiableList(agents));
        snmpman.start();
        return snmpman;
    }

    /**
     * Starts all agents as defined in {@link #agents}.
     * 
     * @throws com.oneandone.network.snmpman.exception.InitializationException thrown if any agent could not be started
     */
    private void start() {
        log.debug("starting to load agents");
        for (final SnmpmanAgent agent : agents) {
            try {
                agent.execute();
            } catch (final IOException e) {
                throw new InitializationException("failed to start agent \"" + agent.getName() + "\"", e);
            }
        }
        log.debug("all agents initialized");
    }
    
    /** Stops all agents as defined in {@link #agents}. */
    public void stop() {
        agents.forEach(com.oneandone.network.snmpman.SnmpmanAgent::stop);
    }
}
