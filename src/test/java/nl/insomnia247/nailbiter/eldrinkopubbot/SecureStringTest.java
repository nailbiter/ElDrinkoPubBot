package nl.insomnia247.nailbiter.eldrinkopubbot;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;

/**
 * @author Alex Leontiev
 */
public class SecureStringTest extends TestCase {
    public SecureStringTest( String testName ) {
        super( testName );
    }
    public static Test suite() {
        return new TestSuite( SecureStringTest.class );
    }
    public void testElDrinkoStateMachineConfig() {
        String what = "293eda2c6062dbb8";
        String what2 = "cac07132cd0759cc";

        SecureString.setHiddenInfo(what);
        assertEquals(SecureString.format("%02d:%02d %s",3,20,what),("03:20 ***"));
        assertEquals(SecureString.format("%02d:%02d %s",3,20,what2),("03:20 cac07132cd0759cc"));
    }
}
