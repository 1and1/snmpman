package com.oneandone.snmpman.configuration;

import com.oneandone.snmpman.configuration.modifier.Counter32Modifier;
import com.oneandone.snmpman.configuration.modifier.Modifier;
import com.oneandone.snmpman.configuration.type.ModifierProperties;
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
    public void readWalkWithInvalidLine() throws IOException {
        Files.write(tmpFile, Collections.singletonList("this is just an example"));
        Map<OID, Variable> walk = Walks.readWalk(tmpFile.toFile());
        assertEquals(walk, Collections.emptyMap());
    }
}
