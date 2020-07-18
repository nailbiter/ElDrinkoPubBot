package nl.insomnia247.nailbiter.eldrinkopubbot.mongodb;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.PersistentStorage;


/**
 * @author Alex Leontiev
 * assumption: database only gets modified through this object
 */
public class MongoPersistentStorage implements PersistentStorage {
    private MongoCollection<Document> _mongoCollection = null;
    private JSONObject _obj = null;
    private String _key = null;
    private String _val = null;
    public MongoPersistentStorage(MongoCollection<Document> mongoCollection, String key, String val) {
        System.err.format(" ad964b5521b11fb9 \n");
        _mongoCollection = mongoCollection;
        _key = key;
        _val = val;
        System.err.format(" 3ff43cbd930bd1f1 \n");
        _getObj();
        System.err.format(" 2e878d806817478d \n");
    }
    private void _getObj() {
        if(_obj == null) {
            System.err.format(" a2a82f031dd60d4a \n");
            Document doc = _mongoCollection.find(Filters.eq(_key,_val)).first();
            System.err.format("doc: %s\n",doc);
            if(doc == null) {
                doc = new Document(_key,_val);
                _mongoCollection.insertOne(doc);
                _obj = new JSONObject();
            } else {
                String json = doc.toJson();
                System.err.format("json: %s\n",json);
                _obj = new JSONObject(json);
            }
        }
    }
    @Override
    public boolean contains(String key) {
        return _obj.has(key);
    }
    @Override
    public String get(String key) {
        return _obj.getString(key);
    }
    @Override
    public PersistentStorage set(String key, String val) {
        _mongoCollection.updateOne(Filters.eq(_key,_val),Updates.set(key,val));
        _obj.put(key,val);
        return this;
    }
}
