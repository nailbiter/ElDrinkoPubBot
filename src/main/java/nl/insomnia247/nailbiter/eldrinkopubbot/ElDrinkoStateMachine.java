package nl.insomnia247.nailbiter.eldrinkopubbot;
import com.hubspot.jinjava.Jinjava;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import java.net.URL;
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
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage;
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
    private final String[] _PAYMENT_METHOD = new String[]{
        MiscUtils.GetResource("04a01e8e5b9d67c12e77ac9b"),
            MiscUtils.GetResource("3bb515ee38b009362e946b37")
    };
    private final String[] _D1343B2D16FF152D = new String[] {
        MiscUtils.GetResource("9c6abf272ea0b6c0acbefa27"),
            MiscUtils.GetResource("6c0fe50efe214e5ae28b0d99"),
            MiscUtils.GetResource("458ea57833f558fd9063c425")
    };
    private final String[] _CDB9E516E47123128F30C596 = new String[] {
        MiscUtils.GetResource("d28703745c047d0c0fdaad71"),
            MiscUtils.GetResource("52fa52f003446458b55512e0"),
            MiscUtils.GetResource("2c2e02a2cd6ef6958fc10cdd")
    };
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
    private Function<TelegramInputMessage,OutputMessage> _keyboardMessage(String msg, String[] categories) {
        return new Function<TelegramInputMessage,OutputMessage>() {
            @Override
            public OutputMessage apply(TelegramInputMessage im) {
                return new TelegramKeyboard(_ud, _ProcessTemplate(msg,null), categories);
            }
        };
    }
    private String _ProcessTemplate(String templateName, JSONObject order) {
        Jinjava jinjava = new Jinjava();
        Map<String, Object> context = new HashMap<String,Object>();
        Tsv tsv = new Tsv(MiscUtils.SafeUrl(_BEERLIST));
        List<List<String>> products = tsv.getRecords();
        context.put("products", products);
        if(order!=null) {
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
        }
        _Log.info(String.format("context: %s",context));

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
    private Function<TelegramInputMessage,OutputMessage> _productKeyboardMessage(String msg) {
        return new Function<TelegramInputMessage,OutputMessage>() {
            @Override
            public OutputMessage apply(TelegramInputMessage im) {
                Tsv tsv = new Tsv(MiscUtils.SafeUrl(_BEERLIST));
                return new TelegramKeyboard(_ud, msg, tsv.getColumn("name").toArray(new String[]{}));
            }
        };
    }
    public ElDrinkoStateMachine setUp() {
        ElDrinkoStateMachine res = (ElDrinkoStateMachine) this
            .addTransition("_", "start", _TRIVIAL_PREDICATE, new Function<TelegramInputMessage,OutputMessage>() {
                @Override
                public OutputMessage apply(TelegramInputMessage im) {
                    return new OutputArrayMessage(new OutputMessage[]{
                        new TelegramTextOutputMessage(_ud,
                                _ProcessTemplate("ae784befe1f1bac4d5929a4a",null)),
                        new TelegramKeyboard(_ud, 
                                _ProcessTemplate("fdb3ef9a7dcc8e36c4fa489f",null), 
                                new String[]{
                                    MiscUtils.GetResource("3275901e049dae508d9794bd"),
                                    MiscUtils.GetResource("0780c061af50729a89c0197b")
                                }
                                )
                    });
                }
            }
            )
            .addTransition("start", "choose_product_to_see_description", _MessageKeyboardComparisonPredicate("1"), _productKeyboardMessage(MiscUtils.GetResource("a96f38cbc06abbd47de38fe3")))
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
                                    new TelegramKeyboard(_ud, 
                                            _ProcessTemplate("fdb3ef9a7dcc8e36c4fa489f",null),
                                            new String[]{
                                                MiscUtils.GetResource("3275901e049dae508d9794bd"),
                                                MiscUtils.GetResource("0780c061af50729a89c0197b")
                                }
                                                ),
                            });
                        }
                    })
        .addTransition("start","choose_product_to_make_order",
                _MessageKeyboardComparisonPredicate("0"),
                _productKeyboardMessage(MiscUtils.GetResource("67c31fcc0fa6566a955c1792")))
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
                                    _ProcessTemplate("ec779e4315ccf36a38c2d470",order));
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
                        return new TelegramKeyboard(_ud,
                                _ProcessTemplate("7a70873a5685da4f9cb2c609",order),
                                _CDB9E516E47123128F30C596
                        );
                    }
                })
            .addTransition("confirm","choose_product_to_make_order",_MessageKeyboardComparisonPredicate("0"),_productKeyboardMessage("давайте добавим еще"))
            .addTransition("confirm","delete",_MessageKeyboardComparisonPredicate("2"),
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            _Log.info(String.format(" 1ee9068eb4441ddd \n"));
                            JSONArray cart = new JSONObject(_persistentStorage.get("order")).getJSONArray("cart");
                            _Log.info(String.format(" 17694b345db033db \n"));
                            _Log.info(String.format("cart: %s\n",cart));
                            ArrayList<String> res = new ArrayList<String>();
                            for(int i = 0; i < cart.length(); i++) {
                                res.add(String.format("%.1f литров пива \"%s\"",
                                            cart.getJSONObject(i).getDouble("amount"),
                                            cart.getJSONObject(i).getString("name")
                                            ));
                            }
                            _Log.info(String.format("res: %s\n",res));
                            return new TelegramKeyboard(_ud,"что будем удалять?",res.toArray(new String[]{}));
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
                        String m = String.format("%.1f литров пива \"%s\"",
                                cart.getJSONObject(i).getDouble("amount"),
                                cart.getJSONObject(i).getString("name")
                                );
                        cart.remove(i);
                        _persistentStorage.set("order",order.toString());
                        return new TelegramKeyboard(_ud,
                                String.format("удалили \"%s\". Что дальше?",m),
                                cart.length()>0 ? 
                                    _CDB9E516E47123128F30C596: 
                                    new String[]{_CDB9E516E47123128F30C596[0]});
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
                            return new TelegramKeyboard(_ud,MiscUtils.GetResource("1dc02faec7377fc537510e30"),
                                    _PAYMENT_METHOD);
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
                        return new TelegramKeyboard(_ud,
                                _ProcessTemplate("eb34fa7ee27d1192ef20f960",order),
                                _D1343B2D16FF152D);
                    }
                })
        .addTransition("send","edit_address",_MessageComparisonPredicate("2"),_textMessage("введите адрес"))
            .addTransition("edit_address","send",_IS_TEXT_MESSAGE,
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            _persistentStorage.set("address",im.getMsg());
                            return new TelegramKeyboard(_ud,
                                    _ProcessTemplate("eb34fa7ee27d1192ef20f960",_GetOrder(_persistentStorage)),
                                    _D1343B2D16FF152D);
                        }
                    })
        .addTransition("send","choose_payment",_MessageComparisonPredicate("1"),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        _persistentStorage.set("address",im.getMsg());
                        return new TelegramKeyboard(_ud,MiscUtils.GetResource("1dc02faec7377fc537510e30"),
                                _PAYMENT_METHOD);
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
                                _ProcessTemplate("3804e512b18b339fe8786dbd",order));
                        _persistentStorage.set("order","");
                        return new TelegramKeyboard(_ud, 
                                _ProcessTemplate("fdb3ef9a7dcc8e36c4fa489f",null), 
                                new String[]{
                                    MiscUtils.GetResource("3275901e049dae508d9794bd"),
                                    MiscUtils.GetResource("0780c061af50729a89c0197b")
                                }
                        );
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
