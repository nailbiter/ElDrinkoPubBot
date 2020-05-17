package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * @author Alex Leontiev
 */
public class TelegramKeyboard extends TelegramOutputMessage {
    private final static int _COLNUM = 2;
    public TelegramKeyboard(UserData ud, String msg, String[] categories) {
        super(ud);
        System.err.format("TelegramKeyboard: \"%s\" [%s]\n",msg,String.join(",",Arrays.asList(categories)));
        setText(msg);
		List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();
        for(int i = 0; i < categories.length; ) {
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
    }
}
