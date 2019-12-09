package com.oneandone.snmpman;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertTrue;

public abstract class AbstractSnmpmanTest {

    protected Snmpman snmpman;
    protected static String COMMUNITY = "public";
    protected static final int PORT = 10009;

    @BeforeMethod
    public void startSnmpman() {
        snmpman = Snmpman.start(new File("src/test/resources/configuration/configuration.yaml"));
    }

    @AfterMethod
    public void stopSnmpman() {
        snmpman.stop();
    }


    public static boolean containsColumn(final List<TableEvent> responses, final String oid, final String result) {
        for (final TableEvent e : responses) {
            String columnsToString = Arrays.toString(e.getColumns());
            if (columnsToString.contains(oid) && columnsToString.contains("= " + result)) {
                return true;
            }
        }
        return false;
    }

    public static List<TableEvent> getResponse(final OID query, int port, final String community) throws Exception {
        final Address targetAddress = GenericAddress.parse(String.format("127.0.0.1/%d", port));
        final Snmp snmp = new Snmp(new DefaultUdpTransportMapping());
        snmp.listen();

        final CommunityTarget target = getCommunityTarget(community, targetAddress);

        // creating PDU
        final PDUFactory pduFactory = new DefaultPDUFactory(PDU.GETBULK);
        final TableUtils utils = new TableUtils(snmp, pduFactory);

        return utils.getTable(target, new OID[]{query}, null, null);
    }

    static CommunityTarget getCommunityTarget(String community, Address targetAddress) {
        final CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(targetAddress);
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

    public static List<TableEvent> getResponse(final OID query, int port) throws Exception {
        return getResponse(query, port, COMMUNITY);
    }

    void assertThatOidHasValue(OID oid, String expectedValue) throws Exception {
        List<TableEvent> responses1 = getResponse(oid, PORT);
        assertTrue(containsColumn(responses1, oid.toString(), expectedValue),
                "Table under OID=" + oid + " doesn't contain value=" + expectedValue);
    }
}
