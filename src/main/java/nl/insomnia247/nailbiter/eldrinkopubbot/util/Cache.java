package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * @author Alex Leontiev
 */
public class Cache {
    protected final static Map<String,ImmutablePair<Date,Object>> _DATA 
        = Collections.synchronizedMap(new HashMap<String,ImmutablePair<Date,Object>>());
    private int _expirationSec;
    private static Logger _Log = LogManager.getLogger(Cache.class);
    public Cache(int expirationSec) {
        _expirationSec = expirationSec;
    }
    public Object get(String key) {
        Date now = new Date();
        if(_DATA.containsKey(key) && (now.getTime()-_DATA.get(key).left.getTime())/1000 < _expirationSec) {
            _Log.info("cache hit");
            return _DATA.get(key).right;
        } else {
            _Log.info("cache miss");
            return null;
        }
    }
    public Cache put(String key, Object val) {
        _DATA.put(key,new ImmutablePair<Date,Object>(new Date(),val));
        return this;
    }
}
