package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;

/**
 * @author Alex Leontiev
 */
public class TelegramKeyboardAnswer extends TelegramInputMessage implements KeyboardAnswer {
    public TelegramKeyboardAnswer(String ans) {
        super(ans);
    }
}
