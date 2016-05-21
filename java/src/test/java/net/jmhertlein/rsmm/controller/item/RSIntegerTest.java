package net.jmhertlein.rsmm.controller.item;

import net.jmhertlein.rsmm.model.RSIntegers;
import org.junit.Test;
import static org.junit.Assert.*;

public class RSIntegerTest {
    @Test
    public void testParsing() {
        assertTrue(RSIntegers.parseInt("2b") == 2000000000);
        assertTrue(RSIntegers.parseInt("100m") == 100000000);
        assertTrue(RSIntegers.parseInt("10m") == 10000000);
        assertTrue(RSIntegers.parseInt("1m") == 1000000);
        assertTrue(RSIntegers.parseInt("100k") == 100000);
        assertTrue(RSIntegers.parseInt("1k") == 1000);
        assertTrue(RSIntegers.parseInt("120") == 120);
    }

    @Test
    public void testPrinting() {
        assertEquals("100k", RSIntegers.toString(100000));
        assertEquals("1000k", RSIntegers.toString(1000000));
        assertEquals("1100k", RSIntegers.toString(1100000));
        assertEquals("10M", RSIntegers.toString(10000000));
        assertEquals("1", RSIntegers.toString(1));
        assertEquals("10", RSIntegers.toString(10));
        assertEquals("2147M", RSIntegers.toString(Integer.MAX_VALUE));
    }
}
