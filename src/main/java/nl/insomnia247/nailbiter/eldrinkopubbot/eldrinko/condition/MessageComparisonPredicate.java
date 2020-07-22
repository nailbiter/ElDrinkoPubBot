package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import org.json.JSONObject;

/**
 * @author Alex Leontiev
 */
public class MessageComparisonPredicate extends ElDrinkoCondition {
    String _what;
    public MessageComparisonPredicate(Object o) {
        _what = (String) o;
    }
    @Override
    public boolean test(ElDrinkoInputMessage im) {
        return im.left.getMsg().equals(_what);
    }
    @Override
    public String toJsonString() {
        return new JSONObject(super.toJsonString()).put("value", _what).toString();
    }
}
