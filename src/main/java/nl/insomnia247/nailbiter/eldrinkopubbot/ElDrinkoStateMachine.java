package nl.insomnia247.nailbiter.eldrinkopubbot;
import com.mongodb.MongoClient;
import java.util.function.Function;
import java.util.function.Predicate;
import nl.insomnia247.nailbiter.eldrinkopubbot.mongodb.PersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.StateMachine;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboard;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;


/**
 * @author Alex Leontiev
 */
public class ElDrinkoStateMachine extends StateMachine<TelegramInputMessage,TelegramOutputMessage> {
    private final UserData _ud;
    private final PersistentStorage _persistentStorage;
    public ElDrinkoStateMachine(UserData ud, MongoClient mongoClient) {
        super("_");
        _ud = ud;
        _persistentStorage = new PersistentStorage(
                mongoClient.getDatabase("beerbot").getCollection("data"), 
                "id",
                ud.toString()
                );
    }
    private static Predicate<TelegramInputMessage> _TrivialPredicate() {
        return new Predicate<TelegramInputMessage>(){
        
            @Override
            public boolean test(TelegramInputMessage im) {
                return true;
            }
        };
    }
    private Function<TelegramInputMessage,TelegramOutputMessage> _keyboardMessage(String msg, String[] categories) {
        return new Function<TelegramInputMessage,TelegramOutputMessage>() {
            @Override
            public TelegramOutputMessage apply(TelegramInputMessage im) {
                return new TelegramKeyboard(_ud, msg, categories);
            }
        };
    }
    public ElDrinkoStateMachine setUp() {
            System.err.format("%s\n","52970894e62074d5");

            ElDrinkoStateMachine res = (ElDrinkoStateMachine) this
                .addTransition("_", "start", _TrivialPredicate(), _keyboardMessage("Добро Пожаловать.",
                            new String[]{"Посмотреть описание","Сформировать заказ покупку"}
                            ))
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
