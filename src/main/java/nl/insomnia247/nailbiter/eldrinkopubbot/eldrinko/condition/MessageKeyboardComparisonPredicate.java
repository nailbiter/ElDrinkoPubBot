package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import org.json.JSONObject;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer;

/**
 * @author Alex Leontiev
 */
public class MessageKeyboardComparisonPredicate extends ElDrinkoCondition {
    String _what = null;
    public MessageKeyboardComparisonPredicate(Object o) {
        if (o!=null) {
            _what = (String) o;
        }
    }
    @Override
    public boolean test(ElDrinkoInputMessage im) {
        return (im.left instanceof KeyboardAnswer) && (_what==null || im.left.getMsg().equals(_what));
    }
    @Override
    public String toJsonString() {
        return new JSONObject(super.toJsonString()).put("value", _what).toString();
    }
}
