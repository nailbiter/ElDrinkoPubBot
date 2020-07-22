package nl.insomnia247.nailbiter.eldrinkopubbot.util;

/**
 * @author Alex Leontiev
 */
public interface PersistentStorage {
    public boolean contains(String key);
    public String get(String key);
    public PersistentStorage set(String key, String val);
}
