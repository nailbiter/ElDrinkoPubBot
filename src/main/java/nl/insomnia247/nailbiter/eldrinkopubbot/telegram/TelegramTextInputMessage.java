package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.TextInputMessage;
import org.json.JSONObject;

/**
 * @author Alex Leontiev
 */
public class TelegramTextInputMessage extends TelegramInputMessage implements TextInputMessage {
    public TelegramTextInputMessage(String ans) {
        super(ans);
    }
}
