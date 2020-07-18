package nl.insomnia247.nailbiter.eldrinkopubbot;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoStateMachine;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoActionInflator;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoConditionInflator;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.PersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.test_util.MockConsumer;
import nl.insomnia247.nailbiter.eldrinkopubbot.test_util.MockPersistentStorage;
import org.json.JSONObject;

/**
 * @author Alex Leontiev
 */
public class ElDrinkoPubBotStateMachineTest extends TestCase {
    public ElDrinkoPubBotStateMachineTest( String testName ) {
        super( testName );
    }
    public static Test suite() {
        return new TestSuite( ElDrinkoPubBotStateMachineTest.class );
    }
    public void testElDrinkoStateMachineConfig() {
        MockConsumer consumer = new MockConsumer();
        ElDrinkoStateMachine edsm = new ElDrinkoStateMachine(consumer);
        PersistentStorage persistentStorage = new MockPersistentStorage();
        ElDrinkoActionInflator actionInflator = new ElDrinkoActionInflator(consumer, persistentStorage);
        ElDrinkoConditionInflator conditionInflator = new ElDrinkoConditionInflator();
        try {
            edsm.inflateTransitionsFromJSON(conditionInflator,actionInflator, 
                    new JSONObject(MiscUtils.GetResource("transitions",".json")).getJSONArray("correspondence").toString());
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        JSONObject eldrinko_state_machine = new JSONObject(MiscUtils.GetResource("eldrinko_state_machine",".json"));
        System.out.println(edsm.toJsonString());
        assertEquals(eldrinko_state_machine.toString(), new JSONObject(edsm.toJsonString()).toString());
    }
}
