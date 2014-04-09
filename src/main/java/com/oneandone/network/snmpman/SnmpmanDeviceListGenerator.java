package com.oneandone.network.snmpman;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.*;

/**
 * Generates a list of simulated devices by the found real walk files in a specified directory.
 */
public class SnmpmanDeviceListGenerator {

    /**
     * Executes the generator.
     * <p/>
     * The command line options or arguments can be found in {@link CommandLineOptions}.
     *
     * @param args the arguments
     * @throws IOException      if files could not be read or written
     * @throws CmdLineException if arguments were illegal a help message will be printed
     */
    public static void main(final String... args) throws IOException, CmdLineException {
        final CommandLineOptions commandLineOptions = new CommandLineOptions();
        final CmdLineParser cmdLineParser = new CmdLineParser(commandLineOptions);
        try {
            cmdLineParser.parseArgument(args);
        } catch (final CmdLineException e) {
            cmdLineParser.printUsage(System.out);
            throw e;
        }

        final BufferedWriter writer;
        if (commandLineOptions.output != null) {
            final FileWriter fileWriter = new FileWriter(commandLineOptions.output);
            writer = new BufferedWriter(fileWriter);
        } else {
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(System.out);
            writer = new BufferedWriter(outputStreamWriter);
        }

        printConfiguration(writer, commandLineOptions.walkFileDirectory, commandLineOptions.simulatedDevices, commandLineOptions.startPort, commandLineOptions.agentIp);
        writer.close();
    }

    /**
     * Prints the configuration by the specified writer.
     *
     * @param writer            the writer as the output target
     * @param walkFileDirectory the directory in which the real walk files can be found
     * @param simulatedDevices  the amount of desired devices to simulate
     * @param startPort         the start port for the simulated agents
     * @param agentIp           the ips for the simulated agents
     * @throws IOException thrown if results could not be written
     */
    private static final void printConfiguration(final BufferedWriter writer,
                                                 final File walkFileDirectory,
                                                 final int simulatedDevices,
                                                 final int startPort,
                                                 final String[] agentIp) throws IOException {
        final File[] device = walkFileDirectory.listFiles();

        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.newLine();

        writer.write("<devices>");
        writer.newLine();

        for (int i = 0; i < simulatedDevices; i++) {
            final String id = device[i % device.length].getName();
            final String newId = id.replaceFirst("\\.", ".test-" + i + ".");

            final String ip = agentIp[i % agentIp.length];
            final int port = startPort + i;

            writer.write("\t<device id=\"" + id + "\" newId=\"" + newId + "\" ip=\"" + ip + "\" port=\"" + port + "\"/>");
            writer.newLine();
        }
        writer.write("</devices>");
    }


    /**
     * The command line options for the execution of this service.
     */
    private static final class CommandLineOptions {

        /**
         * The amount of desired devices to simulate.
         */
        @Option(name = "-s", aliases = "--simulatedDevices", usage = "number of devices to simulate", required = true)
        private int simulatedDevices = 0;

        /**
         * The start port for the simulated agents.
         */
        @Option(name = "-p", aliases = "--port", usage = "the start port for simulated agents", required = false)
        private int startPort = 10000;

        /**
         * The ips for the simulated agents.
         */
        @Option(name = "-i", aliases = "--agentIp", usage = "the IPs for the snmpman instances", required = true, handler = StringArrayOptionHandler.class)
        private String[] agentIp = null;

        /**
         * The directory in which the real walk files can be found.
         */
        @Option(name = "-d", aliases = "--walkFileDirectory", usage = "the directory for the walk files", required = true)
        private File walkFileDirectory = null;

        /**
         * The output file for the configuration. If set to {@code null} the result will be printed to the default output stream.
         */
        @Option(name = "-o", aliases = "--output", usage = "the output target for the generated configuration", required = false)
        private File output = null;
    }

}
