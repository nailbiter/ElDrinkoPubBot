package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.InputMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Alex Leontiev
 */
public class TelegramInputMessage implements InputMessage {
    protected String _msg = null;
    protected TelegramInputMessage(String msg) {
        _msg = msg;
    }
    public String getMsg() {
        return _msg;
    }
    @Override
    public String toString() {
        return String.format("TelegramInputMessage(%s)",_msg);
    }
    public String toJsonString() {
        return new JSONObject().put("tag",getClass().getSimpleName()).put("msg",_msg).toString();
    }
}
