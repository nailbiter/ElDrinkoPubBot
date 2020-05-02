package nl.insomnia247.nailbiter.eldrinkopubbot;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import nl.insomnia247.nailbiter.eldrinkopubbot.ElDrinkoPubBot;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );
        File file = new File("/Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/secret.json");
        String content = FileUtils.readFileToString(file, "utf-8");

        // Convert JSON string to JSONObject
        JSONObject tomJsonObject = new JSONObject(content);
        System.out.format("here is json: %s\n",tomJsonObject);

		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramBotsApi.registerBot(new ElDrinkoPubBot(tomJsonObject.getString("token"),tomJsonObject.getString("dbpass")));
    }
}
