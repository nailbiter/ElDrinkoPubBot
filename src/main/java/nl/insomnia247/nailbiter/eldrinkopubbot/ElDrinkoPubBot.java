package nl.insomnia247.nailbiter.eldrinkopubbot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import nl.insomnia247.nailbiter.eldrinkopubbot.mongodb.PersistentStorage;
import java.util.stream.Collectors;
import com.mongodb.MongoClient;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.Filters;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputArrayMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.apache.commons.lang3.tuple.ImmutablePair;


public class ElDrinkoPubBot extends TelegramLongPollingBot implements Consumer<ImmutablePair<String,String>>{
    private MongoClient _mongoClient = null;
    private String _botname = null;
    private JSONObject _config = null;
    private Map<String, ElDrinkoStateMachine> _data = new HashMap<>();
    private final Map<String,List<Long>> _masterChatIds = new HashMap<>();
    private static Logger _Log = LogManager.getLogger(ElDrinkoPubBot.class);
    private PersistentStorage _persistentStorage = null;
    @Override 
    public void accept(ImmutablePair<String,String> o) {
        _sendMessageToMasters(o.left,false,o.right);
    }
    private void _sendMessageToMasters(String msg, boolean isMarkdown, String key) {
        if(key.equals("developerChatIds")) {
            msg = String.format("(] %s [)",msg);
            isMarkdown = true;
        }
        for(Long masterChatId : _masterChatIds.get(key)) {
            SendMessage sendMessage = new SendMessage();
            String chatId = Long.toString(masterChatId);
            sendMessage.setChatId(chatId);
            sendMessage.setText(msg);
            if(isMarkdown) {
                sendMessage.enableMarkdown(true);
            }
            try {
                _Log.info(String.format("sending %s to %s\n",msg,chatId));
                execute(sendMessage);
            } catch(Exception e) {
                _Log.info(String.format("f531ae90faad7adb: \"%s\" \"%s\" \"%s\" \"%s\"\n",
                            e.getMessage(),
                            e.getClass().getName(),
//                            ((TelegramApiException)e).getApiResponse(),
                            "no",
                            e
                            ));
            }
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        TelegramInputMessage tim = TelegramInputMessage.CreateInputMessage(update);
        _Log.info(String.format(" c4aa6c56bd61a895 \n"));
        if(tim != null) {
            _Log.info(String.format(" 78cbf16ed274bfe5 \n"));
            UserData ud = new UserData(update);
            ElDrinkoStateMachine edsm = null;
            _Log.info(String.format("here %s\n","fc4721b74e5c861c"));
            if( !_data.containsKey(ud.toString()) ) {
                _Log.info(String.format("here %s\n","abbfe7d43f0ae807"));
                edsm = new ElDrinkoStateMachine(ud, _mongoClient, this, _config, _persistentStorage)
                    .setUp()
                    ;
                _data.put(ud.toString(),edsm);
            } else {
                _Log.info(String.format("here %s\n","d6948b5130d382da"));
                edsm = _data.get(ud.toString());
            }
            _Log.info(String.format("%s\n",edsm));
            _Log.info(String.format("here %s\n","52b2688632dd0b0b"));
            _execute(edsm.apply(tim));
        }
    }
    void _execute(OutputMessage om) {
        if( om instanceof OutputArrayMessage ) {
            for(OutputMessage omm: ((OutputArrayMessage)om).getMessages()) {
                _execute(omm);
            }
        } else if (om instanceof SendMessage) {
            SendMessage sendMessage = (SendMessage) om;
            try {
		        sendMessage.setParseMode("Markdown");
                sendMessage.enableMarkdown(true);
                execute(sendMessage);
            } catch(TelegramApiException tae) {
                _Log.info(String.format("here %s\n","05f6e0757caf298b"));
            }
        } else if (om instanceof SendPhoto) {
            SendPhoto sendPhoto = (SendPhoto) om;
            try {
                execute(sendPhoto);
            } catch(TelegramApiException tae) {
                _Log.info(" 30517df9663111bd \n");
            }
        } else {
            _Log.info(" 3c269b0998783662 \n");
        }
    }
    ElDrinkoPubBot(String dbpass,String commit_hash, String botname) {
        _mongoClient = _GetMongoClient(dbpass);
        _config = _MergeJsonObjects(new JSONObject[] {
            new JSONObject(
                _mongoClient
                .getDatabase("beerbot")
                .getCollection("_keyring")
                .find(Filters.eq("id",botname))
                .first()
                .toJson()),
            new JSONObject(
                _mongoClient
                .getDatabase("beerbot")
                .getCollection("_settings")
                .find(Filters.eq("id",botname))
                .first()
                .toJson())
        });
        _Log.info(String.format("_config: %s\n",_config.toString()));
        _botname = botname;

        String[] MASTER_CHAT_IDS = new String[] {"salesmanChatIds","developerChatIds"};
        for(String s:MASTER_CHAT_IDS) {
            List<Long> l = new ArrayList<Long>();
            _masterChatIds.put(s, l);
            for(int i = 0; i < _config.getJSONObject("telegram").getJSONArray(s).length(); i++) {
                l.add( (long)_config.getJSONObject("telegram").getJSONArray(s).getInt(i) );
            }
        }
        _persistentStorage = new PersistentStorage(_mongoClient.getDatabase("beerbot").getCollection("var"),"id",botname);
        ElDrinkoStateMachine.PreloadImages();
        this._sendMessageToMasters(String.format("updated! now at %s",commit_hash),true,"developerChatIds");
    }

    private static JSONObject _MergeJsonObjects(JSONObject[] objs) {
        JSONObject res = new JSONObject(objs[0].toString());
        for(int i = 1; i < objs.length; i++) {
            for(String key:objs[i].keySet()) {
                if(res.has(key) && res.get(key) instanceof JSONObject && objs[i].get(key) instanceof JSONObject) {
                    JSONObject combinedObj 
                        = _MergeJsonObjects(new JSONObject[]{
                            (JSONObject)res.get(key),
                                (JSONObject)objs[i].get(key)
                        });
                    res.put(key,combinedObj);
                } else {
                    res.put(key,objs[i].get(key));
                }
            }
        }
        return res;
    }

    @Override
    public String getBotUsername() {
        return _botname;
    }

    @Override
    public String getBotToken() {
        return _config.getJSONObject("telegram").getString("token");
    }
	private static MongoClient _GetMongoClient(String password) {
        String mongo = "mongodb+srv://nailbiter:%s@cluster0-ta3pc.gcp.mongodb.net/test?retryWrites=true&w=majority";
		String url = String.format(mongo,password);
		MongoClientURI uri = null;
		try {
			uri = new MongoClientURI(url);
		}
		catch(Exception e) {
			_Log.info(String.format("EXCEPTION!\n"));
		}
		return new MongoClient(uri);
	}
}
