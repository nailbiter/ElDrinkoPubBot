package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.InputMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Alex Leontiev
 */
public class TelegramInputMessage implements InputMessage {
    private String _msg = null;
    protected TelegramInputMessage(String msg) {
        _msg = msg;
    }
    public String getMsg() {
        return _msg;
    }
    public static TelegramInputMessage CreateInputMessage(Update u) {
        if (u.hasMessage()) {
            Message m = u.getMessage();
            System.err.format("CreateInputMessage: %s\n",m.getText());
            return new TelegramTextInputMessage(m.getText());
        } else if(u.hasCallbackQuery()) {
		    String call_data = u.getCallbackQuery().getData();
            System.err.format("call_data: %s\n",call_data);
            return new TelegramKeyboardAnswer(call_data);
        } else {
            return null;
        }
    }
    @Override
    public String toString() {
        return String.format("TelegramInputMessage(%s)",_msg);
    }
}
