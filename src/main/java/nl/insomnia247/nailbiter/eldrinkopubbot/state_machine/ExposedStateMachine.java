package nl.insomnia247.nailbiter.eldrinkopubbot.state_machine;

public class ExposedStateMachine<InputMessage,OutputMessage> extends StateMachine<InputMessage,OutputMessage> {
    public ExposedStateMachine(String state) {
        super(state);
    }
    public void setState(String state) throws StateMachineException {
        _setState(state);
    }
    public String getState() {
        return _currentState;
    }
}
