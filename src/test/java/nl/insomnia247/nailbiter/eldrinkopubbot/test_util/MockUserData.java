package nl.insomnia247.nailbiter.eldrinkopubbot.test_util;
import nl.insomnia247.nailbiter.eldrinkobubbot.telegram.UserData;

/**
 * @author Alex Leontiev
 */
public class MockUserData extends UserData {
    public MockUserData() {
        _chatId = -1;
        _username = "mock_username";
    }
}
