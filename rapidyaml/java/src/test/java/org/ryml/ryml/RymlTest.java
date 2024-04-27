package org.ryml.ryml;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Unit test for simple App.
 */
public class RymlTest extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public RymlTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( RymlTest.class );
    }

    public void testLoadJSON()
    {
        JSONObject result = Ryml.loadJSON("a: 1");
        int value = result.getJSONObject("data").getInt("a");

        assertEquals(1, value);
    }

    public void testLoad()
    {
        JSONObject data = (JSONObject)Ryml.load("a: 1");

        assertEquals(1, data.getInt("a"));
    }

    public void testLoadArray()
    {
        JSONArray data = Ryml.loadArray("[1, 2, 3]");

        assertEquals(1, data.getInt(0));
    }

    public void testLoadInt()
    {
        int data = Ryml.loadInt("1");

        assertEquals(1, data);
    }

    public void testLoadBoolean()
    {
        boolean data = Ryml.loadBoolean("true");

        assertTrue(data);
    }

    public void testLoadFloat()
    {
        float data = Ryml.loadFloat("1.4");

        assertEquals(1.4F, data);
    }

    public void testLoadDouble()
    {
        double data = Ryml.loadDouble("1.4");

        assertEquals(1.4D, data);
    }

    public void testLoadLong()
    {
        long data = Ryml.loadLong("1");

        assertEquals(1L, data);
    }

    public void testLoadBigInteger()
    {
        BigInteger data = Ryml.loadBigInteger("1");

        assertEquals(new BigInteger("1"), data);
    }

    public void testLoadBigDecimal()
    {
        BigDecimal data = Ryml.loadBigDecimal("1.4");

        assertEquals(new BigDecimal("1.4"), data);
    }
}
