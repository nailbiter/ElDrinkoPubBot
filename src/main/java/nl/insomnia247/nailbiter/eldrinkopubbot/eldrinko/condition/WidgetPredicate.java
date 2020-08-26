package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.condition;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.action.ElDrinkoActionInflator;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * @author Alex Leontiev
 * FIXME: eliminate
 */
public class WidgetPredicate extends MessageKeyboardComparisonPredicate {
    private static Logger _Log = LogManager.getLogger();
    private String _type;
    public WidgetPredicate(Object o) {
        super(null);
        _type = (String)o;
    }
    @Override
    public boolean test(ElDrinkoInputMessage tim) {
        if( !super.test(tim) ) {
            return false;
        }
        int i = Integer.parseInt(tim.left.getMsg());
        _Log.info(SecureString.format("i: %d",i));
        int numProducts = ElDrinkoActionInflator.BOTTLE_TYPES.length;
        _Log.info(SecureString.format("numProducts: %d",numProducts));
        _Log.info(SecureString.format("type: %s",_type));

        if (_type.equals("finishButton")) {
            return i==4*numProducts;
        } else if (_type.equals("validButton")) {
            return i<4*numProducts && ((i%4==1) || (i%4==2));
        } else if (_type.equals("invalidButton")) {
            return (i==4*numProducts) || ((i%4!=1) && (i%4!=2));
        }

        _Log.error(SecureString.format("_type: %s",_type));
        return false;
    }
}
