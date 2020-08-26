package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import com.hubspot.jinjava.Jinjava;
import java.util.Map;
import com.hubspot.jinjava.lib.filter.Filter;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;

/**
 * @author Alex Leontiev
 */
public class TemplateEngine {
    private Jinjava _jinjava = new Jinjava();
    private static Logger _Log = LogManager.getLogger(TemplateEngine.class);
    public TemplateEngine() {
        _jinjava.getGlobalContext().registerFilter(new Filter(){
            @Override
            public Object filter(Object vvar, JinjavaInterpreter interpreter, String... args) {
                _Log.info(vvar.getClass().getName());
                _Log.info(vvar);
                Double d = 0.0;
                if(vvar instanceof String) {
                    d = Double.parseDouble((String)vvar);
                } else if(vvar instanceof Integer) {
                    d = ((Integer)vvar).doubleValue();
                } else if(vvar instanceof Float) {
                    d = ((Float)vvar).doubleValue();
                } else if(vvar instanceof Long) {
                    d = ((Long)vvar).doubleValue();
                } else {
                    d = (double)vvar;
                }
                _Log.info(d);
                String pre = SecureString.format("%.2f",d.floatValue());
                _Log.info(pre);
                String res = pre.replace(".",",");
                _Log.info(res);
                return res;
            }
            public String getName() {
                return "myprintf";
            }
        });
        _jinjava.getGlobalContext().registerFilter(new Filter(){
            @Override
            public Object filter(Object vvar, JinjavaInterpreter interpreter, String... args) {
                _Log.info(vvar.getClass().getName());
                _Log.info(vvar);
                int d = 0;
                if(vvar instanceof String) {
                    d = Integer.parseInt((String)vvar);
                } else if(vvar instanceof Integer) {
                    d = ((Integer)vvar).intValue();
                } else if(vvar instanceof Float) {
                    d = ((Float)vvar).intValue();
                } else if(vvar instanceof Double) {
                    d = ((Double)vvar).intValue();
                } else if(vvar instanceof Long) {
                    d = ((Long)vvar).intValue();
                } else {
                    d = (int)vvar;
                }
                return SecureString.format("%02d",d);
            }
            public String getName() {
                return "myprintf_int";
            }
        });
    }
    public String render(String template, Map<String,Object> context) {
        _Log.info(template);
        _Log.info(context);
        String res = "";
        try {
            res = _jinjava.render(template,context);
        } catch(Exception e) {
            _Log.error(e);
            _Log.error(e.getMessage());
        }
        _Log.info(res);
        return res;
    }
}
