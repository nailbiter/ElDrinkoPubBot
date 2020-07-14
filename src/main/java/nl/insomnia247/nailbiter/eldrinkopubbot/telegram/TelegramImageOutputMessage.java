package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage;
import java.net.URL;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.DownloadCache;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import java.io.File;

/**
 * @author Alex Leontiev
 */
public class TelegramImageOutputMessage extends SendPhoto implements OutputMessage {
    String _msg = null;
    URL _image = null;
    public TelegramImageOutputMessage(String msg, URL image) {
        super();
        String filePath = new DownloadCache(".png").get(image);
        this.setPhoto(new File(filePath));
        this.setCaption(msg);
        _msg = msg;
        _image = image;
    }
    @Override
    public String toString() {
        return String.format("TelegramImageOutputMessage(%s,%s)",_msg, _image.toString());
    }
}
