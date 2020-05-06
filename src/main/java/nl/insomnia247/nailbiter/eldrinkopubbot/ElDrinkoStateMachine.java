package nl.insomnia247.nailbiter.eldrinkopubbot;
import com.mongodb.MongoClient;
import java.lang.StringBuilder;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.KeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputArrayMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.model.OutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.mongodb.PersistentStorage;
import nl.insomnia247.nailbiter.eldrinkopubbot.state_machine.StateMachine;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboard;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramKeyboardAnswer;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextInputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.TelegramTextOutputMessage;
import nl.insomnia247.nailbiter.eldrinkopubbot.telegram.UserData;
import nl.insomnia247.nailbiter.eldrinkopubbot.util.Tsv;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * @author Alex Leontiev
 */
public class ElDrinkoStateMachine extends StateMachine<TelegramInputMessage,OutputMessage> {
    private static final String _BEERLIST = "https://docs.google.com/spreadsheets/d/e/2PACX-1vRGSUiAeapo7eHNfA1v9ov_Cc2oCjWNsmcpadN6crtxJ236uDOKt_C_cR1hsXCyqZucp_lQoeRHlu0k/pub?gid=0&single=true&output=tsv";
    private final UserData _ud;
    private final PersistentStorage _persistentStorage;
    private final Consumer<JSONObject>  _sendOrderCallback;
    private final String[] _PAYMENT_METHOD = new String[]{"наличными","терминал"};
    private final Predicate<TelegramInputMessage> _IS_TEXT_MESSAGE = new Predicate<>() {
        @Override
        public boolean test(TelegramInputMessage im) {
            return im instanceof TelegramTextInputMessage;
        }
    };
    public ElDrinkoStateMachine(UserData ud, MongoClient mongoClient, Consumer<JSONObject> sendOrderCallback) {
        super("_");
        _ud = ud;
        _sendOrderCallback = sendOrderCallback;
        _persistentStorage = new PersistentStorage(
                mongoClient.getDatabase("beerbot").getCollection("data"), 
                "id",
                ud.toString()
                );
    }
    private static Predicate<TelegramInputMessage> _TrivialPredicate() {
        return new Predicate<TelegramInputMessage>(){

            @Override
            public boolean test(TelegramInputMessage im) {
                return true;
            }
        };
    }
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
                return new TelegramKeyboard(_ud, _TransformMessageString(msg), categories);
            }
        };
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
                Tsv tsv = new Tsv(_SafeUrl(_BEERLIST));
                return new TelegramKeyboard(_ud, msg, tsv.getColumn("name").toArray(new String[]{}));
            }
        };
    }
    private static URL _SafeUrl(String u) {
        URL url = null;
        try {
            url = new URL(u);
        } catch (Exception e) {
            System.err.format("malformed url: \"%s\"\n",u);
        }
        return url;
    }
    private Function<TelegramInputMessage,OutputMessage> _message(OutputMessage om) {
        return new Function<TelegramInputMessage,OutputMessage>() {
            @Override
            public OutputMessage apply(TelegramInputMessage im) {
                return om;
            }
        };
    }
    private static String _TransformMessageString(String msg) {
        return msg
            .replace("%p",_FormattedProductList())
            ;
    }
    private static String _FormattedProductList() {
        Tsv tsv = new Tsv(_SafeUrl(_BEERLIST));
        StringBuilder sb = new StringBuilder();
        List<String> descriptions = tsv.getColumn("name");
        for(int i = 0; i < descriptions.size(); i++) {
            sb.append(String.format("%d. %s",i+1,descriptions.get(i)));
            if(i+1<descriptions.size()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    public ElDrinkoStateMachine setUp() {
        ElDrinkoStateMachine res = (ElDrinkoStateMachine) this
            .addTransition("_", "start", _TrivialPredicate(), _keyboardMessage("Добро Пожаловать. Сейчас у нас есть:\n%p",
                        new String[]{"Посмотреть описание","Сформировать заказ покупку"}
                        ))
            .addTransition("start", "choose_product_to_see_description", _MessageKeyboardComparisonPredicate("0"), _productKeyboardMessage("Выберите продукт"))
            .addTransition("choose_product_to_see_description", "start", _MessageKeyboardComparisonPredicate(null), 
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                            Tsv tsv = new Tsv(_SafeUrl(_BEERLIST));
                            return new OutputArrayMessage(new OutputMessage[]{
                                new TelegramTextOutputMessage(_ud,
                                        tsv.getColumn("description").get(Integer.parseInt(tka.getMsg()))),
                                    new TelegramKeyboard(_ud, 
                                            _TransformMessageString("Добро Пожаловать. Сейчас у нас есть:\n%p"),
                                            new String[]{"Посмотреть описание","Сформировать заказ покупку"}),
                            });
                        }
                    })
            .addTransition("start","choose_product_to_make_order",_MessageKeyboardComparisonPredicate("1"),_productKeyboardMessage("Выберите продукт"))
            .addTransition("choose_product_to_make_order","choose_amount",_MessageKeyboardComparisonPredicate(null),
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                            int i = Integer.parseInt(tka.getMsg());
                            Tsv tsv = new Tsv(_SafeUrl(_BEERLIST));
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
                                    String.format("Вы выбрали \"%s\". Выберите количество (в литрах, с кратностью поллитра)",
                                        name
                                        ));
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
                        if( !_IsFloatInteger(2*amount) ) {
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
                                String.format("Вы выбрали %.1f литров пива \"%s\". Что дальше?",
                                    obj.getDouble("amount"),
                                    obj.getString("name")
                                    ),
                                new String[]{"добавить еще", "оформить заказ", "удалить заказанное"}
                                );
                    }
                })
        .addTransition("confirm","choose_product_to_make_order",_MessageKeyboardComparisonPredicate("0"),_productKeyboardMessage("давайте добавим еще"))
            .addTransition("confirm","delete",_MessageKeyboardComparisonPredicate("2"),
                    new Function<TelegramInputMessage,OutputMessage>() {
                        @Override
                        public OutputMessage apply(TelegramInputMessage im) {
                            System.err.format(" 1ee9068eb4441ddd \n");
                            JSONArray cart = new JSONObject(_persistentStorage.get("order")).getJSONArray("cart");
                            System.err.format(" 17694b345db033db \n");
                            System.err.format("cart: %s\n",cart);
                            ArrayList<String> res = new ArrayList<String>();
                            for(int i = 0; i < cart.length(); i++) {
                                res.add(String.format("%.1f литров пива \"%s\"",
                                            cart.getJSONObject(i).getDouble("amount"),
                                            cart.getJSONObject(i).getString("name")
                                            ));
                            }
                            System.err.format("res: %s\n",res);
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
                                cart.length()>0 
                                ? new String[]{"добавить еще", "оформить заказ", "удалить заказанное"}
                                : new String[]{"добавить еще"}
                                );
                    }
                })
        .addTransition("confirm","choose_address",_MessageKeyboardComparisonPredicate("1"),_textMessage("введите адрес"))
        .addTransition("choose_address","choose_payment",_IS_TEXT_MESSAGE,
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        _persistentStorage.set("address",im.getMsg());
                        return new TelegramKeyboard(_ud,"Выберите способ оплаты",_PAYMENT_METHOD);
                    }
        })
        .addTransition("choose_payment","send",_MessageKeyboardComparisonPredicate(null),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im;
                        int i = Integer.parseInt(tka.getMsg());
                        _persistentStorage.set("payment",_PAYMENT_METHOD[i]);
                        return new TelegramKeyboard(_ud,
                                String.format("адрес: %s, способ платежа: %s",
                                    _persistentStorage.get("address"),
                                    _persistentStorage.get("payment")
                                    ),
                                new String[]{"отправить заказ","изменить способ оплаты","изменить адрес"}
                                );
                    }
        })
        .addTransition("send","edit_address",_MessageComparisonPredicate("2"),_textMessage("введите адрес"))
        .addTransition("edit_address","send",_IS_TEXT_MESSAGE,
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        _persistentStorage.set("address",im.getMsg());
                        return new TelegramKeyboard(_ud,
                                String.format("адрес: %s, способ платежа: %s",
                                    _persistentStorage.get("address"),
                                    _persistentStorage.get("payment")
                                    ),
                                new String[]{"отправить заказ","изменить способ оплаты","изменить адрес"}
                                );
                    }
        })
        .addTransition("send","choose_payment",_MessageComparisonPredicate("1"),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        _persistentStorage.set("address",im.getMsg());
                        return new TelegramKeyboard(_ud,"Выберите способ оплаты",_PAYMENT_METHOD);
                    }
        })
        .addTransition("send","start",_MessageComparisonPredicate("0"),
                new Function<TelegramInputMessage,OutputMessage>() {
                    @Override
                    public OutputMessage apply(TelegramInputMessage im) {
                        JSONObject obj = new JSONObject(_persistentStorage.get("order"));
                        obj.put("address",_persistentStorage.get("address"));
                        obj.put("payment",_persistentStorage.get("payment"));
                        obj.put("uid",_ud.toString());
                        _sendOrderCallback.accept(obj);
                        _persistentStorage.set("order","");
                        return new TelegramKeyboard(_ud,"Благодарим за заказ!",new String[]{"Посмотреть описание","Сформировать заказ покупку"});
                    }
        })
            ;
        if(_persistentStorage.contains("state")) {
            System.err.format("setting _currentState to \"%s\"\n",_persistentStorage.get("state"));
            this._currentState = _persistentStorage.get("state");
        }
        return res;
    }
    private static boolean _IsFloatInteger(float f) {
        return f==Math.floor(f);
    }
    @Override
    protected void _onSetStateCallback(String state) {
        System.err.format("state: %s\n",state);
        _persistentStorage.set("state",state);
    }
}
