package nl.insomnia247.nailbiter.eldrinkopubbot.test_util;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.PersistentStorage;
import org.json.JSONObject;

/**
 * @author Alex Leontiev
 */
public class MockPersistentStorage implements PersistentStorage {
    JSONObject _content = new JSONObject();
    public boolean contains(String key) {
        return _content.has(key);
    }
    public String get(String key) {
        return _content.getString(key);
    }
    public PersistentStorage set(String key, String val) {
        _content.put(key,val);
        return this;
    }
}
