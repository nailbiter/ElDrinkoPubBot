package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import java.util.regex.Pattern;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer;
import java.util.function.Function;
import org.json.JSONObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;

/**
 * @author Alex Leontiev
 */
public class ElDrinkoConditionInflator implements Function<Object,Predicate<ElDrinkoInputMessage>>{
    private static Logger _Log = LogManager.getLogger();
    public static  ElDrinkoCondition ParseElDrinkoCondition(Object oo) {
        JSONObject o = (JSONObject)oo;
        if(o.getString("tag").equals("TrivialPredicate")) {
            return new TrivialPredicate(o.opt("value"));
        } else if(o.getString("tag").equals("IsPhoneNumberPredicate")) {
            return new IsPhoneNumberPredicate(o.opt("value"));
        } else if(o.optString("tag").equals("NegationPredicate")) {
            return new NegationPredicate(o.opt("value"));
        } else if(o.optString("tag").equals("IsTextMessagePredicate")) {
            return new IsTextMessagePredicate(o.opt("value"));
        } else if(o.optString("tag").equals("MessageComparisonPredicate")) {
            return new MessageComparisonPredicate(o.opt("value"));
        } else if(o.optString("tag").equals("MessageKeyboardComparisonPredicate")) {
            return new MessageKeyboardComparisonPredicate(o.opt("value"));
        } else if(o.optString("tag").equals("ConjunctionPredicate")) {
            return new ConjunctionPredicate(o.opt("value"));
        } else if(o.optString("tag").equals("JsonCheckFieldPredicate")) {
            return new JsonCheckFieldPredicate(o.opt("value"));
        } else if(o.optString("tag").equals("IsHalfIntegerFloatPredicate")) {
            return new IsHalfIntegerFloatPredicate(o.opt("value"));
        } else {
            _Log.error("8c817a6ca72e77d2c42e58aa");
            return null;
        }
    }
    @Override
    public Predicate<ElDrinkoInputMessage> apply(Object o) {
        _Log.info(SecureString.format("o: %s",o));
        return ParseElDrinkoCondition(o);
    }
}
