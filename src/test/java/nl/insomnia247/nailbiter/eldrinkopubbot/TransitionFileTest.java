package nl.insomnia247.nailbiter.eldrinkopubbot;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import org.json.JSONObject;

/**
 * @author Alex Leontiev
 */
public class TransitionFileTest extends TestCase {
    public TransitionFileTest( String testName ){
        super( testName );
    }
    public static Test suite(){
        return new TestSuite( TransitionFileTest.class );
    }
    public void testIsEveryCorrespondenceContainsValidTransition(){
        JSONObject transitions = new JSONObject(MiscUtils.GetResource("transitions",".json"));
        for(int i = 0, len = transitions.getJSONArray("correspondence").length(); i < len; i++) {
            assertNotNull(transitions.getJSONObject("transitions").opt(transitions.getJSONArray("correspondence").getJSONArray(i).getJSONObject(3).getString("correspondence")));
        }
    }
}
