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
    private static Logger _Log = LogManager.getLogger(ElDrinkoConditionInflator.class);
    private static final Predicate<ElDrinkoInputMessage> _TRIVIAL_PREDICATE
        = new Predicate<ElDrinkoInputMessage>(){
            @Override
            public boolean test(ElDrinkoInputMessage im) {
                return true;
            }
        };
    private static Predicate<ElDrinkoInputMessage> _MessageComparisonPredicate(String msg) {
        return new Predicate<ElDrinkoInputMessage>(){
            @Override
            public boolean test(ElDrinkoInputMessage im) {
                return im.left.getMsg().equals(msg);
            }
        };
    }
    private final static Predicate<ElDrinkoInputMessage> _IS_TEXT_MESSAGE = new Predicate<>() {
        @Override
        public boolean test(ElDrinkoInputMessage im) {
            return im.left instanceof TelegramTextInputMessage;
        }
    };
    private final static Predicate<ElDrinkoInputMessage> _IS_HALF_INTEGER_PREDICATE = new Predicate<>() {
        @Override
        public boolean test(ElDrinkoInputMessage tim) {
            if( !(tim.left instanceof TelegramTextInputMessage) ) {
                return false;
            }
            TelegramTextInputMessage ttim = (TelegramTextInputMessage) tim.left;
            float amount = 0;
            try {
                amount = MiscUtils.ParseFloat(ttim.getMsg());
            } catch(Exception e) {
                return false;
            }
            if( !MiscUtils.IsFloatInteger(2*amount) ) {
                return false;
            }
            return true;
        }
    };
    private final static Predicate<ElDrinkoInputMessage> _IS_PHONE_NUMBER_PREDICATE = new Predicate<>() {
        @Override
        public boolean test(ElDrinkoInputMessage tim) {
            if( !(tim.left instanceof TelegramTextInputMessage) ) {
                return false;
            }
            TelegramTextInputMessage ttim = (TelegramTextInputMessage) tim.left;
            return Pattern.matches("\\d+",ttim.getMsg());
        }
    };
    private static Predicate<ElDrinkoInputMessage> _MessageKeyboardComparisonPredicate(String msg) {
        return new Predicate<ElDrinkoInputMessage>(){
            @Override
            public boolean test(ElDrinkoInputMessage im) {
                return (im.left instanceof KeyboardAnswer) && (msg==null || im.left.getMsg().equals(msg));
            }
        };
    }
    @Override
    public Predicate<ElDrinkoInputMessage> apply(Object o) {
        _Log.info(SecureString.format("o: %s",o));
        if(o==JSONObject.NULL) {
            return _TRIVIAL_PREDICATE;
        } else if(((JSONObject)o).getString("tag").equals("IsPhoneNumberPredicate")) {
            return _IS_PHONE_NUMBER_PREDICATE;
        } else if(((JSONObject)o).getString("tag").equals("IsTextMessagePredicate")) {
            return _IS_TEXT_MESSAGE;
        } else if(((JSONObject)o).getString("tag").equals("MessageComparisonPredicate")) {
            return _MessageComparisonPredicate(((JSONObject)o).getString("value"));
        } else if(((JSONObject)o).getString("tag").equals("MessageKeyboardComparisonPredicate")) {
            return _MessageKeyboardComparisonPredicate( ((JSONObject)o).has("value") ? ((JSONObject)o).getString("value") : null );
        } else if(((JSONObject)o).getString("tag").equals("IsHalfIntegerFloatPredicate")) {
            return _IS_HALF_INTEGER_PREDICATE;
        } else {
            _Log.error("8c817a6ca72e77d2c42e58aa");
            return null;
        }
    }
}
