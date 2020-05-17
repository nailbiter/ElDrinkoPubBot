package nl.insomnia247.nailbiter.eldrinkopubbot;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import nl.insomnia247.nailbiter.eldrinkopubbot.ElDrinkoPubBot;
//import org.apache.logging.log4j.PropertyConfigurator;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        File file = new File("./secret.txt");
        String content = FileUtils.readFileToString(file, "utf-8").trim();

//		PropertyConfigurator.configure(App.class.getClassLoader().getResource("log4j.properties"));

		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramBotsApi.registerBot(new ElDrinkoPubBot(content, args.length>0 ? args[0] : "ElDrinkoPubBot"));
    }
}
