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
 * The configuration file is a {@code XML} representation of the {@link Configuration} class.
 * <p/>
 * All available command-line options are documented in the {@link CommandLineOptions} class.
 */
@Slf4j
public final class Snmpman {

    @Getter private final List<SnmpmanAgent> agents;
    
    private Snmpman(final List<SnmpmanAgent> agents) {
        this.agents = agents;
    }
    
    /**
     * The application entry-point.
     * <p/>
     * All available command-line arguments are documented in the {@link CommandLineOptions} class.
     * <p/>
     * If illegal command-line options were specified for execution, a usage help message will be printed out
     * on the {@link System#out} stream and the application will terminate. Otherwise the configuration will
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

    public static Snmpman start(final File configurationFile) {
        Preconditions.checkNotNull(configurationFile, "the configuration file may not be null");
        Preconditions.checkArgument(configurationFile.exists() && configurationFile.isFile(), "configuration does not exist or is not a file");

        log.debug("started with configuration in path {}", configurationFile.getAbsolutePath());
        try {
            final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            final AgentConfiguration[] configurations = mapper.readValue(configurationFile, AgentConfiguration[].class);
            
            return Snmpman.start(Collections.unmodifiableList(
                    Arrays.stream(configurations).map(configuration -> new SnmpmanAgent(configuration)).collect(Collectors.toList())
            ));
        } catch (final IOException e) {
            throw new InitializationException("could not parse configuration at path: " + configurationFile.getAbsolutePath(), e);
        }
    }
    
    public static Snmpman start(final List<SnmpmanAgent> agents) {
        final Snmpman snmpman = new Snmpman(agents);
        snmpman.start();
        return snmpman;
    }
    
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
    
    public void stop() {
        agents.forEach(com.oneandone.network.snmpman.SnmpmanAgent::stop);
    }
}
