package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;

/**
 * @author Alex Leontiev
 */
public class UserData {
    private long _chatId = 0;
    public UserData(long chatId) {
        _chatId = chatId;
    }
    Long getChatId() {
        return _chatId;
    }
    @Override
    public String toString() {
        return Long.toString(_chatId);
    }
}
