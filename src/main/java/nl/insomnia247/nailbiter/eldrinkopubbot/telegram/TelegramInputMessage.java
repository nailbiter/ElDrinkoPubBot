package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.InputMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Alex Leontiev
 */
public class TelegramInputMessage implements InputMessage {
    public static TelegramInputMessage CreateInputMessage(Message m) {
        return new TelegramInputMessage();
    }
}
