package net.jmhertlein.rsmm.controller.item;

import net.jmhertlein.rsmm.model.RSInteger;
import org.junit.Test;
import static org.junit.Assert.*;

public class RSIntegerTest {
    @Test
    public void testParsing() {
        assertTrue(RSInteger.parseInt("2b").intValue() == 2000000000);
        assertTrue(RSInteger.parseInt("100m").intValue() == 100000000);
        assertTrue(RSInteger.parseInt("10m").intValue() == 10000000);
        assertTrue(RSInteger.parseInt("1m").intValue() == 1000000);
        assertTrue(RSInteger.parseInt("100k").intValue() == 100000);
        assertTrue(RSInteger.parseInt("1k").intValue() == 1000);
        assertTrue(RSInteger.parseInt("120").intValue() == 120);
    }

    @Test
    public void testPrinting() {
        assertEquals("100k", new RSInteger(100000).toString());
        assertEquals("1000k", new RSInteger(1000000).toString());
        assertEquals("1100k", new RSInteger(1100000).toString());
        assertEquals("10M", new RSInteger(10000000).toString());
        assertEquals("1", new RSInteger(1).toString());
        assertEquals("10", new RSInteger(10).toString());
        assertEquals("2147M", new RSInteger(Integer.MAX_VALUE).toString());
    }
}
