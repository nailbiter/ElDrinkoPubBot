package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


/**
 * @author Alex Leontiev
 */
public class TelegramTextOutputMessage extends TelegramOutputMessage {
    public TelegramTextOutputMessage(UserData ud, String msg) {
        super(ud);
        setText(msg);
    }
}
