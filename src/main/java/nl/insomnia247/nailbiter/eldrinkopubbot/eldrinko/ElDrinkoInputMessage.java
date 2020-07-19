package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.JSONObject;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import org.json.JSONObject;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.Tsv;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;

/**
 * @author Alex Leontiev
 */
public class ElDrinkoInputMessage {
    public TelegramInputMessage left = null;
    public final static String BEERLIST = "https://docs.google.com/spreadsheets/d/e/2PACX-1vRGSUiAeapo7eHNfA1v9ov_Cc2oCjWNsmcpadN6crtxJ236uDOKt_C_cR1hsXCyqZucp_lQoeRHlu0k/pub?gid=0&single=true&output=tsv";
    public JSONObject right = null;
    public UserData userData;
    public Tsv beerlist = new Tsv(MiscUtils.SafeUrl(BEERLIST));
    public ElDrinkoInputMessage(TelegramInputMessage i, JSONObject o, UserData u, Tsv b) {
        left = i;
        right = o;
        userData = u;
        beerlist = b;
    }
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
            .put("left",new JSONObject(left.toJsonString()))
            .put("right",right)
            .put("userData",new JSONObject(userData.toJsonString()))
            .put("beerlist",new JSONObject(beerlist.toJsonString()))
            .toString();
    }
}
