package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboard;
import nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.StateMachineException;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.PersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.mongodb.MongoPersistentStorage;
import org.bson.Document;
import java.util.stream.Collectors;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import com.mongodb.MongoClientURI;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputArrayMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;


public class ElDrinkoPubBot extends TelegramLongPollingBot implements Consumer<ImmutablePair<String,String>> {
    private MongoClient _mongoClient = null;
    private String _botname = null;
    private JSONObject _config = null;
    private final Map<String,List<Long>> _masterChatIds = new HashMap<>();
    private static Logger _Log = LogManager.getLogger(ElDrinkoPubBot.class);
    private Map<String,Integer> _lastSentKeyboardHash = new HashMap<>();
    ElDrinkoStateMachine _edsm = null;
    ElDrinkoActionInflator _actionInflator = null;
    ElDrinkoConditionInflator _conditionInflator = null;
    private PersistentStorage _persistentStorage = null;
    @Override 
    public void accept(ImmutablePair<String,String> o) {
        _sendMessageToMasters(o.left,false,o.right);
    }
    private void _sendMessageToMasters(String msg, boolean isMarkdown, String key) {
        if(key.equals("developerChatIds")) {
            msg = SecureString.format("`(> %s <)`",msg);
            isMarkdown = true;
        }

        List<Long> list = new ArrayList<>();
        if( _masterChatIds.containsKey(key) ) {
            list.addAll(_masterChatIds.get(key));
        } else {
            list.add(Long.parseLong(key));
        }

        for(Long masterChatId : list) {
            SendMessage sendMessage = null;
            sendMessage = new SendMessage();
            String chatId = Long.toString(masterChatId);
            sendMessage.setChatId(chatId);
            sendMessage.setText(msg);
            if(isMarkdown) {
                sendMessage.enableMarkdown(true);
            }
            try {
                _Log.info(SecureString.format("sending %s to %s\n",msg,chatId));
                execute(sendMessage);
            } catch(Exception e) {
                _Log.error(SecureString.format("f531ae90faad7adb: \"%s\" \"%s\" \"%s\" \"%s\"\n",
                            e.getMessage(),
                            e.getClass().getName(),
                            "no",
                            e
                            ));
            }
        }
    }
    private TelegramInputMessage _createInputMessage(Update u) {
        if (u.hasMessage()) {
            Message m = u.getMessage();
            _Log.info("CreateInputMessage: %s\n",m.getText());
            return new TelegramTextInputMessage(m.getText());
        } else if(u.hasCallbackQuery()) {
            Integer replyMessageId = null;
		    String call_data = u.getCallbackQuery().getData();
            if(u.getCallbackQuery().getMessage().getReplyMarkup()!=null && u.getCallbackQuery().getMessage().getReplyMarkup().getKeyboard()!=null) {
                _Log.info(SecureString.format("inline markup: %s\n",u.getCallbackQuery().getMessage().getReplyMarkup().getKeyboard()));
                List<List<InlineKeyboardButton>> buttons = u.getCallbackQuery().getMessage().getReplyMarkup().getKeyboard();
                for(int i = 0, idx = 0;i<buttons.size();i++) {
                    List<InlineKeyboardButton> row = buttons.get(i);
                    for(int j = 0; j<row.size();j++,idx++) {
                        if(idx==Integer.parseInt(call_data)) {
                            SendMessage sendMessage = new SendMessage();
                            String chatId = Long.toString(u.getCallbackQuery().getMessage().getChatId());
                            sendMessage.setChatId(chatId);
                            sendMessage.setText(row.get(j).getText());
                            sendMessage.enableMarkdown(true);

                            UserData ud = new UserData(u);
                            replyMessageId = u.getCallbackQuery().getMessage().getMessageId();
                            if( _lastSentKeyboardHash.containsKey(ud.toString()) &&  !_lastSentKeyboardHash.get(ud.toString()).equals(replyMessageId)) {
                                _Log.info("it's old, so we ignore it");
                                return null;
                            }
                            
                            _Log.info(sendMessage);
                            try {
                                execute(new DeleteMessage(u.getCallbackQuery().getMessage().getChatId(),u.getCallbackQuery().getMessage().getMessageId()));
                                execute(sendMessage);
                            } catch(Exception e) {
                                _Log.error(e);
                            }
                        }
                    }
                }
            }

            _Log.info(SecureString.format("call_data: %s",call_data));
            _Log.info(SecureString.format("TelegramKeyboardAnswer(%s) to %s",call_data,replyMessageId));
            return new TelegramKeyboardAnswer(call_data);
        } else {
            return null;
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        TelegramInputMessage tim = _createInputMessage(update);
        if(tim != null) {
            UserData ud = new UserData(update);
            _Log.info(SecureString.format("config: %s",_config));
            MongoCollection<Document> statesColl = _mongoClient
                .getDatabase("beerbot")
                .getCollection(_config.getJSONObject("mongodb").getString("state_machine_states"));
            Document doc = statesColl.find(Filters.eq("id",ud.toString())).first();
            _Log.info(SecureString.format("doc: %s",doc));
            String state = null;
            if( doc == null ) {
                state = "_";
            } else {
                state = new JSONObject(doc.toJson()).getString("state");
            }
            _Log.info(SecureString.format("state: %s",state));
            try {
                _edsm.setState(state);
            } catch (Exception e) {
                _Log.error(e);
                return;
            }

            Document data = _mongoClient
                .getDatabase("beerbot")
                .getCollection(_config.getJSONObject("mongodb").getString("data"))
                .find(Filters.eq("id",ud.toString())).first();
            ElDrinkoInputMessage im = new ElDrinkoInputMessage(tim, data==null ? new JSONObject() : new JSONObject(data.toJson()).getJSONObject("data"), ud);

            _Log.info(SecureString.format("ss(%s): %s",im.userData,_edsm.getState()));
            _Log.info(SecureString.format("im(%s): %s",im.userData,im));
            ImmutablePair<OutputMessage,JSONObject> om = _edsm.apply(im);
            _Log.info(SecureString.format("es(%s): %s",im.userData,_edsm.getState()));
            _Log.info(SecureString.format("om(%s): %s",im.userData,om));

            _mongoClient.getDatabase("beerbot").getCollection(_config.getJSONObject("mongodb").getString("data"))
                .updateOne(Filters.eq("id",ud.toString()),Updates.set("data",Document.parse(om.right.toString())),new UpdateOptions().upsert(true));
            statesColl.updateOne(Filters.eq("id",ud.toString()),Updates.set("state",_edsm.getState()),new UpdateOptions().upsert(true));
            _execute(om.left,ud);
        }
    }
    void _execute(OutputMessage om, UserData ud) {
        try {
            if( om instanceof OutputArrayMessage ) {
                for(OutputMessage omm: ((OutputArrayMessage)om).getMessages()) {
                    _execute(omm,ud);
                }
            } else if (om instanceof SendMessage) {
                SendMessage sendMessage = (SendMessage) om;
                sendMessage.setParseMode("Markdown");
                sendMessage.enableMarkdown(true);
                sendMessage.setChatId(ud.getChatId().toString());
                Message resMessage = execute(sendMessage);
                _Log.info(SecureString.format("after execute(%s) as %s",sendMessage,resMessage.getMessageId()));
                if( om instanceof TelegramKeyboard ) {
                    _lastSentKeyboardHash.put(ud.toString(),resMessage.getMessageId());
                }
            } else if (om instanceof SendPhoto) {
                SendPhoto sendPhoto = (SendPhoto) om;
                sendPhoto.setChatId(ud.getChatId().toString());
                execute(sendPhoto);
            } else {
                throw new ElDrinkoStateMachine.ElDrinkoStateMachineException(SecureString.format("cannot _execute(%s,%s)",om,ud));
            }
        } catch(Exception e) {
            _Log.error(e);
        }
    }
    public ElDrinkoPubBot(String dbpass,String commit_hash, String botname) throws ElDrinkoStateMachine.ElDrinkoStateMachineException, StateMachineException {
        _mongoClient = _GetMongoClient(dbpass);
        _Log.info(SecureString.format("botname: %s",botname));
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
        _Log.info(SecureString.format("_config: %s\n",_config.toString()));
        _botname = botname;

        String[] MASTER_CHAT_IDS = new String[] {"salesmanChatIds","developerChatIds"};
        for(String s:MASTER_CHAT_IDS) {
            List<Long> l = new ArrayList<Long>();
            _masterChatIds.put(s, l);
            for(int i = 0; i < _config.getJSONObject("telegram").getJSONArray(s).length(); i++) {
                l.add( (long)_config.getJSONObject("telegram").getJSONArray(s).getInt(i) );
            }
        }
        _persistentStorage = new MongoPersistentStorage(_mongoClient.getDatabase("beerbot").getCollection("var"),"id",botname);
        ElDrinkoStateMachine.PreloadImages();
        _edsm = new ElDrinkoStateMachine(this);
        _actionInflator = new ElDrinkoActionInflator(this,_persistentStorage);
        _conditionInflator = new ElDrinkoConditionInflator();
        _edsm.inflateTransitionsFromJSON(_conditionInflator,_actionInflator, 
                new JSONObject(MiscUtils.GetResource("transitions",".json")).getJSONArray("correspondence").toString());
        _Log.info(SecureString.format("edsm: %s\n",_edsm));
        this._sendMessageToMasters(SecureString.format("updated! now at %s",commit_hash),true,"developerChatIds");
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
		String url = SecureString.format(mongo,password);
		MongoClientURI uri = null;
		try {
			uri = new MongoClientURI(url);
		}
		catch(Exception e) {
			_Log.info(SecureString.format("EXCEPTION!\n"));
		}
		return new MongoClient(uri);
	}
}
