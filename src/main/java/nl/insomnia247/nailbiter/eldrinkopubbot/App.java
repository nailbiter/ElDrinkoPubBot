package nl.insomnia247.nailbiter.eldrinkopubbot;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import nl.insomnia247.nailbiter.eldrinkopubbot.ElDrinkoPubBot;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    private static Logger _Log = LogManager.getLogger(App.class);
    public static void main( String[] args ) throws Exception
    {
        File file = new File("./secret.txt");
        String content = FileUtils.readFileToString(file, "utf-8").trim();

        _Log.info(String.format("args(%d): [%s]",args.length,String.join(",",Arrays.asList(args))));

		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		telegramBotsApi.registerBot(new ElDrinkoPubBot(content 
                    , args[0]
                    ,args.length>1 ? args[1] : "ElDrinkoPubBot"
                    ));
    }
}
