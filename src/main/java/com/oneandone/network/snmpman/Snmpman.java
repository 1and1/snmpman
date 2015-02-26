package com.oneandone.network.snmpman;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.UnmarshalException;
import java.io.File;
import java.io.IOException;

/**
 * This is the main-class for this application.
 * <p/>
 * A mandatory argument for execution of the {@link #main(String...)} method is the path to the configuration file.
 * The configuration file is a {@code XML} representation of the {@link Configuration} class.
 * <p/>
 * All available command-line options are documented in the {@link CommandLineOptions} class.
 *
 * @author Johann Böhler
 */
public final class Snmpman {

    /** The logging instance for this class. */
    private static final Logger LOG = LoggerFactory.getLogger(Snmpman.class);

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

            LOG.debug("starting to load agents");
            for (SnmpmanAgent agent : commandLineOptions.agents) {
                agent.execute();
            }
            LOG.debug("all agents initialized");
	        
        } catch (final Exception e) {
            if (e.getCause() != null && e.getCause() instanceof UnmarshalException) {
                System.err.println("Configuration could not be parsed. Check the logs for more information.");
            }
            LOG.error("could not parse or process command-line arguments", e);
            cmdLineParser.printUsage(System.out);
        }
    }

    /** The command-line options for this application. */
    private static final class CommandLineOptions {

        /** The {@code Snmpman} configuration. */
        private SnmpmanAgent[] agents;

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
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                this.agents = mapper.readValue(configurationFile, SnmpmanAgent[].class);
            } catch (final IOException e) {
                throw new IllegalStateException("could not parse configuration at path: " + configurationFile.getAbsolutePath());
            }
        }

    }


}
