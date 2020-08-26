package nl.insomnia247.nailbiter.eldrinkopubbot.state_machine;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Comparator;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.lang.StringBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Alex Leontiev
 */
public class StateMachine<InputMessage,OutputMessage> implements Function<InputMessage,OutputMessage> {
    protected String _currentState = null;
    protected Set<String> _states = new HashSet<>();
    private static Logger _Log = LogManager.getLogger(StateMachine.class);
    protected Map<ImmutablePair<String,String>,List<ImmutablePair<Predicate<InputMessage>,Function<InputMessage,OutputMessage>>>> _transitions = new HashMap<>();
    public StateMachine(String state) {
        _currentState = state;
        _states.add(state);
    }
    public void inflateTransitionsFromJSON(Function<Object,Predicate<InputMessage>> conditionInflator, Function<Object,Function<InputMessage,OutputMessage>> actionInflator, String s) throws StateMachineException {
        JSONArray source = new JSONArray(s);
        for(int i = 0; i < source.length(); i++) {
            JSONArray a = source.getJSONArray(i);
            String start = a.isNull(0) ? null : a.getString(0)
                , end = a.isNull(1) ? null : a.getString(1);
            Predicate<InputMessage> pred = conditionInflator.apply(a.get(2));
            Function<InputMessage,OutputMessage> action = actionInflator.apply(a.get(3));

            _Log.info(SecureString.format("start: %s, end: %s",start,end));
            if(start!=null && end!=null) {
                this.addTransition(start,end,pred, action);
            } else if (start==null && end!=null) {
                for(String _start:this._states) {
                    this.addTransition(_start,end,pred,action);
                }
            } else {
                throw new StateMachineException("aed8ed251586aa75f5243c3a");
            }
        }
    }
    public StateMachine addTransition(String from, String to, 
            Predicate<InputMessage> transitionCondition, 
            Function<InputMessage,OutputMessage> transitionAction) {
        assert from != null;
        assert to != null;
        _states.add(to);
        _states.add(from);
        ImmutablePair<String,String> key = new ImmutablePair<>(from,to);
        if( !_transitions.containsKey(key) ) {
            _transitions.put(key,new ArrayList<ImmutablePair<Predicate<InputMessage>,Function<InputMessage,OutputMessage>>>());
        }
        _transitions
            .get(key)
            .add(new ImmutablePair<Predicate<InputMessage>, Function<InputMessage,OutputMessage>>(
                    transitionCondition,transitionAction
                    ));
        return this;
    }
    protected void _setState(String state) throws StateMachineException {
        if(!_states.contains(state)) {
            throw new StateMachineException(state);
        }
        _currentState = state;
        _onSetStateCallback(state);
    }
    @Override
    public OutputMessage apply(InputMessage im) {
        _Log.info(SecureString.format("apply: state: \"%s\"\nim: \"%s\"",_currentState,im));
        for(String to:_states) {
            List<ImmutablePair<Predicate<InputMessage>, Function<InputMessage,OutputMessage>>> pl
                = _transitions.get(new ImmutablePair<String,String>(_currentState,to));
            _Log.info(SecureString.format("checking %s -> %s [%s]",_currentState,to,pl));
            if( pl != null ) {
                for(ImmutablePair<Predicate<InputMessage>, Function<InputMessage,OutputMessage>> p:pl) {
                    if(p.left.test(im)) {
                        _Log.info(SecureString.format("active transition: %s -> %s",_currentState,to));
                        try {
                          _setState(to);
                        } catch (StateMachineException sme) {
                            return null;
                        }
                        OutputMessage om = p.right.apply(im);
                        _Log.info(SecureString.format("om: \"%s\"",om));
                        return om;
                    }
                }
            }
        }
        _didNotFoundSuitableTransition(im);
        return null;
    }
    protected void _didNotFoundSuitableTransition(InputMessage im) {
        _Log.info(SecureString.format("%s: did not found suitable transition. returning null",im));
    }
    protected void _onSetStateCallback(String state) {
        _Log.info(SecureString.format("state: %s\n",state));
    }
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for(ImmutablePair<String,String> p : _transitions.keySet()) {
            res.append(SecureString.format("\"%s\" -> \"%s\"\n",p.left,p.right));
        }
        return res.toString();
    }
    public String toJsonString() {
        return toJsonString(new Function<Predicate<InputMessage>,Object>(){
            @Override
            public Object apply(Predicate<InputMessage> p) {
                return JSONObject.NULL;
            }
        }, new Function<Function<InputMessage,OutputMessage>,Object>() {
            @Override
            public Object apply(Function<InputMessage,OutputMessage> f){
                return JSONObject.NULL;
            }
        });
    }
    public String toJsonString(Function<Predicate<InputMessage>,Object> predicatePrinter, Function<Function<InputMessage,OutputMessage>,Object> transitionPrinter) {
        JSONObject res = new JSONObject();
        res.put("currentState",_currentState);

        JSONObject states = new JSONObject();
        for(String s:_states) {
            states.put(s,1);
        }
        res.put("states",states);

        JSONObject transitions = new JSONObject();
        for(ImmutablePair<String,String> p:_transitions.keySet()) {
            JSONArray val = new JSONArray();
            for(ImmutablePair<Predicate<InputMessage>,Function<InputMessage,OutputMessage>> pp: _transitions.get(p)) {
                JSONArray item = new JSONArray();
                item.put(predicatePrinter.apply(pp.left));
                item.put(transitionPrinter.apply(pp.right));
                val.put(item);
            }
            transitions.put(new JSONArray().put(p.left).put(p.right).toString(),val);
        }
        res.put("transitions",transitions);

        return res.toString();
    }
}
