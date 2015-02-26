package com.oneandone.network.snmpman;

import com.oneandone.network.snmpman.configuration.Device;
import org.testng.annotations.Test;

import java.io.File;

import static org.testng.Assert.*;

public class DeviceFactoryTest {

    @Test
    public void testGetDeviceForNullPath() throws Exception {
        final SnmpmanAgent.DeviceFactory deviceFactory = new SnmpmanAgent.DeviceFactory();
        assertEquals(deviceFactory.getDevice(null), SnmpmanAgent.DeviceFactory.DEFAULT_DEVICE);
    }
    
    @Test
    public void testGetDeviceForNonExistentPath() throws Exception {
        final SnmpmanAgent.DeviceFactory deviceFactory = new SnmpmanAgent.DeviceFactory();
        assertEquals(deviceFactory.getDevice(new File("src/foo/bar/what.txt")), SnmpmanAgent.DeviceFactory.DEFAULT_DEVICE);
    }

    @Test
    public void testGetCiscoDevice() throws Exception {
        final SnmpmanAgent.DeviceFactory deviceFactory = new SnmpmanAgent.DeviceFactory();
        final Device ciscoDevice = deviceFactory.getDevice(new File("src/test/resources/configuration/cisco.yaml"));
        
        assertNotEquals(ciscoDevice, SnmpmanAgent.DeviceFactory.DEFAULT_DEVICE);
        
        assertEquals(ciscoDevice.getName(), "Cisco");
        assertEquals(ciscoDevice.toString(), "Device(name=Cisco)");
        assertFalse(ciscoDevice.getModifiers().isEmpty());
    }
}