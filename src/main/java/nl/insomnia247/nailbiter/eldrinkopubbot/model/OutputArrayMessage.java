package nl.insomnia247.nailbiter.eldrinkopubbot.model;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * @author Alex Leontiev
 */
public class OutputArrayMessage implements OutputMessage {
    private OutputMessage[] _msgs;
    public OutputArrayMessage(OutputMessage[] msgs) {
        _msgs = msgs;
    }
    public OutputMessage[] getMessages() {
        return _msgs;
    }
    @Override
    public String toString() {
        String res = "";
        for(OutputMessage msg:_msgs) {
            res += msg.toString()+",";
        }
        return String.format("[%s]",res);
    }
    @Override
    public String toJsonString() {
        JSONArray arr = new JSONArray();
        for(OutputMessage om:_msgs) {
            arr.put(new JSONObject(om.toJsonString()));
        }
        return new JSONObject()
            .put("tag",this.getClass().getSimpleName())
            .put("value",arr)
            .toString();
    }
}
