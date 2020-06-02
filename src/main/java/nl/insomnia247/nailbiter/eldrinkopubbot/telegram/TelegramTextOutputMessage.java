package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


/**
 * @author Alex Leontiev
 */
public class TelegramTextOutputMessage extends TelegramOutputMessage {
    String _msg = null;
    public TelegramTextOutputMessage(String msg) {
        super();
        setText(msg);
        _msg = msg;
    }
    @Override
    public String toString() {
        return String.format("TelegramOutputMessage(%s)",_msg);
    }
}
