package nl.insomnia247.nailbiter.eldrinkopubbot.state_machine;

/**
 * @author Alex Leontiev
 */
public class StateMachineException extends Exception {
    public StateMachineException() {
        super();
    }
    public StateMachineException(String msg) {
        super(msg);
    }
}
