package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import org.json.JSONObject;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;

/**
 * @author Alex Leontiev
 */
public class TrivialPredicate extends ElDrinkoCondition {
    public TrivialPredicate(Object o) {}
    @Override
    public boolean test(ElDrinkoInputMessage im) {
        return true;
    }
}
