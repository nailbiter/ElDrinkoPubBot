package nl.insomnia247.nailbiter.eldrinkopubbot;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.lang3.tuple.ImmutablePair;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoStateMachine;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.action.ElDrinkoActionInflator;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition.ElDrinkoConditionInflator;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.PersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.test_util.MockConsumer;
import nl.insomnia247.nailbiter.eldrinkopubbot.test_util.MockUserData;
import nl.insomnia247.nailbiter.eldrinkopubbot.test_util.MockBeerlist;
import nl.insomnia247.nailbiter.eldrinkopubbot.test_util.MockPersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.JSONTools;

/**
 * @author Alex Leontiev
 */
public class ElDrinkoPubBotStateMachineTest extends TestCase {
    Logger _Log = LogManager.getLogger();
    private MockConsumer _consumer;
    private ElDrinkoStateMachine _edsm;
    private PersistentStorage _persistentStorage;
    private ElDrinkoActionInflator _actionInflator;
    private ElDrinkoConditionInflator _conditionInflator;
    public ElDrinkoPubBotStateMachineTest( String testName ) {
        super( testName );

        _consumer = new MockConsumer();
        _edsm = new ElDrinkoStateMachine(_consumer);
        _persistentStorage = new MockPersistentStorage();
        _actionInflator = new ElDrinkoActionInflator(_consumer, _persistentStorage, null);
        _conditionInflator = new ElDrinkoConditionInflator();
        try {
            _edsm.inflateTransitionsFromJSON(_conditionInflator,_actionInflator, 
                    new JSONObject(MiscUtils.GetResource("transitions",".json")).getJSONArray("correspondence").toString());
        } catch (Exception e) {
            _Log.error(e);
            System.exit(1);
        }
    }
    public static Test suite() {
        return new TestSuite( ElDrinkoPubBotStateMachineTest.class );
    }
    public void testElDrinkoStateMachineConfig() {
        JSONObject eldrinko_state_machine = new JSONObject(MiscUtils.GetResource("eldrinko_state_machine",".json"));
        _Log.info(String.format("%s: %s","01c303e27fafdc26",_edsm.toJsonString()));
        assertEquals(JSONTools.JSONObjectToMap(eldrinko_state_machine), JSONTools.JSONObjectToMap(new JSONObject(_edsm.toJsonString())));
    }
    private static TelegramInputMessage _ParseTelegramInputMessage(JSONObject o) throws Exception {
        if(o.getString("tag").equals("TelegramTextInputMessage")) {
            return new TelegramTextInputMessage(o.getString("value"));
        } else if (o.getString("tag").equals("TelegramKeyboardAnswer")) {
            return new TelegramKeyboardAnswer(o.getString("value"));
        } else {
            throw new Exception(String.format("cannot parse tag \"%s\"",o.getString("tag")));
        }
    }
    private static ElDrinkoInputMessage _ParseElDrinkoInputMessage(JSONObject o) throws Exception {
        return new ElDrinkoInputMessage(_ParseTelegramInputMessage(o.getJSONObject("left")),
                o.getJSONObject("right"),
                new MockUserData(o.getJSONObject("userData"))
                ,new MockBeerlist(new JSONObject(MiscUtils.GetResource("beerlist",".json")))
                );
    }
    public void testElDrinkoStateMachineTransitions() {
        JSONArray transition_tests = new JSONObject(MiscUtils.GetResource("transition_tests",".json")).getJSONArray("transitions");
        for(int i = 0; i < transition_tests.length(); i++) {
            String fn = transition_tests.getString(i);
            _Log.info(String.format("test #%d (%s)",i,fn));
            JSONObject transition = new JSONObject(MiscUtils.GetResource(String.format("transition_tests/%s",fn),".json"));
            ElDrinkoInputMessage im = null;
            try {
                _edsm.setState(transition.getString("ss"));
                im = _ParseElDrinkoInputMessage(transition.getJSONObject("im"));
            } catch (Exception e) {
                _Log.error(e);
                System.exit(1);
            }
            ImmutablePair<OutputMessage,JSONObject> om = _edsm.apply(im);
            String om_actual 
                = new JSONObject()
                  .put("left",new JSONObject(om.left.toJsonString())).put("right",om.right).toString();
            _Log.info(String.format("es(%d,%s,5113060c):\n%s\n==?\n%s",i,fn,_edsm.getState(),transition.getString("es")));
            assertEquals(_edsm.getState(),transition.getString("es"));
            _Log.info(String.format("om(%d,%s,048a2215):\n%s\n==?\n%s",i,fn,transition.getJSONObject("om").toString(),om_actual));
            assertEquals(JSONTools.JSONObjectToMap(transition.getJSONObject("om")), JSONTools.JSONObjectToMap(new JSONObject(om_actual)));
        }
    }
}
//{
//  "transitions": [
//    "5eb4f1",
//    "18e3e1",
//    "f76989",
//    "69114e",
//    "bc7898",
//    "d65253",
//    "93c1d4",
//    "dbdec3",
//    "532308",
//    "cf2959",
//    "3d23ef",
//    "462470",
//    "21f3a0",
//    "10276d",
//    "1ff9a7",
//    "36983c",
//    "783c59",
//    "7212df",
//    "ba7076",
//    "b8dc6a",
//    "ccd65e",
//    "437c64",
//    "3c2548",
//    "ef1f39",
//    "edd9d1",
//    "25e80f",
//    "c4d378",
//    "96c6c1",
//    "85bf6b",
//    "895e39",
//    "a7981f",
//    "b65c81",
//    "395a0c",
//    "1203db",
//    "f5d2ce",
//    "2ed26d"
//  ]
//}
