package nl.insomnia247.nailbiter.eldrinkopubbot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.TemplateEngine;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Alex Leontiev
 */
public class TemplateEngineTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TemplateEngineTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( TemplateEngineTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testRender()
    {
        TemplateEngine te = new TemplateEngine();
        Map<String,Object> env = new HashMap<>();
        env.put("x",3);
        env.put("y",3.5678);
        env.put("z",3.5678f);
        env.put("w",43);

        assertEquals( te.render("{{x}}",env), "3");
        assertEquals( te.render("{{x|myprintf}}",env), "3,00");
        assertEquals( te.render("{{y|myprintf}}",env), "3,57");
        assertEquals( te.render("{{z|myprintf}}",env), "3,57");
        assertEquals( te.render("{{x|myprintf_int}}",env), "03");
        assertEquals( te.render("{{w|myprintf_int}}",env), "43");
    }
}
