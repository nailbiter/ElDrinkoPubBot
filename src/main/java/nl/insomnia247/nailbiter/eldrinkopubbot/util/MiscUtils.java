package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;


/**
 * @author Alex Leontiev
 */
public class MiscUtils {
    private static Logger _Log = LogManager.getLogger(MiscUtils.class);
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
