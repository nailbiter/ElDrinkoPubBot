package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.Logger;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;
import org.apache.logging.log4j.LogManager;

/**
 * @author Alex Leontiev
 */
public class ConjunctionPredicate extends ElDrinkoCondition {
    Logger _Log = LogManager.getLogger();
    List<ElDrinkoCondition> _conditions = new ArrayList<>();
    public ConjunctionPredicate(Object o) {
        JSONArray arr = (JSONArray) o;
        for(int i = 0; i < arr.length(); i++) {
            _conditions.add(ElDrinkoConditionInflator.ParseElDrinkoCondition(arr.getJSONObject(i)));
        }
    }
    @Override
    public boolean test(ElDrinkoInputMessage tim) {
        _Log.info(SecureString.format("tim: %s",tim));
        for(int i = 0; i < _conditions.size(); i++) {
            _Log.info(SecureString.format("c: %s",_conditions.get(i).toJsonString()));
            if (!_conditions.get(i).test(tim)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public String toJsonString() {
        JSONArray value = new JSONArray();
        for(int i = 0; i < _conditions.size(); i++) {
            value.put(new JSONObject(_conditions.get(i).toJsonString()));
        }
        return new JSONObject(super.toJsonString()).put("value", value).toString();
    }
}
