package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * @author Alex Leontiev
 */
public class MiscUtils {
    private static Logger _Log = LogManager.getLogger(MiscUtils.class);
    private static final Pattern PARSE_FLOAT_PATTERN 
        = Pattern.compile("(?<sign>-)?(?<intpart>\\d+)((.|,)(?<fracpart>\\d+))?");
    public static class ParseFloatException extends Exception {
        public ParseFloatException(String m) { super(m); }
    }
    public static float ParseFloat(String s) throws ParseFloatException {
        Matcher m = PARSE_FLOAT_PATTERN.matcher(s);
        if( !m.matches() ) {
            throw new ParseFloatException(s);
        }
        return Float.parseFloat((m.group("sign")==null?"":m.group("sign")) + m.group("intpart")
            + (m.group("fracpart")==null?"":"."+m.group("fracpart")));
    }
    public static boolean IsFloatInteger(float f) {
        return f==Math.floor(f);
    }
    public static URL SafeUrl(String u) {
        URL url = null;
        try {
            url = new URL(u);
        } catch (Exception e) {
            _Log.info(String.format("malformed url: \"%s\"\n",u));
        }
        return url;
    }
    public static String GetResource(String templateName) {
        return GetResource(templateName,".txt");
    }
    public static String GetResource(String templateName, String ext) {
        _Log.info(String.format("MiscUtils.GetResource(%s,%s)",templateName,ext));
        String template = null;
        try {
            InputStream in 
                = MiscUtils.class.getClassLoader().getResource(templateName+ext).openStream();
            template = IOUtils.toString( in );
        } catch(Exception e) {
            _Log.info(" 60a93278bbe5f78d \n");
        }
        _Log.info(String.format("res: %s",template));
        return template;
    }
}
