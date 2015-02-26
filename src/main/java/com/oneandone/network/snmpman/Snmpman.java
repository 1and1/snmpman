package com.oneandone.network.snmpman;

import lombok.extern.slf4j.Slf4j;
import org.kohsuke.args4j.CmdLineParser;

import javax.xml.bind.UnmarshalException;

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

            log.debug("starting to load agents");
            for (final SnmpmanAgent agent : commandLineOptions.getAgents()) {
                agent.execute();
            }
            log.debug("all agents initialized");
        } catch (final Exception e) {
            if (e.getCause() != null && e.getCause() instanceof UnmarshalException) {
                System.err.println("Configuration could not be parsed. Check the logs for more information.");
            }
            log.error("could not parse or process command-line arguments", e);
            cmdLineParser.printUsage(System.out);
        }
    }

}
