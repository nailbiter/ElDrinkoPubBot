package nl.insomnia247.nailbiter.eldrinkopubbot.test_util;
import java.util.function.Consumer;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.json.JSONArray;

/**
 * @author Alex Leontiev
 */
public class MockConsumer implements Consumer<ImmutablePair<String,String>> {
    JSONArray _content;
    @Override
    public void accept(ImmutablePair<String,String> pair) {
        JSONArray item = new JSONArray();
        item.put(pair.left);
        item.put(pair.right);
        _content.put(item);
    }
}
