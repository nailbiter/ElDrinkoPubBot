package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import java.io.InputStream;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.io.IOUtils;
import java.net.URL;


/**
 * @author Alex Leontiev
 */
public class Tsv {
    private Map<String,List<String>> _content = new HashMap<>();
    public Tsv(URL url) {
        InputStream in = null;
        Cache cache = new Cache(60);
        try {
            String body = null;
            if(cache.get(url.toString())==null) {
                in = url.openStream();
                cache.put(url.toString(),IOUtils.toString( in ));
            }
            _content = _ParseContent((String)cache.get(url.toString()));
        } catch(Exception e ) {
            System.err.format("exception: %s\n",e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }
    public Tsv(String content) {
        _content = _ParseContent(content);
    }
    private static Map<String,List<String>> _ParseContent(String content) {
        //System.err.format("content: \"%s\"\n",content);
        Map<String,List<String>> contentMap = new HashMap<>();
        String[] lines = content.split("\n+");
        //System.err.format("lines: %d\n",lines.length);
        for(int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
            //System.err.format("line: %s\n",lines[i]);
        }
		String[] body = IntStream.range(1,lines.length).mapToObj(i -> lines[i]).toArray(String[]::new);
        String header = lines[0];
        String[] header_tokens = header.split("\t");
        for(String h:header_tokens) {
            contentMap.put(h,new ArrayList<String>());
            //System.err.format("header token: \"%s\"\n",h);
        }
        for(String line: body) {
            String[] tokens = line.split("\t");
            //System.err.format("tokens: %d\n",tokens.length);
            for(int i = 0; i < tokens.length; i++) {
                //System.err.format("token: %s\n",tokens[i]);
                contentMap.get(header_tokens[i]).add(tokens[i]);
            }
        }
        return contentMap;
    }
    public List<String> getColumn(String name) {
        return _content.get(name);
    }
    public String toString() {
        return _content.toString();
    }
}
