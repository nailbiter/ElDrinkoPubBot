package nl.insomnia247.nailbiter.eldrinkopubbot.test_util;
import org.json.JSONObject;
import java.util.Iterator;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.Tsv;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Alex Leontiev
 */
public class MockBeerlist extends Tsv {
    public MockBeerlist(JSONObject a) {
        for(int i = 0; i < a.getJSONArray("headers").length();i++) {
            String h = a.getJSONArray("headers").getString(i);
            _headers.add(h);
            List<String> c = new ArrayList<>();
            for(int j = 0; j < a.getJSONObject("content").getJSONArray(h).length();j++) {
                c.add(a.getJSONObject("content").getJSONArray(h).getString(j));
            }
            _content.put(h, c);
        }
    }
}
