package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import org.json.JSONObject;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;

/**
 * @author Alex Leontiev
 */
public class NegationPredicate extends ElDrinkoCondition {
    ElDrinkoCondition _pred;
    public NegationPredicate(Object o) {
        _pred = ElDrinkoConditionInflator.ParseElDrinkoCondition(o);
    }
    @Override
    public boolean test(ElDrinkoInputMessage im) {
        return !_pred.test(im);
    }
    @Override
    public String toJsonString() {
        return new JSONObject(super.toJsonString()).put("value", new JSONObject(_pred.toJsonString())).toString();
    }
}
