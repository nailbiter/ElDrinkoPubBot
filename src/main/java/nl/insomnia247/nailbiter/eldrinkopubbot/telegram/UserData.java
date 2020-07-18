package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.json.JSONObject;

/**
 * @author Alex Leontiev
 */
public class UserData {
    protected long _chatId = 0;
    protected String _username = null;
    public UserData() {}
    public UserData(Update update) {
        if(update.hasMessage()) {
            _chatId = update.getMessage().getChatId();
            _username = update.getMessage().getFrom().getUserName();
        } else if(update.hasCallbackQuery()) {
            _chatId = update.getCallbackQuery().getMessage().getChatId();
        }
    }
    public Long getChatId() {
        return _chatId;
    }
    public String getUserName() {
        return "@"+_username;
    }
    @Override
    public String toString() {
        return Long.toString(_chatId);
    }
    public String toJsonString() {
        return new JSONObject().put("chatId",_chatId).put("username",_username).toString();
    }
}
