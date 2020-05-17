package nl.insomnia247.nailbiter.eldrinkopubbot.model;

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
}
