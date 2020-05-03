package nl.insomnia247.nailbiter.eldrinkopubbot;

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
import com.mongodb.MongoClientURI;
import com.mongodb.MongoClient;
import org.json.JSONObject;
import com.mongodb.client.model.Filters;

public class ElDrinkoPubBot extends TelegramLongPollingBot {
    private MongoClient _mongoClient = null;
    private String _botname = null;
    private JSONObject _config = null;
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId().toString());
            sendMessage.setText("Well, all information looks like noise until you break the code.");
            try {
                execute(sendMessage);
            } catch(TelegramApiException tae) {}
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
