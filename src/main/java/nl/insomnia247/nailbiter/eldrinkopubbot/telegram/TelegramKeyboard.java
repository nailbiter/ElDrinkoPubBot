package nl.insomnia247.nailbiter.eldrinkopubbot.telegram;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * @author Alex Leontiev
 */
public class TelegramKeyboard extends TelegramOutputMessage {
    String _msg;
    List<String> _categories;
    private int _columns;
    public TelegramKeyboard(String msg, String[] categories, int columns) {
        super();
        _msg = msg;
        _columns = columns;
        _categories = Arrays.asList(categories);
        setText(msg);
		List<List<InlineKeyboardButton>> buttons = new ArrayList<List<InlineKeyboardButton>>();
        for(int i = 0; i < categories.length; ) {
			buttons.add(new ArrayList<InlineKeyboardButton>());
			for(int j = 0; j < columns && i < categories.length; j++) {
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
    @Override
    public String toString() {
        String res = super.toString();
        return String.format("%s(%s,%s)",this.getClass().getSimpleName(),_msg,_categories);
    }
    @Override
    public String toJsonString() {
        return new JSONObject()
            .put("tag",getClass().getSimpleName())
            .put("value", new JSONObject().put("msg",_msg).put("categories",new JSONArray(Arrays.asList(_categories))))
            .toString();
    }
}
