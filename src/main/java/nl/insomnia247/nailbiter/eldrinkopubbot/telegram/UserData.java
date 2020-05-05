package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Alex Leontiev
 */
public class UserData {
    private long _chatId = 0;
    public UserData(Update update) {
        if(update.hasMessage()) {
            _chatId = update.getMessage().getChatId();
        } else if(update.hasCallbackQuery()) {
            _chatId = update.getCallbackQuery().getMessage().getChatId();
        }
    }
    Long getChatId() {
        return _chatId;
    }
    @Override
    public String toString() {
        return Long.toString(_chatId);
    }
}
