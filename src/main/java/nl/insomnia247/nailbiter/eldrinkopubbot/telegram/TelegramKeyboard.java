package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.Keyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Leontiev
 */
public class TelegramKeyboard extends TelegramOutputMessage implements Keyboard {
    private final static int _COLNUM = 2;
    public TelegramKeyboard(UserData ud, String msg, String[] categories) {
        super(ud);
        setText(msg);
		List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();
        for(int i = 0; i < categories.length; i++) {
			buttons.add(new ArrayList<InlineKeyboardButton>());
			for(int j = 0; j < _COLNUM && i < categories.length; j++) {
				buttons.get(buttons.size()-1).add(new InlineKeyboardButton()
						.setText(categories[i])
						.setCallbackData(Integer.toString(i)));
				i++;
			}
		}
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(buttons);
        this.setReplyMarkup(markupInline);
        /*Message res = execute(message); 
        int id = res.getMessageId();
        logger_.info(String.format("return id=%d", id));
        return id;*/
    }
}
