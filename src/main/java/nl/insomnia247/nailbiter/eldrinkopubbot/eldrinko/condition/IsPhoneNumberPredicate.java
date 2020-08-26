package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import java.util.regex.Pattern;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;

/**
 * @author Alex Leontiev
 */
public class IsPhoneNumberPredicate extends ElDrinkoCondition {
    private static Logger _Log = LogManager.getLogger();
    public IsPhoneNumberPredicate(Object o) {}
    @Override
    public boolean test(ElDrinkoInputMessage tim) {
        _Log.info(tim);
        if( !(tim.left instanceof TelegramTextInputMessage) ) {
            _Log.info("in if");
            return false;
        }
        TelegramTextInputMessage ttim = (TelegramTextInputMessage) tim.left;
        boolean res = Pattern.matches("\\d+",ttim.getMsg());
        _Log.info(SecureString.format("res: %s",res));
        return res;
    }
}
