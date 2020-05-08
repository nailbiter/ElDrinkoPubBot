package nl.insomnia247.nailbiter.eldrinkopubbot.state_machine;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.lang.StringBuilder;


/**
 * @author Alex Leontiev
 */
public class StateMachine<InputMessage,OutputMessage> implements Function<InputMessage,OutputMessage> {
    protected String _currentState = null;
    protected Set<String> _states = new HashSet<>();
    protected Map<ImmutablePair<String,String>,ImmutablePair<Predicate<InputMessage>,Function<InputMessage,OutputMessage>>> _transitions = new HashMap<>();
    public StateMachine(String state) {
        _currentState = state;
        _states.add(state);
    }
    public StateMachine addTransition(String from, String to, Predicate<InputMessage> transitionCondition, Function<InputMessage,OutputMessage> transitionAction) {
        if(!_states.contains(to)) {
            _states.add(to);
        }
        if(!_states.contains(from)) {
            _states.add(from);
        }
        _transitions.put(
                new ImmutablePair<String,String>(from,to),
                new ImmutablePair<Predicate<InputMessage>, Function<InputMessage,OutputMessage>>(
                    transitionCondition,transitionAction
                    )
                );
        return this;
    }
    protected void _log(String msg) {
        System.err.println(msg);
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
        _log(String.format("apply: state: \"%s\"\nim: \"%s\"",_currentState,im));
        for(String to:_states) {
            ImmutablePair<Predicate<InputMessage>, Function<InputMessage,OutputMessage>> p = null;
            _log(String.format("checking %s -> %s",_currentState,to));
            if( (p=_transitions.get(new ImmutablePair<String,String>(_currentState,to))) != null ) {
                if(p.left.test(im)) {
                    _log(String.format("active transition: %s -> %s",_currentState,to));
                    try {
                      _setState(to);
                    } catch (StateMachineException sme) {
                        return null;
                    }
                    OutputMessage om = p.right.apply(im);
                    _log(String.format("om: \"%s\"",om));
                    return om;
                }
            }
        }
        _log("did not found suitable transition. returning null");
        return null;
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
    public StateMachine addStateMachine(StateMachine anotherStateMachine) {
        _states.addAll(anotherStateMachine._states);
        _transitions.putAll(anotherStateMachine._transitions);
        return this;
    }
}
