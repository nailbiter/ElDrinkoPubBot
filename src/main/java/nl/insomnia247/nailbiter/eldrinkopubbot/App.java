package nl.insomnia247.nailbiter.eldrinkopubbot;
import java.io.File;
import java.io.FileWriter;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoPubBot;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App {
    private static Logger _Log = LogManager.getLogger();
    public static void main( String[] args ) throws Exception {
        File file = new File("./secret.txt");
        String content = FileUtils.readFileToString(file, "utf-8").trim();

        String commit_data = System.getenv().get("COMMIT_DATA")
            , bot_name = System.getenv().getOrDefault("BOT_NAME", "ElDrinkoPubBot");

        String fn = String.format(".tmp/%s.runs.txt",bot_name);
        File f = new File(fn);
        _Log.info(SecureString.format("file flag: %s",fn));
        if(f.exists()) {
            throw new Exception(String.format("only one instance of \"%s\" allowed to run!",bot_name));
        } else {
            FileWriter w = new FileWriter(f);
            String file_content = String.format("%d",ProcessHandle.current().pid());
            _Log.info(SecureString.format("save \"%s\" to \"%s\"",file_content,fn));
            w.write(file_content);
            w.flush();
        }

        _Log.info(SecureString.format("COMMIT_DATA: %s",commit_data));
        _Log.info(SecureString.format("BOT_NAME: %s",bot_name));

		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        ElDrinkoPubBot edpb = new ElDrinkoPubBot(content, commit_data, bot_name);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println(String.format("removing \"%s\"",fn));
                System.out.println("bye-bye!");
                new File(fn).delete();
            }
        });

		telegramBotsApi.registerBot(edpb);
    }
}
