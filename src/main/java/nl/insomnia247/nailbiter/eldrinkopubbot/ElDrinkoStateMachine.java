package nl.insomnia247.nailbiter.eldrinkopubbot;
import com.hubspot.jinjava.Jinjava;
import com.mongodb.MongoClient;
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
import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputArrayMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.mongodb.PersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.StateMachine;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.MiscUtils;
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
    private final Consumer<String>  _sendOrderCallback;
    private static Logger _Log = LogManager.getLogger(ElDrinkoStateMachine.class);
    private static MongoCollection<Document> _LogDb = null;
    private final Predicate<TelegramInputMessage> _IS_TEXT_MESSAGE = new Predicate<>() {
        @Override
        public boolean test(TelegramInputMessage im) {
            return im instanceof TelegramTextInputMessage;
        }
    };
    public ElDrinkoStateMachine(UserData ud, MongoClient mongoClient, Consumer<String> sendOrderCallback, JSONObject config, PersistentStorage masterPersistentStorage) {
        super("_");
        _ud = ud;
        _masterPersistentStorage = masterPersistentStorage;
        _sendOrderCallback = sendOrderCallback;
        if( _LogDb == null ) {
            _LogDb = mongoClient.getDatabase("beerbot").getCollection(config.getJSONObject("mongodb").getString("logs"));
        }
        _persistentStorage = new PersistentStorage(
                mongoClient.getDatabase("beerbot").getCollection(config.getJSONObject("mongodb").getString("data")), 
                "id",
                ud.toString()
                );
    }
    private static final DateFormat _ORDER_REPORT_FORMATTER = new SimpleDateFormat("dd.mm.yy HH:MM");
    static {
        _ORDER_REPORT_FORMATTER.setTimeZone(TimeZone.getTimeZone("Ukraine/Kiev"));
    }
    private static final Predicate<TelegramInputMessage> _TRIVIAL_PREDICATE
        = new Predicate<TelegramInputMessage>(){
            @Override
            public boolean test(TelegramInputMessage im) {
                return true;
            }
        };
    private Function<TelegramInputMessage,OutputMessage> _textMessage(String msg) {
        return new Function<TelegramInputMessage,OutputMessage>() {
            @Override
            public OutputMessage apply(TelegramInputMessage im) {
                return new TelegramTextOutputMessage(_ud, msg);
            }
        };
    }
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
        orderMap.put("delivery_fee",(double)20.0);
        _Log.info(orderMap.toString());
        context.put("order",orderMap);
        _Log.info(String.format("context: %s",context));
        return context;
    }
    private static String _ProcessTemplate(String templateName, Map<String, Object> additionalContext) {
        Jinjava jinjava = new Jinjava();
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

        String template = MiscUtils.GetResource(templateName);
        _Log.info(String.format("template: %s",template));
        String renderedTemplate = MiscUtils.GetResource(templateName);
        renderedTemplate = jinjava.render(template, context);	
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
    public ElDrinkoStateMachine setUp() {
        ElDrinkoStateMachine res = (ElDrinkoStateMachine) this
            .addTransition("_", "start", _TRIVIAL_PREDICATE, new Function<TelegramInputMessage,OutputMessage>() {
                @Override
                public OutputMessage apply(TelegramInputMessage im) {
                    return new OutputArrayMessage(new OutputMessage[]{
                        new TelegramTextOutputMessage(_ud,
                            _ProcessTemplate("ae784befe1f1bac4d5929a4a",null)),
                            _InflateTelegramKeyboard(_ud,null,"fdb3ef9a7dcc8e36c4fa489f","16c4082080253eee262c9cf2")
                    });
                }
            }
            )
            .addTransition("start", "choose_product_to_see_description", 
                    _MessageKeyboardComparisonPredicate("1"), 
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            return _InflateTelegramKeyboard(_ud,null,
                                    "a96f38cbc06abbd47de38fe3","76a5127e87e48a4e6759d248");
                        }
                    }
                    )
            .addTransition("choose_product_to_see_description", "start", _MessageKeyboardComparisonPredicate(null), 
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                            Tsv tsv = new Tsv(MiscUtils.SafeUrl(_BEERLIST));
                            String msg 
                                = tsv.getColumn("description").get(Integer.parseInt(tka.getMsg()));
                            String imgUrl 
                                = tsv.getColumn("image link").get(Integer.parseInt(tka.getMsg()));
                            _Log.info(String.format("msg: %s\nimgUrl: %s",msg,imgUrl));
                            return new OutputArrayMessage(new OutputMessage[]{
                                new TelegramImageOutputMessage(_ud, msg, MiscUtils.SafeUrl(imgUrl)),
                                _InflateTelegramKeyboard(_ud,null,
                                        "fdb3ef9a7dcc8e36c4fa489f","16c4082080253eee262c9cf2")
                            });
                        }
                    })
        .addTransition("start","choose_product_to_make_order",
                _MessageKeyboardComparisonPredicate("0"),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        return _InflateTelegramKeyboard(_ud,null,
                                "67c31fcc0fa6566a955c1792","76a5127e87e48a4e6759d248");
                    }
                })
            .addTransition("choose_product_to_make_order","choose_amount",_MessageKeyboardComparisonPredicate(null),
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
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
                            return new TelegramTextOutputMessage(_ud,
                                    _ProcessTemplate("ec779e4315ccf36a38c2d470",
                                        _OrderObjectToJinjaContext(order)));
                        }
                    })
        .addTransition("choose_amount","confirm",
                new Predicate<TelegramInputMessage>() {
                    @Override
                    public boolean test(TelegramInputMessage tim) {
                        if( !(tim instanceof TelegramTextInputMessage) ) {
                            return false;
                        }
                        TelegramTextInputMessage ttim = (TelegramTextInputMessage) tim;
                        float amount = 0;
                        try {
                            amount = Float.parseFloat(ttim.getMsg());
                        } catch(Exception e) {
                            return false;
                        }
                        if( !MiscUtils.IsFloatInteger(2*amount) ) {
                            return false;
                        }
                        return true;
                    }
                },
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        TelegramTextInputMessage ttim = (TelegramTextInputMessage) im;
                        float amount = 0;
                        try {
                            amount = Float.parseFloat(ttim.getMsg());
                        } catch(Exception e) {}
                        JSONObject order = new JSONObject(_persistentStorage.get("order"));
                        JSONArray cart = order.getJSONArray("cart");
                        JSONObject obj = cart.getJSONObject(cart.length()-1);
                        obj.put("amount",amount);
                        _persistentStorage.set("order",order.toString());
                        return _InflateTelegramKeyboard(_ud,_OrderObjectToJinjaContext(order),
                                "7a70873a5685da4f9cb2c609","494f92141fc09881489ff307"
                                );
                    }
                })
            .addTransition("confirm","choose_product_to_make_order",
                    _MessageKeyboardComparisonPredicate("0"),
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            return _IncrementOrderCount(_ud,null,
                                    "5ab45bdbebe1bd5cdaccb425",
                                    "76a5127e87e48a4e6759d248"
                                    );
                        }
            })
            .addTransition("confirm","delete",_MessageKeyboardComparisonPredicate("2"),
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            JSONObject order = new JSONObject(_persistentStorage.get("order"));
                            _Log.info(String.format("order: %s\n",order));
                            return _InflateTelegramKeyboard(_ud,
                                    _OrderObjectToJinjaContext(order),
                                    "2ae6c7859b755abf51a3289b",
                                    "ac9a00c3bc0e96735b047a7e");
                        }
                    })
        .addTransition("delete","confirm",_MessageKeyboardComparisonPredicate(null),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                        int i = Integer.parseInt(tka.getMsg());
                        JSONObject order = new JSONObject(_persistentStorage.get("order"));
                        JSONArray cart = order.getJSONArray("cart");
                        JSONObject removed = cart.getJSONObject(i);
                        cart.remove(i);
                        _persistentStorage.set("order",order.toString());
                        Map<String,Object> orderMap = _OrderObjectToJinjaContext(order);
                        orderMap.put("removed",JSONTools.JSONObjectToMap(removed));
                        return _InflateTelegramKeyboard(_ud,orderMap,
                                "754a44a81961ac43dda890e7","494f92141fc09881489ff307");
                    }
                })
        .addTransition("confirm","choose_address",
                _MessageKeyboardComparisonPredicate("1"),
                _textMessage(MiscUtils.GetResource("054edccc65c193f7583a5773")))
            .addTransition("choose_address","choose_payment",_IS_TEXT_MESSAGE,
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            _persistentStorage.set("address",im.getMsg());
                            return _InflateTelegramKeyboard(_ud,null,"1dc02faec7377fc537510e30",
                                    "4ea9a63509e8ed5826a37f8a");
                        }
                    })
        .addTransition("choose_payment","send",_MessageKeyboardComparisonPredicate(null),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                        int i = Integer.parseInt(tka.getMsg());
                        _persistentStorage.set("payment",_PAYMENT_METHOD[i]);
                        JSONObject order = _GetOrder(_persistentStorage);
                        return _InflateTelegramKeyboard(_ud,_OrderObjectToJinjaContext(order),
                                "eb34fa7ee27d1192ef20f960",
                                "a203791aec81dfaf5a187b3d"
                                );
                    }
                })
        .addTransition("send","edit_address",_MessageComparisonPredicate("2"),
                _textMessage("введите адрес"))
            .addTransition("edit_address","send",_IS_TEXT_MESSAGE,
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            _persistentStorage.set("address",im.getMsg());
                            return _InflateTelegramKeyboard(_ud,
                                    _OrderObjectToJinjaContext(_GetOrder(_persistentStorage)),
                                    "eb34fa7ee27d1192ef20f960",
                                    "a203791aec81dfaf5a187b3d"
                                    );
                        }
                    })
        .addTransition("send","choose_payment",_MessageComparisonPredicate("1"),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        _persistentStorage.set("address",im.getMsg());
                        return _InflateTelegramKeyboard(_ud,null,"1dc02faec7377fc537510e30",
                                "4ea9a63509e8ed5826a37f8a");
                    }
                })
        .addTransition("send","start",_MessageComparisonPredicate("0"),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        JSONObject order = _GetOrder(_persistentStorage);
                        order.put("uid",_ud.getUserName());
                        order.put("count",_IncrementOrderCount(_masterPersistentStorage));
                        order.put("timestamp", _ORDER_REPORT_FORMATTER.format(new Date()));
                        _sendOrderCallback.accept(
                                _ProcessTemplate("3804e512b18b339fe8786dbd",
                                    _OrderObjectToJinjaContext(order)));
                        _persistentStorage.set("order","");
                        return _InflateTelegramKeyboard(_ud,null,
                                "fdb3ef9a7dcc8e36c4fa489f","16c4082080253eee262c9cf2")
                }
            })
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
        order.put("address",persistentStorage.get("address"));
        order.put("payment",persistentStorage.get("payment"));
        return order;
    }
    @Override
    protected void _onSetStateCallback(String state) {
        _Log.info(String.format("state: %s\n",state));
        _persistentStorage.set("state",state);
    }
    @Override
    protected void _log(String msg) {
        _Log.info(msg);
        if(_LogDb!=null && _ud!=null && false) {
            Date now = new Date();
            _LogDb.insertOne(new Document("_ud",_ud.toString())
                    .append("date",now.toGMTString())
                    .append("msg",msg)
                    );
        }
    }
}
