package com.oneandone.snmpman;

import org.snmp4j.smi.OctetString;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class SnmpmanAgentTest {

    private Snmpman snmpman;

    @BeforeMethod
    public void startSnmpman() {
        snmpman = Snmpman.start(new File("src/test/resources/configuration/configuration.yaml"));
    }

    @Test
    public void testSnmpAgentSetupWithCommunityContext() {
        final List<OctetString> contexts = new ArrayList<>();
        final List<SnmpmanAgent> snmpmanAgents = snmpman.getAgents();
        for (final SnmpmanAgent agent : snmpmanAgents) {
            agent.registerManagedObjects();

            contexts.addAll(Arrays.asList(agent.getServer().getContexts()));
        }

        assertTrue(contexts.contains(new OctetString("9")));
        assertTrue(contexts.contains(new OctetString("42")));
    }

    @AfterMethod
    public void stopSnmpman() {
        if (snmpman != null) {
            snmpman.stop();
        }
    }
}
