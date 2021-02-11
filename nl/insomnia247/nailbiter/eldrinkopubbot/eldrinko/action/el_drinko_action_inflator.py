"""===============================================================================

        FILE: /Users/nailbiter/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/eldrinko/action/el_drinko_action_inflator.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION:

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION:
     VERSION: ---
     CREATED: 2021-02-07T14:32:53.623252
    REVISION: ---

==============================================================================="""
from nl.insomnia247.nailbiter.eldrinkopubbot.eldrinko import ElDrinkoInputMessage
from nl.insomnia247.nailbiter.eldrinkopubbot.util import parse_ukrainian_float, process_template
from nl.insomnia247.nailbiter.eldrinkopubbot.telegram import TelegramKeyboard, TelegramTextOutputMessage, TelegramImageOutputMessage, TelegramArrayOutputMessage
from jinja2 import Environment, Template
from jinja2.loaders import FileSystemLoader
import json
from datetime import datetime
import pytz
import logging

#    private static JSONObject _GetOrder(JSONObject obj) {
#        JSONObject order = obj.optJSONObject("order");
#        if(order!=null) {
#            order = new JSONObject(order.toString());
#            final String[] KEYS = new String[] {"phone_number","address","payment"};
#            for(String k: KEYS) {
#                if(obj.has(k)) {
#                    order.put(k,obj.getString(k));
#                }
#            }
#        }
#        return order;
#    }


def _get_order(obj):
    order = obj.get("order", None)
    if order is not None:
        order = json.loads(json.dumps(order))
        for k in ["phone_number", "address", "payment"]:
            if k in obj:
                obj[k] = str(obj[k])
    return order

#    private static int _IncrementOrderCount(PersistentStorage masterPersistentStorage) {
#        final String FN = "order_count";
#        int res = 0;
#        if( masterPersistentStorage.contains(FN) ) {
#            res = Integer.parseInt(masterPersistentStorage.get(FN));
#        }
#        masterPersistentStorage.set(FN,Integer.toString(res+1));
#        return res+1;
#    }


def _increment_order_count(persistent_storage):
    res = int(persistent_storage.get("order_count", 0))
    res += 1
    persistent_storage.set("order_count", res)
    return res


class _DateTimeFormatter:
    def __init__(self):
        self._tz = pytz.timezone("Europe/Kiev")

    def __call__(self, d):
        return d.astimezone(self._tz).strftime("%Y.%m.%d %H:%M")


class ElDrinkoActionInflator:
    # public static String[] BOTTLE_TYPES = new String[] {"0,5","1,0","1,5","2,0","3,0"};
    BOTTLE_TYPES = ["0,5", "1,0", "1,5", "2,0", "3,0"]

    def __init__(self, send_message_callback, persistent_storage, insert_order_callback, template_folder):
        self._send_message_callback = send_message_callback
        self._persistent_storage = persistent_storage
        self._insert_order_callback = insert_order_callback
        self._jinja_env = Environment(loader=FileSystemLoader(template_folder))
        self._date_time_formatter = _DateTimeFormatter()
        self._transitions = None
        self._logger = logging.getLogger(self.__class__.__name__)
        with open(f"{template_folder}/transitions.json") as f:
            self._transitions = json.load(f)

    def _call(self, o, im):
        """return (outmessage, user_data_update)"""
#        return new Function<ElDrinkoInputMessage, ImmutablePair<OutputMessage,JSONObject>>() {
#            @Override
#            public ImmutablePair<OutputMessage,JSONObject> apply(ElDrinkoInputMessage im) {
#                _Log.info(SecureString.format("here with %s,%s",o,im));
#                if( (((JSONObject)o).getString("correspondence")).equals("9c851972cb7438c5") || (((JSONObject)o).getString("correspondence")).equals("07defdb4543782cb")) {
        if o["correspondence"] in ["9c851972cb7438c5", "07defdb4543782cb"]:
            #                    _Log.info(SecureString.format("%s",o));
            #                    if ((((JSONObject)o).optString("src_state")).equals("choose_product_to_make_order")) {
            if o["src_state"] == "choose_product_to_make_order":
                #                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im.left;
                #                        int i = Integer.parseInt(tka.getMsg());
                #                        Tsv tsv = im.beerlist;
                #                        JSONObject order = im.right.optJSONObject("order");
                tka = im.input_message
                i = int(tka.message)
                tsv = im.beerlist
                order = im.data.get("order", None)
#                        if(order==null) {
#                            order = new JSONObject();
#                            order.put("cart",new JSONArray());
#                        }
#                        JSONObject obj = new JSONObject();
#                        String name = tsv.getColumn("name").get(i);
#                        obj.put("name",name).put("bottles",new JSONObject()).put("amount",0.0f);
#                        //FIXME: remove `amount` from here
#                        order.getJSONArray("cart").put(obj);
#                        im.right.put("order",order);
                if order is None:
                    order = {"cart": []}
                obj = {}
                name = list(beerlist["name"])[i]
                obj = {**obj, "name": name, "bottles": [], "amount": 0}
                order["cart"].append(obj)
                im.data["order"] = order
#                    } else if( ((JSONObject)o).optString("type").equals("validButton") ) {
            elif o["type"] == "validButton":
                #                        _Log.info(SecureString.format("%s",im.left.getMsg()));
                #                        _Log.info(SecureString.format("%s",im.right));
                #                        JSONArray cart = im.right.getJSONObject("order").getJSONArray("cart");
                #                        JSONObject lastItem = cart.getJSONObject(cart.length()-1);
                #                        JSONObject bottles = lastItem.getJSONObject("bottles");
                cart = im.data["order"]["cart"]
                lastItem = cart[-1]
                bottles = lastItem["bottles"]
#                        int _i = Integer.parseInt(im.left.getMsg());
                _i = int(im.input_message.message)
#                        _Log.info(SecureString.format("_i: %d",_i));
#                        int idx = _i/4;
                idx = _i/4
#                        _Log.info(SecureString.format("idx: %d",idx));
#                        boolean shouldAdd = _i%4==1;
                shouldAdd = _i % 4 == 1
#                        _Log.info(SecureString.format("shouldAdd: %s",shouldAdd));
#                        String bottleType = BOTTLE_TYPES[idx];
                bottleType = BOTTLE_TYPES[idx]
#
#                        if( !bottles.has(bottleType) ) {
#                            bottles.put(bottleType,0);
#                        }
                if bottleType not in bottles:
                    bottles[bottleType] = 0
#                        bottles.put(bottleType,bottles.getInt(bottleType) + (shouldAdd?1:-1));
                bottles[bottleType] = bottles[bottleType] + \
                    (1 if shouldAdd else -1)
#                        bottles.put(bottleType,Math.max(bottles.getInt(bottleType),0));
                bottles[bottleType] = max(bottles[bottleType], 0)
#                        float amount = 0.0f;
                amount = 0.0
#                        for(String s: BOTTLE_TYPES) {
                for s in BOTTLE_TYPES:
                    #                            try {
                    #                                amount += MiscUtils.ParseFloat(s) * bottles.optInt(s,0);
                    try:
                        amount += parse_ukrainian_float(s) * bottles.get(s, 0)
#                            } catch (MiscUtils.ParseFloatException e) {
#                                _Log.error(e);
#                            }
#                        }
#                        lastItem.put("amount",amount);
                    finally:
                        pass
                lastItem["amount"] = amount
#                    }
#                } else if( ((JSONObject)o).getString("correspondence").equals("5e11c9696e9b38f0") ) {
        elif o["correspondence"] == "5e11c9696e9b38f0":
            if o["src_state"] == "delete":
                #                    if(((JSONObject)o).optString("src_state").equals("delete")) {
                #                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im.left;
                #                        int i = Integer.parseInt(tka.getMsg());
                tka = im.input_message
                i = int(tka.message)
#                        JSONObject order = im.right.optJSONObject("order");
#                        JSONArray cart = order.getJSONArray("cart");
#                        JSONObject removed = cart.getJSONObject(i);
#                        cart.remove(i);
                order = im.data["order"]
                cart = order["cart"]
                removed = cart[i]
                cart = [x for i_, x in enumerate(cart) if i_ != i]
#                    }
#                } else if( (((JSONObject)o).getString("correspondence").equals("72e97b89bcab08c4") && ((JSONObject)o).getString("src_state").equals("choose_address")) ||  ((JSONObject)o).getString("correspondence").equals("774ed3e0f5ef17cf")) {
        elif (o["correspondence"] == "72e97b89bcab08c4" and o["src_state"] == "choose_payment") or o["correspondence"] == "774ed3e0f5ef17cf":
            #                    im.right.put("address",im.left.getMsg());
            im.data["address"] = im.input_message.message
#                } else if( ((JSONObject)o).getString("correspondence").equals("8e0edde4a3199d0c") ) {
        elif o["correspondence"] == "8e0edde4a3199d0c":
            #                    im.right.put("phone_number",im.left.getMsg());
            im.data["phone_number"] = im.input_message.message
#                } else if( ((JSONObject)o).getString("correspondence").equals("fa702a44b70ddcae") ) {
        elif o["correspondence"] == "fa702a44b70ddcae":
            #                    if(((JSONObject)o).optString("src_state").equals("edit_address")) {
            #                        im.right.put("address",im.left.getMsg());
            if o["src_state"] == "edit_address":
                im.data["address"] = im.input_message.message
#                    } else if(((JSONObject)o).optString("src_state").equals("choose_payment")) {
            elif o["src_state"] == "choose_payment":
                #                        TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im.left;
                #                        int i = Integer.parseInt(tka.getMsg());
                #                        String paymentMethods
                #                            = MiscUtils.ProcessTemplate("4ea9a63509e8ed5826a37f8a",null,im.beerlist);
                #                        im.right.put("payment", paymentMethods.split("\n")[i]);
                tka = im.input_message
                i = int(tka.message)
                paymentMethods = self._jinja_env.get_template(
                    "4ea9a63509e8ed5826a37f8a.txt").render().split("\n")
                im.data["payment"] = paymentMethods[i]
#                    } else if(((JSONObject)o).optString("src_state").equals("edit_phone_number")) {
#                        im.right.put("phone_number",im.left.getMsg());
            elif o["src_state"] == "edit_phone_number":
                im.data["phone_number"] = im.input_message.message
#                    }
#                } else if( ((JSONObject)o).getString("correspondence").equals("48c6907046b03db8") ) {
        elif o["correspondence"] == "48c6907046b03db8":
            #                    JSONObject order = _GetOrder(im.right);
            #                    order.put("uid",im.userData.getUserName());
            order = _get_order(im.data)
            order["uid"] = im.user_data.username
#                    order.put("count",_IncrementOrderCount(_masterPersistentStorage));
#                    Date d = new Date();
#                    order.put("timestamp", _ORDER_REPORT_FORMATTER.format(d));
#                    order.put("_timestamp", d);
            order["count"] = _increment_order_count(self._persistent_storage)
            d = datetime.now().date()
            order["timestamp"] = self._date_time_formatter(d)
            order["_timestamp"] = d
#                    Map<String,Object> map = _OrderObjectToJinjaContext(order,im.beerlist);
#                    _orderInserter.accept(new Document(map));
            map_ = ElDrinkoActionInflator._order_object_to_jinja_context(
                order, im.beerlist)
            self._orderInserter(map_)
#                    _sendOrderCallback.accept(new ImmutablePair<String,String>(
#                            MiscUtils.ProcessTemplate("3804e512b18b339fe8786dbd",map,im.beerlist)
#                                ,"salesmanChatIds"));
            self._sendOrderCallback(
                self._jinja_env.get_template(
                    "3804e512b18b339fe8786dbd.txt").render({**map_}),
                "salesmanChatIds"
            )
#                    _sendOrderCallback.accept(new ImmutablePair<String,String>(
#                            MiscUtils.ProcessTemplate("3804e512b18b339fe8786dbd",map,im.beerlist)
#                            , im.userData.getChatId().toString()
#                                ));
#                    im.right.put("order","");
            self._sendOrderCallback(
                self._jinja_env.get_template(
                    "3804e512b18b339fe8786dbd.txt").render({**map_}),
                str(im.userData)
            )
            im.data["order"] = ""  # FIXME: this should be done prettier
        elif o["correspondence"] == "75f676187e00dd85" and o.get("src_state", None) is None:
            im.data["order"] = ""
#                } else if( ((JSONObject)o).getString("correspondence").equals("75f676187e00dd85") && ((JSONObject)o).optString("src_state").equals("null") ) {
#                    im.right.put("order","");
#                }
#
#                _Log.info(SecureString.format("before _GetOrder(%s)",im.right));
#                JSONObject ppo = _GetOrder(im.right);
        ppo = _get_order(im.data)
#                _Log.info(SecureString.format("_OrderObjectToJinjaContext(%s,%s)",ppo,im.beerlist.toJsonString()));
#                Map<String,Object> map = _OrderObjectToJinjaContext(ppo,im.beerlist);
#                Object oo = null;
        map_ = ElDrinkoActionInflator._order_object_to_jinja_context(
            ppo, im.beerlist)
        oo = None
#                _Log.info(SecureString.format("after _GetOrder(%s)",im.right));
#
#                if( (((JSONObject)o).getString("correspondence")).equals("72aa7197071b6503") ) {
        if o["correspondence"] == "72aa7197071b6503":
            #                    TelegramKeyboardAnswer tka = (TelegramKeyboardAnswer) im.left;
            #                    Tsv tsv = im.beerlist;
            tka = im.input_message
            tsv = im.beerlist
#                    String imgUrl
#                        = tsv.getColumn("image link").get(Integer.parseInt(tka.getMsg()));
            imgUrl = list(tsv["image link"])[int(tka.message)]
#                    map.put("i",Integer.parseInt(tka.getMsg()));
#                    oo = MiscUtils.SafeUrl(imgUrl);
            map_["i"] = int(tka.message)
            oo = imgUrl
#                } else if((((JSONObject)o).getString("correspondence")).equals("9c851972cb7438c5") || (((JSONObject)o).getString("correspondence")).equals("07defdb4543782cb") ) {
        elif o["correspondence"] == "9c851972cb7438c5" or o["correspondence"] == "07defdb4543782cb":
            #                    Map<String,Object> beerVolumes = new HashMap<>();
            #                    Tsv tsv = im.beerlist;
            #
            #                    float totalVolume = 0.0f;
            beerVolumes = {}
            tsv = im.beerlist
            totalVolume = 0.0

#                    JSONArray cart = im.right.getJSONObject("order").getJSONArray("cart");
#                    JSONObject bottles = cart.getJSONObject(cart.length()-1).getJSONObject("bottles");
#                    String beerName = cart.getJSONObject(cart.length()-1).getString("name");
            cart = im.data["order"]["cart"]
            bottles = cart[-1]["bottles"]
            beerName = cart[-1]["name"]
#                    for(String s:BOTTLE_TYPES) {
            for s in ElDrinkoActionInflator.BOTTLE_TYPES:
                #                        float volume = 0.0f;
                #                        try {
                #                            volume = MiscUtils.ParseFloat(s);
                #                        } catch (MiscUtils.ParseFloatException e) {
                #                            _Log.error(e);
                #                        }
                #                        totalVolume += volume*bottles.optInt(s);
                volume = 0.0
                try:
                    volume = parse_ukrainian_float(s)
                finally:
                    pass
                totalVolume += volume*bottles.get(s, 0)
#                    }
#                    int i = IterableUtils.indexOf(tsv.getColumn("name"),new Predicate<String>(){
#                        @Override
#                        public boolean evaluate(String n) {
#                            return n.equals(beerName);
#                        }
#                    });
            i = next(i_ for i_, name in enumerate(
                list(tsv["name"])) if name == beerName)
#                    float totalPrice = 0.0f;
            totalPrice = 0.0
#                    try {
#                        totalPrice = totalVolume*MiscUtils.ParseFloat(tsv.getColumn("price (UAH/L)").get(i));
#                    } catch (MiscUtils.ParseFloatException e) {
#                        _Log.error(e);
#                    }
            try:
                totalPrice = totalVolume * \
                    parse_ukrainian_float(list(tsv["price (UAH/L)"])[i])
#                    map.put("totalVolume",totalVolume);
#                    map.put("totalPrice",totalPrice);
            finally:
                pass
            map_["totalVolume"] = totalVolume
            map_["totalPrice"] = totalPrice
#                    _Log.info(SecureString.format("map: %s",map));
#                }
#
#                im.right.put("username",im.userData.getUserName());
#                im.right.put("firstName",im.userData.getFirstName());
#                im.right.put("lastName",im.userData.getLastName());
        im.data["username"] = im.user_data.username
        im.data["firstName"] = im.user_data.first_name
        im.data["lastName"] = im.user_data.last_name
#                _Log.info(SecureString.format("before _InflateOutputMessage(%s,%s,%s,%s),%s",((JSONObject)o).getString("correspondence"),map,oo,im.beerlist.toJsonString(),im.right));
#                OutputMessage om = _InflateOutputMessage(((JSONObject)o).getString("correspondence"),map,oo,im.beerlist);
        om = self._inflate_output_message(
            o["correspondence"], map_, oo, im.beerlist)
#                _Log.info(SecureString.format("om: %s",om));
#                return new ImmutablePair<OutputMessage,JSONObject>(om,im.right);
        return (om, im.data)

    def __call__(self, obj):
        return lambda eim: self._call(obj, eim)
#    private static OutputMessage _InflateOutputMessage(String code, Map<String,Object> env, Object obj, Tsv tsv) {

    def _inflate_output_message(self, code, env, obj, tsv):
        #        Object m = _TRANSITIONS.getJSONObject("transitions").get(code);
        #        _Log.info(SecureString.format("m: %s",m));
        #        return _InflateOutputMessageFromJson(m,env,obj,tsv);
        self._logger.info(code)
        m = self._transitions["transitions"][code]
        return self._inflate_output_message_from_json(m, env, obj, tsv)
#    }
#    private static TelegramKeyboard _InflateTelegramKeyboard(Map<String,Object> env,String msgTemplateResName, String keysTemplateResName,Tsv tsv,int columns) {

    def _inflate_telegram_keyboard(self, env, msgTemplateResName, keysTemplateResName, tsv, columns):
        #        String keyboardKeys = MiscUtils.ProcessTemplate(keysTemplateResName,env,tsv);
        #        String keyboardMsg = MiscUtils.ProcessTemplate(msgTemplateResName,env,tsv);
        keyboardKeys = process_template(
            self._jinja_env, keysTemplateResName, env, tsv).strip().split("\n")
        keyboardMsg = process_template(
            self._jinja_env, msgTemplateResName, env, tsv)
#        _Log.info(SecureString.format("keyboardMsg: %s",keyboardMsg));
#        _Log.info(SecureString.format("keyboardKeys: %s",keyboardKeys));
#        return new TelegramKeyboard(keyboardMsg,keyboardKeys.split("\n"),columns);
        return TelegramKeyboard(message=keyboardMsg, keyboard=keyboardKeys, columns=columns)
#    }
#    private static OutputMessage _InflateOutputMessageFromJson(Object m, Map<String,Object> env, Object obj,Tsv tsv) {

    def _inflate_output_message_from_json(self, m, env, obj, tsv):
        assert isinstance(m, dict) or isinstance(m, list)
#        assert m instanceof JSONObject || m instanceof JSONArray;
#        _Log.info(m);
#        if(m instanceof JSONArray) {
        if isinstance(m, list):
            #            List<OutputMessage> msgs = new ArrayList<>();
            #            _Log.info(SecureString.format(" %s ",m));
            #            for(int i = 0; i < ((JSONArray)m).length();i++) {
            #                msgs.add(_InflateOutputMessageFromJson(((JSONArray)m).get(i),env,obj,tsv));
            #            }
            msgs = [self._inflate_output_message_from_json(
                mm, env, obj, tsv) for mm in m]
#            return new OutputArrayMessage(msgs.toArray(OutputMessage[]::new));
            return TelegramArrayOutputMessage(messages=msgs)
#        } else {
        else:
            #            JSONObject msg = (JSONObject)m;
            msg = m
#            String tag = msg.getString("tag");
            tag = msg["tag"]
#            if(tag.equals("TelegramTextOutputMessage")) {
            if tag == "TelegramTextOutputMessage":
                #                return new TelegramTextOutputMessage(MiscUtils.ProcessTemplate(msg.getString("message"), env,tsv));
                return TelegramTextOutputMessage(process_template(self._jinja_env, msg["message"], env, tsv))
#            } else if(tag.equals("TelegramKeyboard")) {
            elif tag == "TelegramKeyboard":
                #                return _InflateTelegramKeyboard(
                #                        env, msg.getString("message"), msg.getString("keyboard"),tsv,msg.optInt("columns",2));
                return self._inflate_telegram_keyboard(env, msg["message"], msg["keyboard"], tsv, msg.get("columns", 2))
#            } else if(tag.equals("TelegramImageOutputMessage")) {
            elif tag == "TelegramImageOutputMessage":
                #                return new TelegramImageOutputMessage(
                #                        MiscUtils.ProcessTemplate(msg.getString("message"), env,tsv)
                #                        , (URL)obj);
                return TelegramImageOutputMessage(message=process_template(self._jinja_env, msg["message"], env, tsv), url=obj)
#            } else {
            else:
                #                return null;
                return None
#            }
#        }
#    }
#    private static Map<String,Object> _OrderObjectToJinjaContext(JSONObject order, Tsv tsv) {
#        Map<String, Object> context = new HashMap<String,Object>();
#        context.put("BOTTLES",Arrays.asList(BOTTLE_TYPES));

    @classmethod
    def _order_object_to_jinja_context(cls, order, tsv):
        context = {"BOTTLES": cls.BOTTLE_TYPES}
#        _Log.info(order);
#        if(order==null) {
#            return context;
#        }
        if order is None:
            return context
#        List<List<String>> products = tsv.getRecords();
        products = [list(r.values()) for r in tsv.to_dict(orient="records")]
#        Map<String,Object> orderMap = JSONTools.JSONObjectToMap(order);
        orderMap = order
#        //FIXME: try to compute `sum` in templates
#        float sum = 0;
        sum_ = 0
#        JSONArray cart = order.getJSONArray("cart");
        cart = order["cart"]
#        for(int i = 0; i < cart.length(); i++) {
        for i, obj in enumerate(cart):
            #            JSONObject obj = cart.getJSONObject(i);
            #            if(!obj.has("amount")) {
            #                continue;
            #            }
            if "amount" not in obj:
                continue
#            float beerPrice = Float.parseFloat(products.stream()
#                    .filter(r -> r.get(1).equals(obj.getString("name")))
#                    .findAny()
#                    .orElse(null)
#                    .get(3));
            beerPrice = sum([r[3] for r in products if r[1] == obj["name"]])
#            sum += beerPrice * obj.getDouble("amount");
            sum_ += beerPrice * obj["amount"]
#        }
#        orderMap.put("sum",sum);
        orderMap["sum"] = sum_
#        orderMap.put("delivery_fee",sum>=250 ? (double)0.0 : (double)20.0);
        orderMap["delivery_fee"] = (0.0 if sum_ >= 250 else 20.0)
#        _Log.info(orderMap.toString());
#        context.put("order",orderMap);
        context["order"] = orderMap
#        _Log.info(SecureString.format("context: %s",context));
#        return context;
#    }
        return context
