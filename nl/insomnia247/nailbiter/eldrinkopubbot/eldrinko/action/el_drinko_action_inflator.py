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
from nl.insomnia247.nailbiter.eldrinkopubbot.util import add_logger, ukrainian_floats
from nl.insomnia247.nailbiter.eldrinkopubbot.util.el_drinko_jinja_environment import ElDrinkoJinjaEnvironment, BOTTLE_TYPES
from nl.insomnia247.nailbiter.eldrinkopubbot.telegram import TelegramKeyboard, TelegramTextOutputMessage, TelegramImageOutputMessage, TelegramArrayOutputMessage
from jinja2.loaders import FileSystemLoader
import json
from datetime import datetime, date
import pytz
import logging
import math


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
    def __init__(self, send_message_callback, persistent_storage, insert_order_callback, template_folder):
        self._send_message_callback = send_message_callback
        self._persistent_storage = persistent_storage
        self._insert_order_callback = insert_order_callback
        self._sendOrderCallback = send_message_callback
        self._jinja_env = ElDrinkoJinjaEnvironment(
            loader=FileSystemLoader(template_folder))
        self._date_time_formatter = _DateTimeFormatter()
        self._transitions = None
        self._logger = logging.getLogger(self.__class__.__name__)
        self._transitions = {}
        with open(f"{template_folder}/transitions.json") as f:
            self._transitions["transitions"] = json.load(f)
        with open(f"{template_folder}/correspondence.json") as f:
            self._transitions["correspondence"] = json.load(f)

    @property
    def transitions(self):
        return self._transitions

    def _call(self, o, im, src_state, dst_state):
        """return (outmessage, user_data_update)"""
        if o["correspondence"] in ["9c851972cb7438c5", "07defdb4543782cb"]:
            if o.get("src_state", None) == "choose_product_to_make_order":
                tka = im.input_message
                i = int(tka.message)
                tsv = im.beerlist
                order = im.data.order
                obj = {}
                name = list(tsv["name"])[i]
                obj = {
                    **obj,
                    "name": name,
                    "bottles": {},
                }
                cart = order["cart"]
                cart.append(obj)
            elif o["type"] == "validButton":
                cart = im.data.order["cart"]
                lastItem = cart[-1]
                bottles = lastItem["bottles"]
                _i = int(im.input_message.message)
                idx = int(_i/4)
                shouldAdd = _i % 4 == 1
                bottleType = BOTTLE_TYPES[idx]
                if bottleType not in bottles:
                    bottles[bottleType] = 0
                bottles[bottleType] = bottles[bottleType] + \
                    (1 if shouldAdd else -1)
                bottles[bottleType] = max(bottles[bottleType], 0)
        elif o["correspondence"] == "5e11c9696e9b38f0":
            self._logger.info("here")
            if o.get("src_state", None) == "delete":
                self._logger.info("here")
                tka = im.input_message
                i = int(tka.message)
                order = im.data.order
                cart = order["cart"]
#                removed = cart[i]
#                cart = [x for i_, x in enumerate(cart) if i_ != i]
                del cart[i]
        elif (o["correspondence"] == "72e97b89bcab08c4" and o.get("src_state", None) == "choose_address") or o["correspondence"] == "774ed3e0f5ef17cf":
            im.data["address"] = im.input_message.message
        elif o["correspondence"] == "8e0edde4a3199d0c":
            im.data["phone_number"] = im.input_message.message
        elif o["correspondence"] == "fa702a44b70ddcae":
            if o.get("src_state", None) == "edit_address":
                im.data["address"] = im.input_message.message
            elif o.get("src_state", None) == "choose_payment":
                tka = im.input_message
                i = int(tka.message)
                paymentMethods = self._jinja_env.get_template(
                    "4ea9a63509e8ed5826a37f8a.txt").render().split("\n")
                im.data["payment"] = paymentMethods[i]
            elif o.get("src_state", None) == "edit_phone_number":
                im.data["phone_number"] = im.input_message.message
        elif o["correspondence"] == "48c6907046b03db8":
            order = im.data.get_order_pretty()
            order["uid"] = im.user_data.username
            tsv = im.beerlist
            order["count"] = _increment_order_count(self._persistent_storage)
            d = datetime.now()
            order["timestamp"] = self._date_time_formatter(d)
            order["_timestamp"] = d
            map_ = self._order_object_to_jinja_context(
                order, im.beerlist)
            self._insert_order_callback(map_)
            for tgt in ["salesmanChatIds", str(im.user_data)]:
                self._sendOrderCallback(
                    self._jinja_env.process_template(
                        "made_order_notification", map_, tsv),
                    tgt
                )
            im.data.reset_order()
        elif o["correspondence"] == "75f676187e00dd85" and o.get("src_state", None) is None:
            im.data.reset_order()

        ppo = im.data.get_order_pretty()
        map_ = self._order_object_to_jinja_context(
            ppo, im.beerlist)
        oo = None

        if o["correspondence"] == "72aa7197071b6503":
            tka = im.input_message
            tsv = im.beerlist
            imgUrl = list(tsv["image link"])[int(tka.message)]
            map_["i"] = int(tka.message)
            oo = imgUrl
        elif o["correspondence"] == "02503b04d94259c5":
            r = im.beerlist.query(f"name=='{im.input_message.button_title}'").to_dict(
                orient="records")[0]
            oo = r["image link"]
            map_["description"] = r["description"]
        elif o["correspondence"] == "18ca55e51d11ba24":
            map_["category"] = im.input_message.button_title
        elif o["correspondence"] == "12f00bba97cabd0d":
            cart = im.data["cart"]
            if src_state == "choose_snack_amount":
                if o["type"] == "validButton":
                    button_idx = int(im.input_message.message)
                    last_order = cart[-1]
                    self._logger.info(last_order)
                    self._logger.info(im.beerlist)
                    goods_df = im.beerlist.query(
                        f"category==\"{last_order['category']}\"")
                    self._logger.info(goods_df)
                    self._logger.info(button_idx)
                    inc = 1 if button_idx % 4 == 1 else -1
                    goods_r = goods_df.to_dict(orient="records")[
                        math.floor(button_idx/4)]
                    last_order["goods"][goods_r["name"]] = last_order["goods"].get(
                        goods_r["name"], 0) + inc
                    last_order["goods"][goods_r["name"]] = max(
                        last_order["goods"][goods_r["name"]], 0)
            else:
                cart.append(
                    {"category": im.input_message.button_title, "goods": {}})

        # FIXME: we need this?
        im.data["username"] = im.user_data.username
        im.data["firstName"] = im.user_data.first_name
        im.data["lastName"] = im.user_data.last_name
        om = self._inflate_output_message(
            o["correspondence"], {**map_, "data": im.data}, oo, im.beerlist)
        return (om, im.data)

    def __call__(self, obj, src_state, dst_state):
        return lambda eim: self._call(obj, eim, src_state, dst_state)

    def _inflate_output_message(self, code, env, obj, tsv):
        self._logger.info(code)
        m = self._transitions["transitions"][code]
        return self._inflate_output_message_from_json(m, env, obj, tsv)

    def _inflate_telegram_keyboard(self, env, msgTemplateResName, keysTemplateResName, tsv, columns):
        keyboardKeys = self._jinja_env.process_template(
            keysTemplateResName, env, tsv)
        keyboardKeys = keyboardKeys.strip().split("\n")
        self._logger.debug(f"keyboardKeys: {keyboardKeys}")
        keyboardKeys = [l for l in keyboardKeys if len(l.strip()) > 0]
        self._logger.debug(f"keyboardKeys: {keyboardKeys}")
        keyboardMsg = self._jinja_env.process_template(
            msgTemplateResName, env, tsv)
        return TelegramKeyboard(message=keyboardMsg, keyboard=keyboardKeys, columns=columns)

    def _inflate_output_message_from_json(self, m, env, obj, tsv):
        assert isinstance(m, dict) or isinstance(m, list)
        if isinstance(m, list):
            msgs = []
            for mm in m:
                if str(mm) in self._transitions["transitions"]:
                    mm = self._transitions["transitions"][str(mm)]
                msgs.append(self._inflate_output_message_from_json(
                    mm, env, obj, tsv))
            return TelegramArrayOutputMessage(messages=msgs)
        else:
            msg = m
            tag = msg["tag"]
            if tag == "TelegramTextOutputMessage":
                return TelegramTextOutputMessage(self._jinja_env.process_template(msg["message"], env, tsv))
            elif tag == "TelegramKeyboard":
                return self._inflate_telegram_keyboard(env, msg["message"], msg["keyboard"], tsv, msg.get("columns", 2))
            elif tag == "TelegramImageOutputMessage":
                return TelegramImageOutputMessage(message=self._jinja_env.process_template(msg["message"], env, tsv), url=obj)
            else:
                return None

    def _order_object_to_jinja_context(self, order, tsv):
        self._logger.info(f"order: {order}")
        context = {}
        if order is not None:
            context["order"] = order
        self._logger.info(f"context: {context}")
        return context
