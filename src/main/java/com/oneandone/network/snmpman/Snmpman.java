package com.oneandone.network.snmpman;

import com.google.common.base.Preconditions;
import com.oneandone.network.snmpman.configuration.Agent;
import com.oneandone.network.snmpman.configuration.Configuration;
import com.oneandone.network.snmpman.configuration.Device;
import com.oneandone.network.snmpman.configuration.device.DeviceType;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

    /**
     * The logging instance for this class.
     */
    private static transient Logger LOG = LoggerFactory.getLogger(Snmpman.class);

    /**
     * The {@code Snmpman} configuration.
     */
    private final Configuration configuration;

    /**
     * Hidden constructor for the creation of a {@code Snmpman} instance.
     */
    Snmpman(final Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Reads the application properties (e.g. {@code artifactId}, {@code version} ...) and logs them on the specified {@link Logger} instance for this class.
     */
    private static void readApplicationProperties() {
        final Properties mavenProperties = new Properties();
        try (final InputStream mavenPropertiesStream = ClassLoader.getSystemResourceAsStream("maven.properties")) {
            mavenProperties.load(mavenPropertiesStream);
            LOG.debug(String.format("application %s:%s:%s started", mavenProperties.getProperty("maven.groupId"), mavenProperties.getProperty("maven.artifactId"), mavenProperties.getProperty("maven.version")));
        } catch (final IOException e) {
            LOG.error("could not read maven properties", e);
        }
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
        Snmpman.readApplicationProperties();

        final CommandLineOptions commandLineOptions = new CommandLineOptions();
        final CmdLineParser cmdLineParser = new CmdLineParser(commandLineOptions);
        try {
            cmdLineParser.parseArgument(args);
        } catch (final Exception e) {
            if (e.getCause() != null && e.getCause() instanceof UnmarshalException) {
                System.err.println("Configuration could not be parsed. Check the logs for more information.");
            }
            LOG.error("could not parse command-line arguments", e);
            cmdLineParser.printUsage(System.out);
            return;
        }

        final Snmpman snmpman = new Snmpman(commandLineOptions.configuration);
        final Map<String, DeviceType> deviceTypeMap = snmpman.loadDeviceTypes();
        LOG.debug("starting to load agents");
        snmpman.loadAgents(deviceTypeMap);
        LOG.debug("all agents initialized");
    }

    /**
     * Load all device types as specified in the configuration.
     *
     * @return a map that contains the device identifier mapped to the {@code DeviceType} instances
     */
    private Map<String, DeviceType> loadDeviceTypes() {
        try (final InputStream deviceSchemaStream = ClassLoader.getSystemResourceAsStream("schema/device.xsd")) {
            final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final StreamSource deviceSchemaSource = new StreamSource(deviceSchemaStream);

            final Schema deviceSchema = schemaFactory.newSchema(deviceSchemaSource);

            final Map<String, DeviceType> deviceTypeMap = new HashMap<>(configuration.getDevices().size());
            for (final Device device : configuration.getDevices()) {
                try {
                    final JAXBContext context = JAXBContext.newInstance(DeviceType.class);
                    final Unmarshaller unmarshaller = context.createUnmarshaller();
                    unmarshaller.setSchema(deviceSchema);

                    final DeviceType deviceType = (DeviceType) unmarshaller.unmarshal(device.getPath());
                    deviceTypeMap.put(device.getId(), deviceType);
                    LOG.trace("device type {} loaded", device.getId());
                } catch (final JAXBException e) {
                    LOG.error("could not create schema JAXB context", e);
                }
            }
            return deviceTypeMap;
        } catch (final SAXException e) {
            LOG.error("could not create schema", e);
        } catch (final IOException e) {
            LOG.error("failed to close device schema stream", e);
        }
        return new HashMap<>(0);
    }

    /**
     * Load and start-up all agents specified in the configuration.
     *
     * @param deviceTypeMap map that contains the device identifier mapped to the {@code DeviceType} instances
     */
    private void loadAgents(final Map<String, DeviceType> deviceTypeMap) {
        for (final Agent agent : configuration.getAgents()) {
            final File walk = agent.getWalk();
            if (walk.exists() && walk.isFile()) {
                final Device device = (Device) agent.getDevice();
                if (deviceTypeMap.containsKey(device.getId())) {
                    final SnmpmanAgent snmpmanAgent = new SnmpmanAgent(deviceTypeMap.get(device.getId()), agent);
                    try {
                        snmpmanAgent.execute();
                        LOG.debug("agent \"{}\" started on address {}:{}", agent.getId(), agent.getAddress(), agent.getPort());
                    } catch (IOException e) {
                        LOG.error("could not init agent with id \"" + agent.getId() + "\"", e);
                    }
                } else {
                    LOG.warn("could not create agent for walk \"{}\" and device type \"{}\"", walk.getAbsolutePath(), device.getId());
                }
            } else {
                LOG.warn("could not find walk file \"{}\" for agent \"{}\"", walk.getAbsolutePath(), agent.getId());
            }
        }
    }

    /**
     * The command-line options for this application.
     */
    private static final class CommandLineOptions {

        /**
         * The {@code Snmpman} configuration.
         */
        private Configuration configuration;

        /**
         * Sets the {@code Snmpman} configuration by the specified path to the configuration file.
         *
         * @param configurationFile the configuration file
         * @throws SAXException  general SAX error or warning occurred
         * @throws JAXBException unmarshalling probably failed
         */
        @SuppressWarnings("UnusedDeclaration")
        @Option(name = "-c", aliases = "--configuration", usage = "the path to the configuration XML", required = true)
        private void setConfiguration(final File configurationFile) throws SAXException, JAXBException {
            Preconditions.checkArgument(configurationFile.exists() && configurationFile.isFile(), "configuration does not exist or is not a file");

            try (final InputStream configurationSchemaStream = ClassLoader.getSystemResourceAsStream("schema/configuration.xsd")) {
                final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                final StreamSource configurationSchemaSource = new StreamSource(configurationSchemaStream);

                final Schema configurationSchema = schemaFactory.newSchema(configurationSchemaSource);

                final JAXBContext context = JAXBContext.newInstance(Configuration.class);
                final Unmarshaller unmarshaller = context.createUnmarshaller();
                unmarshaller.setSchema(configurationSchema);
                this.configuration = (Configuration) unmarshaller.unmarshal(configurationFile);
                LOG.debug("configuration unmarshalling succeeded");
                return;
            } catch (final IOException e) {
                LOG.error("failed to close configuration schema stream", e);
            }

            throw new IllegalStateException("could not parse configuration from path: " + configurationFile.getAbsolutePath());
        }

    }


}
