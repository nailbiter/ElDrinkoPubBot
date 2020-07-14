package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


/**
 * @author Alex Leontiev
 */
public class TelegramOutputMessage extends SendMessage implements OutputMessage {
    public TelegramOutputMessage() {
        super();
    }
}
