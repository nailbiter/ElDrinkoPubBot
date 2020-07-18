package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * @author Alex Leontiev
 */
public class JSONTools {
    static final Logger _Log = LogManager.getLogger(JSONTools.class);
    public static Map<String,Object> JSONObjectToMap(JSONObject o) {
        Map<String,Object> res = new HashMap<>();
        for(String key:o.keySet()) {
            Object val = o.get(key);
            if(val instanceof JSONObject) {
                res.put(key,JSONObjectToMap((JSONObject)val));
            } else if(val instanceof JSONArray) {
                res.put(key,JSONArrayToList((JSONArray)val));
            } else {
                res.put(key,val);
            }
        }

//        _Log.info(SecureString.format("JSONObjectToMap: %s -> %s",o,res));
        return res;
    }
    public static List<Object> JSONArrayToList(JSONArray a) {
        List<Object> res = new ArrayList<>();
        for(int i = 0; i < a.length(); i++) {
            Object val = a.get(i);
            if(val instanceof JSONObject) {
                res.add(JSONObjectToMap((JSONObject)val));
            } else if(val instanceof JSONArray) {
                res.add(JSONArrayToList((JSONArray)val));
            } else {
                res.add(val);
            }
        }

//        _Log.info(SecureString.format("JSONArrayToList: %s -> %s",a,res));
        return res;
    }
}
