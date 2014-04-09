package com.oneandone.network.snmpman.jaxb;

import junit.framework.Assert;
import org.testng.annotations.Test;

import java.io.File;

public class JAXBObjectConverterTest {

    private String SLASH = File.separator;

    @Test
    public void testParseFile() throws Exception {
        String path = "src" + SLASH + "test" + SLASH + "java" + SLASH + "com" + SLASH + "oneandone" + SLASH + "network" + SLASH + "snmpman" + SLASH + "jaxb" + SLASH + "JAXBObjectConverterTest.java";
        File file = JAXBObjectConverter.parseFile(path);
        Assert.assertTrue("wrong file created", file.getName().endsWith("JAXBObjectConverterTest.java"));
        Assert.assertTrue("absolute path is too short", file.getAbsolutePath().length() > path.length());
    }

    @Test(dependsOnMethods = "testParseFile")
    public void testPrintFile() throws Exception {
        String path = "src" + SLASH + "test" + SLASH + "java" + SLASH + "com" + SLASH + "oneandone" + SLASH + "network" + SLASH + "snmpman" + SLASH + "jaxb" + SLASH + "JAXBObjectConverterTest.java";
        File file = JAXBObjectConverter.parseFile(path);
        String result = JAXBObjectConverter.printFile(file);
        Assert.assertTrue(result.endsWith(path));
    }

    @Test
    public void testParseWildcardOID() throws Exception {
        String oid = "1.3.3.7.*.1";
        WildcardOID wildcardOID = JAXBObjectConverter.parseWildcardOID(oid);
        Assert.assertEquals("created OID string is wrong", wildcardOID.toString(), oid);
    }

    @Test(dependsOnMethods = "testParseWildcardOID")
    public void testPrintWildcardOID() throws Exception {
        String oid = "1.3.3.7.*.1";
        WildcardOID wildcardOID = JAXBObjectConverter.parseWildcardOID(oid);
        String result = JAXBObjectConverter.printWildcardOID(wildcardOID);
        Assert.assertEquals("created OID string is wrong", result, oid);
    }
}
