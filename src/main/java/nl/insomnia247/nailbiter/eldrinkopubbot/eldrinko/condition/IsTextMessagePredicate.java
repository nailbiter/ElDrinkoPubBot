package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;

/**
 * @author Alex Leontiev
 */
public class IsTextMessagePredicate extends ElDrinkoCondition {
    public IsTextMessagePredicate(Object o) {}
    @Override
    public boolean test(ElDrinkoInputMessage im) {
        return im.left instanceof TelegramTextInputMessage;
    }
}
