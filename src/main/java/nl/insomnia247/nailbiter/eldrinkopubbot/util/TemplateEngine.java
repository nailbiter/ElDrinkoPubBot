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
            public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
                _Log.info(var.getClass().getName());
                _Log.info(var);
                Double d = 0.0;
                if(var instanceof String) {
                    d = Double.parseDouble((String)var);
                } else if(var instanceof Integer) {
                    d = ((Integer)var).doubleValue();
                } else if(var instanceof Float) {
                    d = ((Float)var).doubleValue();
                } else if(var instanceof Long) {
                    d = ((Long)var).doubleValue();
                } else {
                    d = (double)var;
                }
                return SecureString.format("%.2f",d).replace(".",",");
            }
            public String getName() {
                return "myprintf";
            }
        });
        _jinjava.getGlobalContext().registerFilter(new Filter(){
            @Override
            public Object filter(Object var, JinjavaInterpreter interpreter, String... args) {
                _Log.info(var.getClass().getName());
                _Log.info(var);
                int d = 0;
                if(var instanceof String) {
                    d = Integer.parseInt((String)var);
                } else if(var instanceof Integer) {
                    d = ((Integer)var).intValue();
                } else if(var instanceof Float) {
                    d = ((Float)var).intValue();
                } else if(var instanceof Double) {
                    d = ((Double)var).intValue();
                } else if(var instanceof Long) {
                    d = ((Long)var).intValue();
                } else {
                    d = (int)var;
                }
                return SecureString.format("%02d",d);
            }
            public String getName() {
                return "myprintf_int";
            }
        });
    }
    public String render(String template, Map<String,Object> context) {
        return _jinjava.render(template,context);
    }
}
