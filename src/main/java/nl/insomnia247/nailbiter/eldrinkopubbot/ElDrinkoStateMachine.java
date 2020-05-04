package nl.insomnia247.nailbiter.eldrinkopubbot;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.StateMachine;
import java.util.function.Predicate;
import java.util.function.Function;
import com.mongodb.MongoClient;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import nl.insomnia247.nailbiter.eldrinkopubbot.mongodb.PersistentStorage;


/**
 * @author Alex Leontiev
 */
public class ElDrinkoStateMachine extends StateMachine<TelegramInputMessage,TelegramOutputMessage> {
    private final UserData _ud;
    private final PersistentStorage _persistentStorage;
    public ElDrinkoStateMachine(UserData ud, MongoClient mongoClient) {
        super("start");
        System.err.format("%s\n","f6cb33845066c88e");
        _ud = ud;
        _persistentStorage = new PersistentStorage(
                mongoClient.getDatabase("beerbot").getCollection("data"), 
                "id",
                ud.toString()
                );
        System.err.format("%s\n","aecb56794ac95ffb");
    }
    public ElDrinkoStateMachine setUp() {
            System.err.format("%s\n","52970894e62074d5");

            ElDrinkoStateMachine res = (ElDrinkoStateMachine) this
                .addTransition("start",
                    "end",
                    new Predicate<TelegramInputMessage>(){
                        @Override
                        public boolean test(TelegramInputMessage im) {
                            return true;
                        }
                    }, 
                    new Function<TelegramInputMessage,TelegramOutputMessage>() {
                        @Override
                        public TelegramOutputMessage apply(TelegramInputMessage im) {
                            return new TelegramTextOutputMessage(_ud,"start -> end");
                        }
                    }
                )
                .addTransition("end",
                    "start",
                    new Predicate<TelegramInputMessage>(){
                        @Override
                        public boolean test(TelegramInputMessage im) {
                            return true;
                        }
                    }, 
                    new Function<TelegramInputMessage,TelegramOutputMessage>() {
                        @Override
                        public TelegramOutputMessage apply(TelegramInputMessage im) {
                            return new TelegramTextOutputMessage(_ud,"end -> start");
                        }
                    }
                )
                ;
            if(_persistentStorage.contains("state")) {
                System.err.format("setting _currentState to \"%s\"\n",_persistentStorage.get("state"));
                this._currentState = _persistentStorage.get("state");
            }
            return res;
    }
    @Override
    protected void _onSetStateCallback(String state) {
        System.err.format("state: %s\n",state);
        _persistentStorage.set("state",state);
    }
}
