package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import java.util.function.Predicate;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import org.json.JSONObject;

/**
 * @author Alex Leontiev
 */
public abstract class ElDrinkoCondition implements Predicate<ElDrinkoInputMessage> {
    public String toJsonString() {
        return new JSONObject().put("tag",getClass().getSimpleName()).put("value",JSONObject.NULL).toString();
    }
}
