package nl.insomnia247.nailbiter.eldrinkopubbot.test_util;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import org.json.JSONObject;

/**
 * @author Alex Leontiev
 */
public class MockUserData extends UserData {
    public MockUserData(JSONObject o) {
        _chatId = o.getInt("chatId");
        _username = o.getString("username");
    }
}
