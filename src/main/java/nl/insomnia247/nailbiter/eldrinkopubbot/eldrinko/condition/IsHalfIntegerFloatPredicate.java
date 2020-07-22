package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;

/**
 * @author Alex Leontiev
 */
public class IsHalfIntegerFloatPredicate extends ElDrinkoCondition {
    public IsHalfIntegerFloatPredicate(Object o) {}
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
}
