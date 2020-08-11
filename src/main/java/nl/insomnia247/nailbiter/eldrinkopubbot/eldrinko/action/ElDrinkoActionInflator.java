package nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.action;
import java.util.function.Function;
import org.bson.Document;
import com.mongodb.client.MongoCollection;
import nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko.ElDrinkoInputMessage;
import java.util.Date;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramImageOutputMessage;
import org.apache.commons.collections4.ListUtils;
import java.util.TimeZone;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboard;
import org.apache.commons.lang3.tuple.ImmutablePair;
import java.util.Map;
import java.util.List;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.Tsv;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.JSONTools;
import java.util.ArrayList;
import java.net.URL;
import java.util.HashMap;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.TemplateEngine;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import org.json.JSONObject;
import org.json.JSONArray;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.function.Consumer;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.PersistentStorage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputArrayMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.SecureString;

/**
 * @author Alex Leontiev
 */
public class ElDrinkoActionInflator implements Function<Object,Function<ElDrinkoInputMessage,ImmutablePair<OutputMessage,JSONObject>>> {
    private static Logger _Log = LogManager.getLogger(ElDrinkoActionInflator.class);
    private Consumer<Document> _orderInserter;
    private static final JSONObject _TRANSITIONS 
        = new JSONObject(MiscUtils.GetResource("transitions",".json"));
    private final PersistentStorage _masterPersistentStorage;
    private JSONObject _config;
    private static final DateFormat _ORDER_REPORT_FORMATTER = new SimpleDateFormat("dd.mm.yy HH:MM");
    static {
        _ORDER_REPORT_FORMATTER.setTimeZone(TimeZone.getTimeZone("Ukraine/Kiev"));
    }
    private final Consumer<ImmutablePair<String,String>>  _sendOrderCallback;
    public ElDrinkoActionInflator(Consumer<ImmutablePair<String,String>> sendOrderCallback, PersistentStorage masterPersistentStorage
            , Consumer<Document> orderInserter) {
        _masterPersistentStorage = masterPersistentStorage;
        _sendOrderCallback = sendOrderCallback;
        _orderInserter = orderInserter;
        if(_orderInserter == null) {
            _orderInserter = new Consumer<Document> () {
                @Override
                public void accept(Document d) {}
            };
        }
    }
    @Override
    public Function<ElDrinkoInputMessage,ImmutablePair<OutputMessage,JSONObject>> apply(Object o) {
        return new Function<ElDrinkoInputMessage, ImmutablePair<OutputMessage,JSONObject>>() {
            @Override
            public ImmutablePair<OutputMessage,JSONObject> apply(ElDrinkoInputMessage im) {
                _Log.info(SecureString.format("here with %s,%s",o,im));
                if( (((JSONObject)o).getString("correspondence")).equals("f02480c016715289") ) {
                    TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im.left;
                    int i = Integer.parseInt(tka.getMsg());
                    Tsv tsv = im.beerlist;
                    JSONObject order = im.right.optJSONObject("order");
                    if(order==null) {
                        order = new JSONObject();
                        order.put("cart",new JSONArray());
                    }
                    JSONObject obj = new JSONObject();
                    String name = tsv.getColumn("name").get(i);
                    obj.put("name",name);
                    order.getJSONArray("cart").put(obj);
                    im.right.put("order",order);
                } else if( ((JSONObject)o).getString("correspondence").equals("5e11c9696e9b38f0") ) {
                    if(((JSONObject)o).optString("src_state").equals("delete")) {
                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im.left;
                        int i = Integer.parseInt(tka.getMsg());
                        JSONObject order = im.right.optJSONObject("order");
                        JSONArray cart = order.getJSONArray("cart");
                        JSONObject removed = cart.getJSONObject(i);
                        cart.remove(i);
                    } else {
                        TelegramTextInputMessage ttim = (TelegramTextInputMessage) im.left;
                        float amount = 0;
                        try {
                            amount = MiscUtils.ParseFloat(ttim.getMsg());
                        } catch(Exception e) {}
                        JSONObject order = im.right.optJSONObject("order");
                        JSONArray cart = order.getJSONArray("cart");
                        JSONObject obj = cart.getJSONObject(cart.length()-1);
                        obj.put("amount",amount);
                        im.right.put("order",order);
                    }
                } else if( ((JSONObject)o).getString("correspondence").equals("72e97b89bcab08c4") ) {
                    im.right.put("address",im.left.getMsg());
                } else if( ((JSONObject)o).getString("correspondence").equals("8e0edde4a3199d0c") ) {
                    im.right.put("phone_number",im.left.getMsg());
                } else if( ((JSONObject)o).getString("correspondence").equals("fa702a44b70ddcae") ) {
                    if(((JSONObject)o).optString("src_state").equals("edit_address")) {
                        im.right.put("address",im.left.getMsg());
                    } else {
                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im.left;
                        int i = Integer.parseInt(tka.getMsg());
                        String paymentMethods 
                            = MiscUtils.ProcessTemplate("4ea9a63509e8ed5826a37f8a",null,im.beerlist);
                        im.right.put("payment", paymentMethods.split("\n")[i]);
                    }
                } else if( ((JSONObject)o).getString("correspondence").equals("e22069e5c338fa6c") ) {
                    _Log.info(SecureString.format("7ba3f409e638d9313fbb0e9a o: %s",o));
                    if(((JSONObject)o).optString("src_state").equals("widget")) {
                        String type = ((JSONObject)o).optString("type");
                        _Log.info(SecureString.format("type: %s",type));
                        if( type.equals("validButton") ) {
                            int _i = Integer.parseInt(im.left.getMsg());
                            _Log.info(SecureString.format("_i: %d",_i));
                            int idx = _i/4;
                            _Log.info(SecureString.format("idx: %d",idx));
                            boolean shouldAdd = _i%4==1;
                            _Log.info(SecureString.format("shouldAdd: %s",shouldAdd));
                            List<String> r = im.beerlist.getRecords().get(idx);
                            _Log.info(SecureString.format("r: %s",r));
                            String name = r.get(1);
                            _Log.info(SecureString.format("name: %s",name));

                            JSONObject order = im.right.optJSONObject("order");
                            _Log.info(SecureString.format("order: %s",order));
                            if(order==null) {
                                order = new JSONObject();
                                order.put("cart",new JSONArray());
                            }
                            _Log.info(SecureString.format("order: %s",order));

                            JSONArray cart = order.getJSONArray("cart");
                            boolean didFind = false;
                            for(int i = 0; i < cart.length(); i++) {
                                JSONObject obj = cart.getJSONObject(i);
                                if(obj.getString("name").equals(name)) {
                                    obj.put("amount",(float)(obj.getDouble("amount") + (shouldAdd ? 0.5 : -0.5)));
                                    didFind = true;
                                    break;
                                }
                            }
                            if( !didFind && shouldAdd ) {
                                JSONObject obj = new JSONObject();
                                obj.put("name",name).put("amount",0.5f);
                                cart.put(obj);
                            }
                            for(int i = 0; i < cart.length(); i++) {
                                JSONObject obj = cart.getJSONObject(i);
                                if(obj.getDouble("amount")==0.0) {
                                    cart.remove(i);
                                    i--;
                                }
                            }

                            im.right.put("order",order);
                        }
                    }
                } else if( ((JSONObject)o).getString("correspondence").equals("48c6907046b03db8") ) {
                    JSONObject order = _GetOrder(im.right);
                    order.put("uid",im.userData.getUserName());
                    order.put("count",_IncrementOrderCount(_masterPersistentStorage));
                    Date d = new Date();
                    order.put("timestamp", _ORDER_REPORT_FORMATTER.format(d));
                    order.put("_timestamp", d);
                    Map<String,Object> map = _OrderObjectToJinjaContext(order,im.beerlist);
                    _orderInserter.accept(new Document(map));
                    _sendOrderCallback.accept(new ImmutablePair<String,String>(
                            MiscUtils.ProcessTemplate("3804e512b18b339fe8786dbd",map,im.beerlist)
                                ,"salesmanChatIds"));
                    _sendOrderCallback.accept(new ImmutablePair<String,String>(
                            MiscUtils.ProcessTemplate("3804e512b18b339fe8786dbd",map,im.beerlist)
                            , im.userData.getChatId().toString()
                                ));
                    im.right.put("order","");
                } else if( ((JSONObject)o).getString("correspondence").equals("75f676187e00dd85") && ((JSONObject)o).optString("src_state").equals("null") ) {
                    im.right.put("order","");
                }

                _Log.info(SecureString.format("before _GetOrder(%s)",im.right));
                JSONObject ppo = _GetOrder(im.right);
                _Log.info(SecureString.format("_OrderObjectToJinjaContext(%s,%s)",ppo,im.beerlist.toJsonString()));
                Map<String,Object> map = _OrderObjectToJinjaContext(ppo,im.beerlist);
                Object oo = null;
                _Log.info(SecureString.format("after _GetOrder(%s)",im.right));

                if( (((JSONObject)o).getString("correspondence")).equals("72aa7197071b6503") ) {
                    TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im.left;
                    Tsv tsv = im.beerlist;
                    String imgUrl 
                        = tsv.getColumn("image link").get(Integer.parseInt(tka.getMsg()));
                    map.put("i",Integer.parseInt(tka.getMsg()));
                    oo = MiscUtils.SafeUrl(imgUrl);
                } else if( ((JSONObject)o).getString("correspondence").equals("e22069e5c338fa6c") ) {
                    try {
                        Map<String,Object> beerVolumes = new HashMap<>();
                        Tsv tsv = im.beerlist;

                        float totalVolume = 0.0f, totalPrice = 0.0f;
                        _Log.info("a28b2cf6997f67b9d64e3773");

                        for(int i = 0; i < tsv.getColumn("name").size(); i++) {
                            String cn = tsv.getColumn("name").get(i);
                            Map<String,Object> r = new HashMap<>();
                            r.put("price",0.0f);
                            r.put("amount",0.0f);
                            r.put("_price",Float.parseFloat(tsv.getColumn("price (UAH/L)").get(i)));
                            beerVolumes.put(cn,r);
                        }
                        _Log.info(SecureString.format("beerVolumes: %s",beerVolumes));
                        JSONObject order = im.right.optJSONObject("order");
                        if(order==null) {
                            order = new JSONObject();
                            order.put("cart",new JSONArray());
                        }
                        for(int i = 0; i < order.getJSONArray("cart").length(); i++) {
                            JSONObject o = order.getJSONArray("cart").getJSONObject(i);
                            Map<String,Object> r = (Map<String,Object>)beerVolumes.get(o.getString("name"));
                            r.put("amount",(float)(o.getDouble("amount")));
                            r.put("price",(float)(o.getDouble("amount")*(float)r.get("_price")));
                        }
                        for(String cn : beerVolumes.keySet()) {
                            Map<String,Object> r = (Map<String,Object>) beerVolumes.get(cn);
                            totalPrice += (float)r.get("price");
                            totalVolume += (float)r.get("amount");
                        }

                        map.put("beerVolumes",beerVolumes);
                        map.put("totalVolume",totalVolume);
                        map.put("totalPrice",totalPrice);
                        _Log.info(SecureString.format("map: %s",map));
                    } catch(Exception e) {
                        _Log.error(e.getMessage(), e);
                    }
                }

                im.right.put("username",im.userData.getUserName());
                _Log.info(SecureString.format("before _InflateOutputMessage(%s,%s,%s,%s),%s",((JSONObject)o).getString("correspondence"),map,oo,im.beerlist.toJsonString(),im.right));
                OutputMessage om = _InflateOutputMessage(((JSONObject)o).getString("correspondence"),map,oo,im.beerlist);
                _Log.info(SecureString.format("om: %s",om));
                return new ImmutablePair<OutputMessage,JSONObject>(om,im.right);
            }
        };
    }
    private static OutputMessage _InflateOutputMessage(String code, Map<String,Object> env, Object obj, Tsv tsv) {
        Object m = _TRANSITIONS.getJSONObject("transitions").get(code);
        _Log.info(SecureString.format("m: %s",m));
        return _InflateOutputMessageFromJson(m,env,obj,tsv);
    }
    private static OutputMessage _InflateOutputMessageFromJson(Object m, Map<String,Object> env, Object obj,Tsv tsv) {
        assert m instanceof JSONObject || m instanceof JSONArray;
        _Log.info(m);
        if(m instanceof JSONArray) {
            List<OutputMessage> msgs = new ArrayList<>();

            _Log.info(SecureString.format(" %s ",m));
            for(int i = 0; i < ((JSONArray)m).length();i++) {
                msgs.add(_InflateOutputMessageFromJson(((JSONArray)m).get(i),env,obj,tsv));
            }
            return new OutputArrayMessage(msgs.toArray(OutputMessage[]::new));
        } else {
            JSONObject msg = (JSONObject)m;
            String tag = msg.getString("tag");
            if(tag.equals("TelegramTextOutputMessage")) {
                return new TelegramTextOutputMessage(MiscUtils.ProcessTemplate(msg.getString("message"), env,tsv));
            } else if(tag.equals("TelegramKeyboard")) {
                return _InflateTelegramKeyboard(
                        env, msg.getString("message"), msg.getString("keyboard"),tsv,msg.optInt("columns",2));
            } else if(tag.equals("TelegramImageOutputMessage")) {
                return new TelegramImageOutputMessage(
                        MiscUtils.ProcessTemplate(msg.getString("message"), env,tsv)
                        , (URL)obj);
            } else {
                return null;
            }
        }
    }
    private static Map<String,Object> _OrderObjectToJinjaContext(JSONObject order, Tsv tsv) {
        Map<String, Object> context = new HashMap<String,Object>();
        _Log.info(order);
        if(order==null) {
            return context;
        }
        List<List<String>> products = tsv.getRecords();
        Map<String,Object> orderMap = JSONTools.JSONObjectToMap(order);
        //FIXME: try to compute `sum` in templates
        float sum = 0;
        JSONArray cart = order.getJSONArray("cart");
        for(int i = 0; i < cart.length(); i++) {
            JSONObject obj = cart.getJSONObject(i);
            if(!obj.has("amount")) {
                continue;
            }
            float beerPrice = Float.parseFloat(products.stream()
                    .filter(r -> r.get(1).equals(obj.getString("name")))
                    .findAny()
                    .orElse(null)
                    .get(3));
            sum += beerPrice * obj.getDouble("amount");
        }
        orderMap.put("sum",sum);
        orderMap.put("delivery_fee",sum>=250 ? (double)0.0 : (double)20.0);
        _Log.info(orderMap.toString());
        context.put("order",orderMap);
        _Log.info(SecureString.format("context: %s",context));
        return context;
    }
    private static TelegramKeyboard _InflateTelegramKeyboard(Map<String,Object> env,String msgTemplateResName, String keysTemplateResName,Tsv tsv,int columns) {
        String keyboardKeys = MiscUtils.ProcessTemplate(keysTemplateResName,env,tsv);
        String keyboardMsg = MiscUtils.ProcessTemplate(msgTemplateResName,env,tsv);
        _Log.info(SecureString.format("keyboardMsg: %s",keyboardMsg));
        _Log.info(SecureString.format("keyboardKeys: %s",keyboardKeys));
        return new TelegramKeyboard(keyboardMsg,keyboardKeys.split("\n"),columns);
    }
    private static int _IncrementOrderCount(PersistentStorage masterPersistentStorage) {
        final String FN = "order_count";
        int res = 0;
        if( masterPersistentStorage.contains(FN) ) {
            res = Integer.parseInt(masterPersistentStorage.get(FN));
        }
        masterPersistentStorage.set(FN,Integer.toString(res+1));
        return res+1;
    }
    private static JSONObject _GetOrder(JSONObject obj) {
        JSONObject order = obj.optJSONObject("order");
        if(order!=null) {
            order = new JSONObject(order.toString());
            final String[] KEYS = new String[] {"phone_number","address","payment"};
            for(String k: KEYS) {
                if(obj.has(k)) {
                    order.put(k,obj.getString(k));
                }
            }
        }
        return order;
    }
}
