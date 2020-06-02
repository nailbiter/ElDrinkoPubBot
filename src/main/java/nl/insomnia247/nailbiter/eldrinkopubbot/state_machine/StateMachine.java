package nl.insomnia247.nailbiter.eldrinkopubbot.state_machine;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.lang.StringBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


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
        _Log.info(String.format("apply: state: \"%s\"\nim: \"%s\"",_currentState,im));
        for(String to:_states) {
            List<ImmutablePair<Predicate<InputMessage>, Function<InputMessage,OutputMessage>>> pl
                = null;
            _Log.info(String.format("checking %s -> %s",_currentState,to));
            if( (pl=_transitions.get(new ImmutablePair<String,String>(_currentState,to))) != null ) {
                for(ImmutablePair<Predicate<InputMessage>, Function<InputMessage,OutputMessage>> p:pl) {
                    if(p.left.test(im)) {
                        _Log.info(String.format("active transition: %s -> %s",_currentState,to));
                        try {
                          _setState(to);
                        } catch (StateMachineException sme) {
                            return null;
                        }
                        OutputMessage om = p.right.apply(im);
                        _Log.info(String.format("om: \"%s\"",om));
                        return om;
                    }
                }
            }
        }
        _didNotFoundSuitableTransition(im);
        return null;
    }
    protected void _didNotFoundSuitableTransition(InputMessage im) {
        _Log.info(String.format("%s: did not found suitable transition. returning null",im));
                
    }
    protected void _onSetStateCallback(String state) {}
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for(ImmutablePair<String,String> p : _transitions.keySet()) {
            res.append(String.format("\"%s\" -> \"%s\"\n",p.left,p.right));
        }
        return res.toString();
    }
}
