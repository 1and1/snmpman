package com.oneandone.snmpman.configuration;

import com.oneandone.snmpman.configuration.modifier.Counter32Modifier;
import com.oneandone.snmpman.configuration.modifier.Modifier;
import com.oneandone.snmpman.configuration.type.ModifierProperties;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WalksTest {
    private Path tmpFile;

    @BeforeMethod
    public void setUp() throws IOException {
        tmpFile = Files.createTempFile("snmpman", "walk");
    }

    @AfterMethod
    public void tearDown() {
        if (tmpFile != null) {
            tmpFile.toFile().delete();
        }
    }

    @Test
    public void readWalkWithEmptyFile() throws IOException {
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        assertEquals(walk, Collections.emptyMap());
    }

    @Test
    public void readWalkWithStringLine() throws IOException {
        Files.write(tmpFile, Collections.singletonList(".1.3.6.1.2.1.2.2.1.2.10101 = STRING: \"GigabitEthernet0/1\""));
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        assertEquals(walk, Collections.singletonMap(
                new OID(".1.3.6.1.2.1.2.2.1.2.10101"),
                new OctetString("GigabitEthernet0/1")));
    }

    @Test
    public void readWalkWithFourStringLine() throws IOException {
        Files.write(tmpFile, Arrays.asList(
                ".1.0.8802.1.1.2.1.3.4.0 = STRING: \"Cisco IOS Software, C3560 Software (C3560-IPBASEK9-M), Version 12.2(50)SE3, RELEASE SOFTWARE (fc1)",
                "Technical Support: http://www.cisco.com/techsupport",
                "Copyright (c) 1986-2009 by Cisco Systems, Inc.",
                "Compiled Wed 22-Jul-09 06:41 by prod_rel_team\""));
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        String expected = "Cisco IOS Software, C3560 Software (C3560-IPBASEK9-M), Version 12.2(50)SE3, RELEASE SOFTWARE (fc1)\n" +
                "Technical Support: http://www.cisco.com/techsupport\n" +
                "Copyright (c) 1986-2009 by Cisco Systems, Inc.\n" +
                "Compiled Wed 22-Jul-09 06:41 by prod_rel_team";
        assertEquals(walk, Collections.singletonMap(
                new OID(".1.0.8802.1.1.2.1.3.4.0"),
                new OctetString(expected)));
    }

    @Test
    public void readWalkWithHexStringLine() throws IOException {
        Files.write(tmpFile, Collections.singletonList(".1.3.6.1.4.1.9.9.683.1.5.0 = Hex-STRING: 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f"));
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        byte[] expected = new byte[16];
        for (int i = 0; i < 16; i++) {
            expected[i] = (byte) i;
        }
        assertEquals(walk, Collections.singletonMap(
                new OID(".1.3.6.1.4.1.9.9.683.1.5.0"),
                new OctetString(expected)));
    }

    @Test
    public void readWalkWithIsoOid() throws IOException {
        Files.write(tmpFile, Collections.singletonList("iso.3.6.1.4.1.9.9.683.1.5.0 = Hex-STRING: 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f"));
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        byte[] expected = new byte[16];
        for (int i = 0; i < 16; i++) {
            expected[i] = (byte) i;
        }
        assertEquals(walk, Collections.singletonMap(
                new OID(".1.3.6.1.4.1.9.9.683.1.5.0"),
                new OctetString(expected)));
    }

    @Test
    public void readWalkWithTwoHexStringLine() throws IOException {
        Files.write(tmpFile, Arrays.asList(
                ".1.3.6.1.4.1.9.9.683.1.5.0 = Hex-STRING: 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f",
                "10 11 12 13 14 15 16 17 18 19 1a 1b 1c 1d 1e 1f"));
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        byte[] expected = new byte[32];
        for (int i = 0; i < 32; i++) {
            expected[i] = (byte) i;
        }
        assertEquals(walk, Collections.singletonMap(
                new OID(".1.3.6.1.4.1.9.9.683.1.5.0"),
                new OctetString(expected)));
    }

    @Test
    public void readWalkWithMultipleKeys() throws IOException {
        Files.write(tmpFile, Arrays.asList(
            ".1.3.6.1.2.1.2.2.1.2.10101 = STRING: \"GigabitEthernet0/1\"",
            ".1.3.6.1.2.1.31.1.1.1.1.10101 = STRING: \"Gi0/1\"",
            ".1.3.6.1.2.1.31.1.1.1.10.10101 = Counter64: 48648257581",
            ".1.3.6.1.2.1.31.1.1.1.11.10101 = Counter64: 32038868",
            ".1.3.6.1.2.1.31.1.1.1.12.10101 = Counter64: 141915228",
            ".1.3.6.1.2.1.31.1.1.1.13.10101 = Counter64: 44011328",
            ".1.3.6.1.2.1.31.1.1.1.15.10101 = Gauge32: 1000",
            ".1.3.6.1.2.1.2.2.1.5.10101 = Gauge32: 1000000000"
        ));
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        Map<OID, Variable> expected = new HashMap<>();
        expected.put(new OID(".1.3.6.1.2.1.2.2.1.2.10101"), new OctetString("GigabitEthernet0/1"));
        expected.put(new OID(".1.3.6.1.2.1.31.1.1.1.1.10101"), new OctetString("Gi0/1"));
        expected.put(new OID(".1.3.6.1.2.1.31.1.1.1.10.10101"), new Counter64(48648257581L));
        expected.put(new OID(".1.3.6.1.2.1.31.1.1.1.11.10101"), new Counter64(32038868L));
        expected.put(new OID(".1.3.6.1.2.1.31.1.1.1.12.10101"), new Counter64(141915228L));
        expected.put(new OID(".1.3.6.1.2.1.31.1.1.1.13.10101"), new Counter64(44011328));
        expected.put(new OID(".1.3.6.1.2.1.31.1.1.1.15.10101"), new Gauge32(1000));
        expected.put(new OID(".1.3.6.1.2.1.2.2.1.5.10101"), new Gauge32(1000000000));

        assertEquals(walk, expected);
    }

    @Test
    public void readWalkWithInvalidLine() throws IOException {
        Files.write(tmpFile, Collections.singletonList("this is just an example"));
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        assertEquals(walk, Collections.emptyMap());
    }
}
