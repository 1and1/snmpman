package com.oneandone.network.snmpman;

import org.mockito.Mockito;
import org.slf4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Field;

public class SnmpmanTest {

    private Logger logger = Mockito.mock(Logger.class);

    @BeforeTest
    public void mockLog() throws Exception {
        Field log = Snmpman.class.getDeclaredField("LOG");
        log.setAccessible(true);
        log.set(null, logger);
    }

    @Test
    public void testMainWithIllegalParameters() throws Exception {
        Snmpman.main();
        Snmpman.main("-c");
        Snmpman.main("-c", "wtf?");
        Mockito.verify(logger, Mockito.times(3)).error(Mockito.anyString(), Mockito.any(Exception.class));
    }
}
