package nl.insomnia247.nailbiter.eldrinkopubbot;

import java.io.File;

import org.apache.commons.io.FileUtils;
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
        File file = new File("./secret.json");
        String content = FileUtils.readFileToString(file, "utf-8").trim();

		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramBotsApi.registerBot(new ElDrinkoPubBot(content,"ElDrinkoPubBot"));
    }
}
