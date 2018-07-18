package com.oneandone.snmpman;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.oneandone.snmpman.configuration.AgentConfiguration;
import com.oneandone.snmpman.exception.InitializationException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.snmp4j.agent.BaseAgent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is the main-class for this application.
 * <br>
 * A mandatory argument for execution of the {@link #main(String...)} method is the path to the configuration file.
 * See {@link com.oneandone.snmpman.CommandLineOptions} for information on available command line options.
 * <br>
 * Each configuration list item represents an instance of the {@link com.oneandone.snmpman.configuration.AgentConfiguration}.
 * The constructor {@link com.oneandone.snmpman.configuration.AgentConfiguration#AgentConfiguration(String, java.io.File, java.io.File, String, int, String)}
 * lists all available properties, which may or may not be required.
 * <br>
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
 * <br>
 * The configuration {@code YAML} file defines a list of all agents that should be simulated by the {@code Main}.
 */
@Slf4j
public final class Main {

    /**
     * The application entry-point.
     * <br>
     * All available command-line arguments are documented in the {@link com.oneandone.snmpman.CommandLineOptions} class.
     * <br>
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
}
