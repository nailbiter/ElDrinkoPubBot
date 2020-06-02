package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.InputMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Alex Leontiev
 */
public class TelegramInputMessage implements InputMessage {
    protected String _msg = null;
    private UserData _ud = null;
    protected TelegramInputMessage(String msg) {
        _msg = msg;
    }
    public String getMsg() {
        return _msg;
    }
    public static TelegramInputMessage CreateInputMessage(Update u) {
        TelegramInputMessage res = null;
        if (u.hasMessage()) {
            Message m = u.getMessage();
            System.err.format("CreateInputMessage: %s\n",m.getText());
            res = new TelegramTextInputMessage(m.getText());
        } else if(u.hasCallbackQuery()) {
		    String call_data = u.getCallbackQuery().getData();
            System.err.format("call_data: %s\n",call_data);
            res = new TelegramKeyboardAnswer(call_data);
        }
        res._ud = new UserData(u);
        return res;
    }
    @Override
    public String toString() {
        return String.format("TelegramInputMessage(%s)",_msg);
    }
    public UserData getUserData() {
        return _ud;
    }
}
