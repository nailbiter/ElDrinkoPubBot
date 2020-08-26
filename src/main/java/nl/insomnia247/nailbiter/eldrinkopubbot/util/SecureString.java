package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alex Leontiev
 */
public class SecureString {
    private final static String _MASK = "***";
    private final static Set<String> _SENSITIVE_INFO = new HashSet<>();
    public static void setHiddenInfo(String what) {
        _SENSITIVE_INFO.add(what);
    }
    /**
     * FIXME: remove and move this functionality to constructor
     */
    public static String format(String format,Object... args) {
        String res = String.format(format,args);
        for(String what: _SENSITIVE_INFO) {
            res = res.replace(what,_MASK);
        }
        return res;
    }
}
