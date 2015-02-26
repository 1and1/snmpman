package com.oneandone.network.snmpman.configuration;

import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class DeviceFactoryTest {

    @Test
    public void testGetDeviceForNullPath() throws Exception {
        final AgentConfiguration.DeviceFactory deviceFactory = new AgentConfiguration.DeviceFactory();
        assertEquals(deviceFactory.getDevice(null), AgentConfiguration.DeviceFactory.DEFAULT_DEVICE);
    }

    @Test
    public void testGetDeviceForNonExistentPath() throws Exception {
        final AgentConfiguration.DeviceFactory deviceFactory = new AgentConfiguration.DeviceFactory();
        assertEquals(deviceFactory.getDevice(new File("src/foo/bar/what.txt")), AgentConfiguration.DeviceFactory.DEFAULT_DEVICE);
    }

    @Test
    public void testGetCiscoDevice() throws Exception {
        final AgentConfiguration.DeviceFactory deviceFactory = new AgentConfiguration.DeviceFactory();
        final Device ciscoDevice = deviceFactory.getDevice(new File("src/test/resources/configuration/cisco.yaml"));

        assertNotEquals(ciscoDevice, AgentConfiguration.DeviceFactory.DEFAULT_DEVICE);

        assertEquals(ciscoDevice.getName(), "Cisco");
        assertEquals(ciscoDevice.toString(), "Device(name=Cisco)");
        assertFalse(ciscoDevice.getModifiers().isEmpty());
    }

}