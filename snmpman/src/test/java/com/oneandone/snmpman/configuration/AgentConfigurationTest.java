package com.oneandone.snmpman.configuration;

import org.testng.annotations.Test;

import java.io.File;

public class AgentConfigurationTest {

    @Test
    public void testInitialization() {
        final AgentConfiguration configuration = new AgentConfiguration(
                "Test",
                new File("src/test/resources/configuration/cisco.yaml"),
                new File("src/test/resources/configuration/example.txt"),
                "127.0.0.1", 8080,
                "secret");

        assertEquals(configuration.getName(), "Test");
        assertEquals(configuration.getWalk(), new File("src/test/resources/configuration/example.txt"));
        assertEquals(configuration.getCommunity(), "secret");
        assertEquals(configuration.getDevice().getName(), "Cisco");
        assertEquals(configuration.getAddress().toByteArray(), new byte[]{ 127, 0, 0, 1 });
    }

    @Test
    public void testInitializationWithoutName() {
        final AgentConfiguration configuration = new AgentConfiguration(
                null,
                new File("src/test/resources/configuration/cisco.yaml"),
                new File("src/test/resources/configuration/example.txt"),
                "127.0.0.1", 8080,
                "secret");

        assertEquals(configuration.getName(), "127.0.0.1:8080");
        assertEquals(configuration.getWalk(), new File("src/test/resources/configuration/example.txt"));
        assertEquals(configuration.getCommunity(), "secret");
        assertEquals(configuration.getDevice().getName(), "Cisco");
        assertEquals(configuration.getAddress().toByteArray(), new byte[]{ 127, 0, 0, 1 });
    }

    @Test
    public void testInitializationWithoutDevice() {
        final AgentConfiguration configuration = new AgentConfiguration(
                "Test",
                null,
                new File("src/test/resources/configuration/example.txt"),
                "127.0.0.1", 8080,
                "secret");

        assertEquals(configuration.getName(), "Test");
        assertEquals(configuration.getWalk(), new File("src/test/resources/configuration/example.txt"));
        assertEquals(configuration.getCommunity(), "secret");
        assertEquals(configuration.getDevice(), AgentConfiguration.DeviceFactory.DEFAULT_DEVICE);
        assertEquals(configuration.getAddress().toByteArray(), new byte[]{ 127, 0, 0, 1 });
    }
}