package nl.insomnia247.nailbiter.eldrinkopubbot.util;
import java.io.InputStream;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.Jsonable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.lang.StringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.commons.io.IOUtils;
import java.net.URL;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import com.mongodb.MongoClient;
import java.util.function.Consumer;
import org.bson.Document;
import com.mongodb.client.MongoCollection;


/**
 * @author Alex Leontiev
 */
public class Tsv implements Jsonable{
    protected Map<String,List<String>> _content = new HashMap<>();
    protected List<String> _headers = new ArrayList<>();
    protected Tsv() {}
    public Tsv(URL url) {
        _parseContent(MiscUtils.GetResource("eldrinkopubbot",".tsv")); //FIXME
    }
    public Tsv(MongoCollection<Document> collection) {
        String[] _HEADERS = new String[]{"name","description","price (UAH/L)","image link"};
        for(String s:_HEADERS) {
            _headers.add(s);
            _content.put(s,new ArrayList<String>());
        }
		Consumer<Document> printConsumer = new Consumer<Document>() {
			   @Override
			   public void accept(final Document document) {
				   //System.out.println(document.toJson());
                   JSONObject obj = new JSONObject(document.toJson());
                   for(String s:_HEADERS) {
                       _content.get(s).add(obj.get(s).toString());
                   }
			   }
		};
        collection.find().forEach(printConsumer);
    }
    private void _parseContent(String content) {
        String[] lines = content.split("\n+");
        for(int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].trim();
        }
		String[] body = IntStream.range(1,lines.length).mapToObj(i -> lines[i]).toArray(String[]::new);
        String header = lines[0];
        String[] header_tokens = header.split("\t");
        for(String h:header_tokens) {
            _content.put(h,new ArrayList<String>());
            _headers.add(h);
        }
        for(String line: body) {
            String[] tokens = line.split("\t");
            for(int i = 0; i < tokens.length; i++) {
                _content.get(header_tokens[i]).add(tokens[i]);
            }
        }
    }
    public List<String> getColumn(String name) {
        return _content.get(name);
    }
    public List<List<String>> getRecords() {
        List<List<String>> res = new ArrayList<>();
        int size = _content.get(_headers.get(0)).size();
        for(int i = 0; i < size; i++) {
            List<String> record = new ArrayList<>();
            record.add(Integer.toString(i));
            for(String h:_headers) {
                record.add(_content.get(h).get(i));
            }
            res.add(record);
        }
        return res;
    }
    public String toString() {
        return _content.toString();
    }
    @Override
    public String toJsonString() {
        JSONObject res = new JSONObject();
        for(String fn: _headers) {
            res.put(fn, new JSONArray(_content.get(fn)));
        }
        return new JSONObject().put("headers",new JSONArray(_headers)).put("content",res).toString();
    }
}
