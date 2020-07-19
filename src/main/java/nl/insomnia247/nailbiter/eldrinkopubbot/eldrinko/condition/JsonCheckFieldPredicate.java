package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.jayway.jsonpath.JsonPath;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import org.json.JSONObject;
import com.jayway.jsonpath.Configuration;

/**
 * @author Alex Leontiev
 */
public class JsonCheckFieldPredicate extends ElDrinkoCondition {
    private static Logger _Log = LogManager.getLogger();
    private JSONObject _path = null;
    public JsonCheckFieldPredicate(Object o) {
        _path = (JSONObject)o;
    }
    @Override
    public boolean test(ElDrinkoInputMessage im) {
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(im.right.toString());
        _Log.info(SecureString.format("im: %s",im.toString()));
        _Log.info(SecureString.format("_path: %s",_path));
        _Log.info(SecureString.format("document: %s",document));
        String res = null;
        try {
            res = JsonPath.read(document,_path.getString("path"));
        } catch (Exception e ) {
            _Log.error(e);
        }
        _Log.info(SecureString.format("res: %s",res));
        return res!=null;
    }
    @Override
    public String toJsonString() {
        return new JSONObject(super.toJsonString()).put("value", _path).toString();
    }
}
