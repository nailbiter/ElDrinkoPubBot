package nl.insomnia247.nailbiter.eldrinkopubbot;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;

/**
 * @author Alex Leontiev
 */
public class MiscUtilsTest extends TestCase {
    public MiscUtilsTest( String testName )
    {
        super( testName );
    }
    public static Test suite()
    {
        return new TestSuite( MiscUtilsTest.class );
    }
    public void testIsFloatInteger()
    {
        assertTrue( MiscUtils.IsFloatInteger(3.0f) );
        assertTrue( !MiscUtils.IsFloatInteger(3.2f) );
    }
    public void testParseFloat() {
        float f = 0.0f;
        try {
            f = MiscUtils.ParseFloat("3.5");
        } catch (Exception e) {
            fail(String.format("fail: %s",e.getMessage()));
        }
        assertEquals(f,3.5f);
        try {
            f = MiscUtils.ParseFloat("1,5");
        } catch (Exception e) {
            fail(String.format("fail: %s",e.getMessage()));
        }
        assertEquals(f,1.5f);
        try {
            f = MiscUtils.ParseFloat("abc");
            fail("missing exception");
        } catch(MiscUtils.ParseFloatException e) {
          assertEquals(e.getMessage(),"abc");
        }
    }
}
