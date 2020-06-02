package nl.insomnia247.nailbiter.eldrinkopubbot;
import org.apache.commons.collections4.ListUtils;
import java.util.Set;
import java.util.HashSet;
import com.mongodb.MongoClient;
import java.net.URL;
import com.mongodb.client.MongoCollection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apache.commons.lang3.tuple.ImmutablePair;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputArrayMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.mongodb.PersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.StateMachine;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.TemplateEngine;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramImageOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboard;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.JSONTools;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.Tsv;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Alex Leontiev
 */
public class ElDrinkoStateMachine extends StateMachine<TelegramInputMessage,OutputMessage> {
    private static final String _BEERLIST = "https://docs.google.com/spreadsheets/d/e/2PACX-1vRGSUiAeapo7eHNfA1v9ov_Cc2oCjWNsmcpadN6crtxJ236uDOKt_C_cR1hsXCyqZucp_lQoeRHlu0k/pub?gid=0&single=true&output=tsv";
    private final UserData _ud;
    private final PersistentStorage _persistentStorage;
    private final PersistentStorage _masterPersistentStorage;
    private final Consumer<ImmutablePair<String,String>>  _sendOrderCallback;
    private static Logger _Log = LogManager.getLogger(ElDrinkoStateMachine.class);
    private static final DateFormat _ORDER_REPORT_FORMATTER = new SimpleDateFormat("dd.mm.yy HH:MM");
    static {
        _ORDER_REPORT_FORMATTER.setTimeZone(TimeZone.getTimeZone("Ukraine/Kiev"));
    }
    private static final JSONObject _TRANSITIONS 
        = new JSONObject(MiscUtils.GetResource("transitions",".json"));
    public ElDrinkoStateMachine(UserData ud, MongoClient mongoClient, Consumer<ImmutablePair<String,String>> sendOrderCallback, JSONObject config, PersistentStorage masterPersistentStorage) {
        super("_");
        _ud = ud;
        _masterPersistentStorage = masterPersistentStorage;
        _sendOrderCallback = sendOrderCallback;
        _persistentStorage = new PersistentStorage(
                mongoClient.getDatabase("beerbot").getCollection(config.getJSONObject("mongodb").getString("data")), 
                "id",
                ud.toString()
                );
    }
    private static final Predicate<TelegramInputMessage> _TRIVIAL_PREDICATE
        = new Predicate<TelegramInputMessage>(){
            @Override
            public boolean test(TelegramInputMessage im) {
                return true;
            }
        };
    private static Map<String,Object> _OrderObjectToJinjaContext(JSONObject order) {
        Map<String, Object> context = new HashMap<String,Object>();
        Tsv tsv = new Tsv(MiscUtils.SafeUrl(_BEERLIST));
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
        _Log.info(String.format("context: %s",context));
        return context;
    }
    private static String _ProcessTemplate(String templateName, Map<String, Object> additionalContext) {
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
    private static Predicate<TelegramInputMessage> _MessageComparisonPredicate(String msg) {
        return new Predicate<TelegramInputMessage>(){
            @Override
            public boolean test(TelegramInputMessage im) {
                return im.getMsg().equals(msg);
            }
        };
    }
    private final Predicate<TelegramInputMessage> _IS_TEXT_MESSAGE = new Predicate<>() {
        @Override
        public boolean test(TelegramInputMessage im) {
            return im instanceof TelegramTextInputMessage;
        }
    };
    private static Predicate<TelegramInputMessage> _MessageKeyboardComparisonPredicate(String msg) {
        return new Predicate<TelegramInputMessage>(){
            @Override
            public boolean test(TelegramInputMessage im) {
                return (im instanceof KeyboardAnswer) && (msg==null || im.getMsg().equals(msg));
            }
        };
    }
    private static TelegramKeyboard _InflateTelegramKeyboard(UserData ud, Map<String,Object> env,String msgTemplateResName, String keysTemplateResName) {
        String keyboardKeys = _ProcessTemplate(keysTemplateResName,env);
        String keyboardMsg = _ProcessTemplate(msgTemplateResName,env);
        _Log.info(String.format("keyboardMsg: %s",keyboardMsg));
        _Log.info(String.format("keyboardKeys: %s",keyboardKeys));
        return new TelegramKeyboard(ud,keyboardMsg,keyboardKeys.split("\n"));
    }
    private static int _CorrespondenceSearch(String startState,String endState) {
        return ListUtils.indexOf(
                JSONTools.JSONArrayToList(_TRANSITIONS.getJSONArray("correspondence")),
                new org.apache.commons.collections4.Predicate<Object>() {
                    @Override
                    public boolean evaluate(Object o) {
                        List<Object> l = (List<Object>)o;
                        return l.get(0).equals(startState) && l.get(1).equals(endState);
                    }
                });
    }
    private static OutputMessage _InflateOutputMessage(String startState, String endState, 
            UserData ud,Map<String,Object> env, Object obj) {
        _Log.info(String.format("start _InflateOutputMessageFromJson(%s,%s,%s,%s,%s)",
                    startState,endState,ud,env,obj
                    ));
        JSONArray correspondence = _TRANSITIONS.getJSONArray("correspondence");
        int i = _CorrespondenceSearch(startState,endState);
        assert i>0;
        String code = correspondence.getJSONArray(i).getString(3);
        _Log.info(code);
        return _InflateOutputMessageFromJson(
                _TRANSITIONS.getJSONObject("transitions").get(code),ud,env,obj);
    }
    private static OutputMessage _InflateOutputMessageFromJson(Object m, UserData ud, Map<String,Object>env, Object obj) {
        assert m instanceof JSONObject || m instanceof JSONArray;
        _Log.info(m);
        if(m instanceof JSONArray) {
            List<OutputMessage> msgs = new ArrayList<>();

            _Log.info(String.format(" %s ",m));
            for(int i = 0; i < ((JSONArray)m).length();i++) {
                msgs.add(_InflateOutputMessageFromJson(((JSONArray)m).get(i),ud,env,obj));
            }
            return new OutputArrayMessage(msgs.toArray(OutputMessage[]::new));
        } else {
            JSONObject msg = (JSONObject)m;
            String tag = msg.getString("tag");
            if(tag.equals("TelegramTextOutputMessage")) {
                return new TelegramTextOutputMessage(ud,_ProcessTemplate(msg.getString("message"),env));
            } else if(tag.equals("TelegramKeyboard")) {
                return _InflateTelegramKeyboard(
                        ud,env,msg.getString("message"),msg.getString("keyboard"));
            } else if(tag.equals("TelegramImageOutputMessage")) {
                return new TelegramImageOutputMessage(ud,
                        _ProcessTemplate(msg.getString("message"),env)
                        ,(URL)obj);
            } else {
                return null;
            }
        }
    }
    private final Set<ImmutablePair<String,String>> _inflatedTransitions = new HashSet<>();
    private ElDrinkoStateMachine _addTransition(String start,String end,
            Predicate<TelegramInputMessage> pred,
            Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>> f) {
        _inflatedTransitions.add(new ImmutablePair<String,String>(start,end));
        if(pred==null) {
            int idx = _CorrespondenceSearch(start,end);
            JSONArray correspondence = _TRANSITIONS.getJSONArray("correspondence");
            JSONObject obj = correspondence.getJSONArray(idx).getJSONObject(2);
            if(obj.getString("tag").equals("MessageComparisonPredicate")) {
                pred = _MessageComparisonPredicate(obj.getString("value"));
            } else if(obj.getString("tag").equals("MessageKeyboardComparisonPredicate")) {
                pred = _MessageKeyboardComparisonPredicate(obj.getString("value"));
            } else {
                pred = null;
            }
        }
        assert pred != null;

        Function<TelegramInputMessage,OutputMessage> t = new Function<>(){
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        ImmutablePair<Map<String,Object>,Object> pair = f.apply(im);
                        return _InflateOutputMessage(start,end,_ud,pair.left,pair.right);
                    }
        };

        StateMachine res = this;
        if(start!=null && end!=null) {
            res = this.addTransition(start,end,pred, t);
        } else if (start==null && end!=null) {
            for(String _start:this._states) {
                res = this.addTransition(_start,end,pred,t);
            }
        } else {
            assert false;
        }
        return (ElDrinkoStateMachine) res;
    }
    private ElDrinkoStateMachine _finalize() {
        JSONArray correspondence = _TRANSITIONS.getJSONArray("correspondence");
        for(int i = 0; i < correspondence.length(); i++) {
            JSONArray t = correspondence.getJSONArray(i);
            assert(_inflatedTransitions.contains(
                        new ImmutablePair<String,String>(t.getString(0),t.getString(1))));
        }
        return this;
    }
    private static Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>> _NM
        = new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>(){
            @Override
            public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                return new ImmutablePair <Map<String,Object>,Object>(null,null);
            }
        };
    public ElDrinkoStateMachine setUp() {
        ElDrinkoStateMachine res = (ElDrinkoStateMachine) this
            ._addTransition("_", "start", _TRIVIAL_PREDICATE, _NM)
            ._addTransition("start", "choose_product_to_see_description", 
                    _MessageKeyboardComparisonPredicate("1"), _NM)
            ._addTransition("choose_product_to_see_description", "start", _MessageKeyboardComparisonPredicate(null), 
                    new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                        @Override
                        public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                            TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                            Tsv tsv = new Tsv(MiscUtils.SafeUrl(_BEERLIST));
                            String imgUrl 
                                = tsv.getColumn("image link").get(Integer.parseInt(tka.getMsg()));
                            Map<String,Object> map = new HashMap<>();
                            map.put("i",Integer.parseInt(tka.getMsg()));
                            _Log.info(String.format("imgUrl: %s",imgUrl));
                            return new ImmutablePair<Map<String,Object>,Object>(map,
                                    MiscUtils.SafeUrl(imgUrl));
                        }
                    })
            ._addTransition("start","choose_product_to_make_order",
                    _MessageKeyboardComparisonPredicate("0"),_NM)
            ._addTransition("choose_product_to_make_order","choose_amount",_MessageKeyboardComparisonPredicate(null),
                    new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                        @Override
                        public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                            TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                            int i = Integer.parseInt(tka.getMsg());
                            Tsv tsv = new Tsv(MiscUtils.SafeUrl(_BEERLIST));
                            JSONObject order = null;
                            if(_persistentStorage.contains("order") && _persistentStorage.get("order").length()>0) {
                                order = new JSONObject(_persistentStorage.get("order"));
                            } else {
                                order = new JSONObject();
                                order.put("cart",new JSONArray());
                            }
                            JSONObject obj = new JSONObject();
                            String name = tsv.getColumn("name").get(i);
                            obj.put("name",name);
                            order.getJSONArray("cart").put(obj);
                            _persistentStorage.set("order",order.toString());
                            return new ImmutablePair<Map<String,Object>,Object>(
                                    _OrderObjectToJinjaContext(order),null);
                        }
                    })
        ._addTransition("choose_amount","confirm",
                new Predicate<TelegramInputMessage>() {
                    @Override
                    public boolean test(TelegramInputMessage tim) {
                        if( !(tim instanceof TelegramTextInputMessage) ) {
                            return false;
                        }
                        TelegramTextInputMessage ttim = (TelegramTextInputMessage) tim;
                        float amount = 0;
                        try {
                            amount = MiscUtils.ParseFloat(ttim.getMsg());
                        } catch(Exception e) {
                            return false;
                        }
                        if( !MiscUtils.IsFloatInteger(2*amount) ) {
                            return false;
                        }
                        return true;
                    }
                },
                new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                    @Override
                    public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                        TelegramTextInputMessage ttim = (TelegramTextInputMessage) im;
                        float amount = 0;
                        try {
                            amount = MiscUtils.ParseFloat(ttim.getMsg());
                        } catch(Exception e) {}
                        JSONObject order = new JSONObject(_persistentStorage.get("order"));
                        JSONArray cart = order.getJSONArray("cart");
                        JSONObject obj = cart.getJSONObject(cart.length()-1);
                        obj.put("amount",amount);
                        _persistentStorage.set("order",order.toString());
                        return new ImmutablePair<Map<String,Object>,Object>(
                                _OrderObjectToJinjaContext(order),null);
                    }
                })
            ._addTransition("confirm","choose_product_to_make_order",null,_NM)
            ._addTransition("confirm","delete",null,
                    new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                        @Override
                        public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                            JSONObject order = new JSONObject(_persistentStorage.get("order"));
                            _Log.info(String.format("order: %s\n",order));
                            return new ImmutablePair<Map<String,Object>,Object>(
                                    _OrderObjectToJinjaContext(order),null);
                        }
                    })
        ._addTransition("delete","confirm",_MessageKeyboardComparisonPredicate(null),
                new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                    @Override
                    public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                        int i = Integer.parseInt(tka.getMsg());
                        JSONObject order = new JSONObject(_persistentStorage.get("order"));
                        JSONArray cart = order.getJSONArray("cart");
                        JSONObject removed = cart.getJSONObject(i);
                        cart.remove(i);
                        _persistentStorage.set("order",order.toString());
                        Map<String,Object> orderMap = _OrderObjectToJinjaContext(order);
                        orderMap.put("removed",JSONTools.JSONObjectToMap(removed));
                        return new ImmutablePair<Map<String,Object>,Object>(orderMap,null);
                    }
                })
        ._addTransition("confirm","choose_address",null,_NM)
        ._addTransition("choose_address","choose_payment",_IS_TEXT_MESSAGE,
                new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                    @Override
                    public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                        _persistentStorage.set("address",im.getMsg());
                        return new ImmutablePair<Map<String,Object>,Object>(null,null);
                    }
        }        )
        ._addTransition("choose_payment","send",_MessageKeyboardComparisonPredicate(null),
                new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                    @Override
                    public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                        _Log.info(" b7aec7c9b5d9a3b2 \n");
                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                        _Log.info(tka);
                        int i = Integer.parseInt(tka.getMsg());
                        String paymentMethods 
                            = _ProcessTemplate("4ea9a63509e8ed5826a37f8a",null);
                        _persistentStorage.set("payment", paymentMethods.split("\n")[i]);
                        JSONObject order = _GetOrder(_persistentStorage);
                        Map<String,Object> orderMap = _OrderObjectToJinjaContext(order);
                        return new ImmutablePair<Map<String,Object>,Object>(orderMap,null);
                    }
                })
        ._addTransition("send","edit_address",null,_NM)
        ._addTransition("edit_address","send",_IS_TEXT_MESSAGE,
                new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                    @Override
                    public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                        _persistentStorage.set("address",im.getMsg());
                        return new ImmutablePair<Map<String,Object>,Object>(
                                _OrderObjectToJinjaContext(_GetOrder(_persistentStorage)),null);
                    }
                })
        ._addTransition("send","choose_payment",_MessageComparisonPredicate("1"),
                new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                    @Override
                    public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                        _persistentStorage.set("address",im.getMsg());
                        return new ImmutablePair<Map<String,Object>,Object>(null,null);
                    }
                })
        ._addTransition("send","idle",null,
                new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>() {
                    @Override
                    public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im) {
                        JSONObject order = _GetOrder(_persistentStorage);
                        order.put("uid",_ud.getUserName());
                        order.put("count",_IncrementOrderCount(_masterPersistentStorage));
                        order.put("timestamp", _ORDER_REPORT_FORMATTER.format(new Date()));
                        _sendOrderCallback.accept(new ImmutablePair<String,String>(
                                _ProcessTemplate("3804e512b18b339fe8786dbd",_OrderObjectToJinjaContext(order))
                                    ,"salesmanChatIds"));
                        _persistentStorage.set("order","");
                        return new ImmutablePair<Map<String,Object>,Object>(null,null);
                }
            })
        ._addTransition("idle","start",null,_NM)
        ._addTransition(null,"start",null, 
                new Function<TelegramInputMessage,ImmutablePair<Map<String,Object>,Object>>(){
                    @Override
                    public ImmutablePair<Map<String,Object>,Object> apply(TelegramInputMessage im){
                        _persistentStorage.set("order","");
                        return new ImmutablePair<Map<String,Object>,Object>(null,null);
                    }
                }) //606c386ce22ad7d0
        ._finalize()    
        ;
        if(_persistentStorage.contains("state")) {
            _Log.info(String.format("setting _currentState to \"%s\"\n",_persistentStorage.get("state")));
            this._currentState = _persistentStorage.get("state");
        }
        return res;
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
    private static JSONObject _GetOrder(PersistentStorage persistentStorage) {
        JSONObject order = new JSONObject(persistentStorage.get("order"));
        if(persistentStorage.contains("address")) {
            order.put("address",persistentStorage.get("address"));
        }
        if(persistentStorage.contains("payment")) {
            order.put("payment",persistentStorage.get("payment"));
        }
        return order;
    }
    @Override
    protected void _onSetStateCallback(String state) {
        _Log.info(String.format("state: %s\n",state));
        _persistentStorage.set("state",state);
    }
    @Override
    protected void _didNotFoundSuitableTransition(TelegramInputMessage im) {
        _sendOrderCallback.accept(new ImmutablePair<String,String>(
                    String.format("cannot found suitable transition \"%s\" \"%s\"",_currentState,im),
                    "developerChatIds"
                    ));
    }
}
