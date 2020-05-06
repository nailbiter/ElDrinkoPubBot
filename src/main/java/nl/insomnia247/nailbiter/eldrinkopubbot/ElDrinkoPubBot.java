package nl.insomnia247.nailbiter.eldrinkopubbot;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.model.Filters;
import java.util.HashMap;
import java.util.Map;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputArrayMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import org.json.JSONObject;
import java.util.function.Consumer;
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


public class ElDrinkoPubBot extends TelegramLongPollingBot implements Consumer<JSONObject>{
    private MongoClient _mongoClient = null;
    private String _botname = null;
    private JSONObject _config = null;
    private Map<String, ElDrinkoStateMachine> _data = new HashMap<>();
    private static final long _MASTER_CHAT_ID = 145766172;
    @Override 
    public void accept(JSONObject o) {
        SendMessage sendMessage = new SendMessage();
        String chatId = Long.toString(_MASTER_CHAT_ID);
        sendMessage.setChatId(chatId);
        sendMessage.setText(o.toString());
        try {
            System.err.format("sending %s to %s\n",o.toString(),chatId);
            execute(sendMessage);
        } catch(Exception e) {
            System.err.format(" f531ae90faad7adb \n");
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        TelegramInputMessage tim = TelegramInputMessage.CreateInputMessage(update);
        System.err.format(" c4aa6c56bd61a895 \n");
        if(tim != null) {
            System.err.format(" 78cbf16ed274bfe5 \n");
            UserData ud = new UserData(update);
            ElDrinkoStateMachine edsm = null;
            System.err.format("here %s\n","fc4721b74e5c861c");
            if( !_data.containsKey(ud.toString()) ) {
                System.err.format("here %s\n","abbfe7d43f0ae807");
                edsm = new ElDrinkoStateMachine(ud, _mongoClient, this)
                    .setUp()
                    ;
                _data.put(ud.toString(),edsm);
            } else {
                System.err.format("here %s\n","d6948b5130d382da");
                edsm = _data.get(ud.toString());
            }
            System.err.format("%s\n",edsm);
            System.err.format("here %s\n","52b2688632dd0b0b");
            _execute(edsm.apply(tim));
        }
    }
    void _execute(OutputMessage om) {
        if( om instanceof OutputArrayMessage ) {
            for(OutputMessage omm: ((OutputArrayMessage)om).getMessages()) {
                _execute(omm);
            }
        } else {
            SendMessage sendMessage = (SendMessage) om;
            try {
		        sendMessage.setParseMode("Markdown");
                execute(sendMessage);
            } catch(TelegramApiException tae) {
                System.err.format("here %s\n","05f6e0757caf298b");
            }
        }
    }
    ElDrinkoPubBot(String dbpass, String botname) {
        _mongoClient = _GetMongoClient(dbpass);
        _config = new JSONObject(
                _mongoClient
                .getDatabase("beerbot")
                .getCollection("_keyring")
                .find(Filters.eq("id",botname))
                .first()
                .toJson());
        System.err.format("_config: %s\n",_config.toString());
        _botname = botname;
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
		String url = String.format("mongodb+srv://%s:%s@cluster0-ta3pc.gcp.mongodb.net/%s?retryWrites=true&w=majority", 
	            "nailbiter",password,"beerbot");
		MongoClientURI uri = null;
		try {
			uri = new MongoClientURI(url);
		}
		catch(Exception e) {
			System.err.format("EXCEPTION!\n");
		}
		return new MongoClient(uri);
	}
}
