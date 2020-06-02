package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import java.util.Map;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.util.List;


/**
 * @author Alex Leontiev
 */
public class MiscUtils {
    private static Logger _Log = LogManager.getLogger(MiscUtils.class);
    private static final String _BEERLIST = MiscUtils.GetResource("beerlist",".txt");
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
    public static String ProcessTemplate(String templateName, Map<String, Object> additionalContext) {
        TemplateEngine _jinjava = new TemplateEngine();
        Map<String,Object> context = new HashMap<>();
        Tsv tsv = new Tsv(MiscUtils.SafeUrl(_BEERLIST));
        List<List<String>> products = tsv.getRecords();
        context.put("products",products);
        if(additionalContext!=null) {
            for(String k:additionalContext.keySet()) {
                context.put(k,additionalContext.get(k));
            }
        }
        _Log.info(String.format("_ProcessTemplate: context before rendering: %s",context));

        _Log.info(String.format("getting resource %s",templateName));
        String template = MiscUtils.GetResource(templateName);
        _Log.info(String.format("template: %s",template));
        String renderedTemplate = MiscUtils.GetResource(templateName);
        renderedTemplate = _jinjava.render(template, context);	
        _Log.info(String.format("renderedTemplate: %s",renderedTemplate));
        return renderedTemplate;
    }
}
