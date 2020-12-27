package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;
import com.mongodb.MongoClient;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition.ElDrinkoCondition;
import java.util.Random;
import com.mongodb.client.MongoCollection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputArrayMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.PersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.ExposedStateMachine;
import nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.StateMachineException;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramImageOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboard;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.DownloadCache;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.JSONTools;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.TemplateEngine;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.Tsv;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Alex Leontiev
 */
public class ElDrinkoStateMachine extends ExposedStateMachine<ElDrinkoInputMessage,ImmutablePair<OutputMessage,JSONObject>> {
    private static final String _BEERLIST = MiscUtils.GetResource("beerlist",".txt");
    private final Consumer<ImmutablePair<String,String>> _sendOrderCallback;
    private static final Logger _Log = LogManager.getLogger(ElDrinkoStateMachine.class);
    public static class ElDrinkoStateMachineException extends Exception {
        public ElDrinkoStateMachineException(String s) {
            super(s);
        }
    }
    public ElDrinkoStateMachine(Consumer<ImmutablePair<String,String>> sendOrderCallback) {
        super("_");
        _sendOrderCallback = sendOrderCallback;
    }
    public static void PreloadImages(MongoCollection<Document> coll) {
        Tsv tsv = new Tsv(coll);
        _Log.info(SecureString.format("tsv: %s",tsv));
        for(String imgUrl:tsv.getColumn("image link")) {
            _Log.info(SecureString.format("start preloading %s",imgUrl));
            String filePath = new DownloadCache(".png").get(MiscUtils.SafeUrl(imgUrl));
            _Log.info(SecureString.format("save %s -> %s",imgUrl,filePath));
        }
    }
    @Override
    protected void _didNotFoundSuitableTransition(ElDrinkoInputMessage im) {
        super._didNotFoundSuitableTransition(im);

        Map<String,Object> map = new HashMap<>();
        map.put("error_code",Math.abs(new Random().nextInt()));
        String userMessage = MiscUtils.ProcessTemplate("421a419b2a7139c88298f2ce",map,im.beerlist);
        _Log.info(userMessage);
        _sendOrderCallback.accept(new ImmutablePair<String,String>(
                    userMessage,
                    im.userData.getChatId().toString()
                    ));
        _sendOrderCallback.accept(new ImmutablePair<String,String>(
                    SecureString.format("%s cannot find suitable transition \"%s\" \"%s\"",
                        im.userData,
                        _currentState,
                        im),
                    "developerChatIds"
                    ));
    }
    @Override
    public String toJsonString() {
        return toJsonString(new Function<Predicate<ElDrinkoInputMessage>,Object>(){
            @Override
            public Object apply(Predicate<ElDrinkoInputMessage> p) {
                ElDrinkoCondition _p = (ElDrinkoCondition)p;
                _Log.info(SecureString.format("_p: %s",_p.toJsonString()));
                return new JSONObject(_p.toJsonString());
            }
        }, new Function<Function<ElDrinkoInputMessage,ImmutablePair<OutputMessage,JSONObject>>,Object>() {
            @Override
            public Object apply(Function<ElDrinkoInputMessage,ImmutablePair<OutputMessage,JSONObject>> f){
                return JSONObject.NULL;
            }
        });
    }
}
