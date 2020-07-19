package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import java.util.regex.Pattern;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;

/**
 * @author Alex Leontiev
 */
public class IsPhoneNumberPredicate extends ElDrinkoCondition {
    public IsPhoneNumberPredicate(Object o) {}
    @Override
    public boolean test(ElDrinkoInputMessage tim) {
        if( !(tim.left instanceof TelegramTextInputMessage) ) {
            return false;
        }
        TelegramTextInputMessage ttim = (TelegramTextInputMessage) tim.left;
        return Pattern.matches("\\d+",ttim.getMsg());
    }
}
