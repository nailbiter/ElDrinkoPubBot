package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.json.JSONObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author Alex Leontiev
 */
public class UserData {
    protected long _chatId = 0;
    protected String _username = null;
    protected String _firstName = null;
    protected String _lastName = null;
    private static Logger _Log = LogManager.getLogger();
    public UserData() {}
    public UserData(Update update) {
        Message m = null;

        if(update.hasMessage()) {
            m = update.getMessage();
        } else if(update.hasCallbackQuery()) {
            m = update.getCallbackQuery().getMessage();
        } else {
            _Log.error(update);
        }
        _Log.info(m);

        _chatId = m.getChatId();
        _username = m.getChat().getUserName();
        _firstName = m.getChat().getFirstName();
        _lastName = m.getChat().getLastName();
    }
    public Long getChatId() {
        return _chatId;
    }
    public String getUserName() {
        return "@"+_username;
    }
    public String getLastName() {
        return _lastName;
    }
    public String getFirstName() {
        return _firstName;
    }
    @Override
    public String toString() {
        return Long.toString(_chatId);
    }
    public String toJsonString() {
        return new JSONObject().put("chatId",_chatId).put("username",_username).toString();
    }
}
