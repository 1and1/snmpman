package com.oneandone.network.snmpman;

import org.kohsuke.args4j.CmdLineParser;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CommandLineOptionsTest {

    @Test
    public void testGetAgents() throws Exception {
        final CommandLineOptions commandLineOptions = new CommandLineOptions();
        final CmdLineParser cmdLineParser = new CmdLineParser(commandLineOptions);
        cmdLineParser.parseArgument("-c", "src/test/resources/configuration/configuration.yaml");
        
        assertEquals(commandLineOptions.getConfigurationFile().getName(), "configuration.yaml");
        assertTrue(commandLineOptions.getConfigurationFile().exists());
        assertTrue(commandLineOptions.getConfigurationFile().isFile());
    }
}