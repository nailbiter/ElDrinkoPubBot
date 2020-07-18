package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.JSONObject;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import org.json.JSONObject;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;

/**
 * @author Alex Leontiev
 */
public class ElDrinkoInputMessage {
    public TelegramInputMessage left = null;
    public JSONObject right = null;
    public UserData userData;
    public ElDrinkoInputMessage(TelegramInputMessage i, JSONObject o, UserData u) {
        left = i;
        right = o;
        userData = u;
    }
    @Override
    public String toString() {
        return new ImmutablePair<TelegramInputMessage,JSONObject>(left,right).toString();
    }
    public String toJsonString() {
        return new JSONObject()
            .put("left",new JSONObject(TelegramInputMessage.toJsonString()))
            .put("right",right)
            .put("userData",new JSONObject(userData.toJsonString()))
            .toString();
    }
}
