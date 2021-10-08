"""===============================================================================

        FILE: nl/insomnia247/nailbiter/eldrinkopubbot/util/el_drinko_jinja_environment.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-03-04T22:49:28.983828
    REVISION: ---

==============================================================================="""
from jinja2 import Environment, Template
import logging
from nl.insomnia247.nailbiter.eldrinkopubbot.util import ukrainian_floats, add_logger
import json
import copy


@add_logger
def _myprintf_int(x, logger=None):
    logger.debug(f"x: {x}")
    res = f"{int(x):02d}"
    logger.debug(f"res: {res}")
    return res


def _deep_copy(obj):
    # return json.loads(json.dumps(obj))
    return copy.deepcopy(obj)


class ElDrinkoJinjaEnvironment(Environment):
    _PRICE_FN = "price (UAH/L)"

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.filters["myprintf_int"] = _myprintf_int
        self.filters["myprintf"] = ukrainian_floats.print
        self._logger = logging.getLogger(self.__class__.__name__)

    def process_template(self, template_name, additional_context, tsv):
        if additional_context is None:
            additional_context = {}
        additional_context = _deep_copy(additional_context)

        additional_context = {
            **additional_context,
            "totalVolume": 0,
            "totalPrice": 0,
        }
        if "order" in additional_context:
            order = additional_context["order"]
            order = {
                "sum": 0,
                **order,
            }
            price = 0
            if "cart" in order and isinstance(order["cart"], dict):
                #                for i, obj in enumerate(order["cart"]):
                #                    if "name" in obj:
                #                        totalVolume = 0
                #                        beerPrice = tsv.query(f"name==\"{obj['name']}\"")[
                #                            ElDrinkoJinjaEnvironment._PRICE_FN].sum()
                #                        totalVolume = sum([ukrainian_floats.parse(
                #                            k)*v for k, v in obj["bottles"].items()])
                #
                #                        obj["amount"] = totalVolume
                #                        additional_context["totalPrice"] = beerPrice * \
                #                            totalVolume
                #                        additional_context["totalVolume"] = totalVolume
                #                        order["sum"] += additional_context["totalPrice"]
                #                    elif "category" in obj:
                #                        for name, i in obj["goods"].items():
                #                            price = tsv.query(f"category==\"{obj['category']}\" and name==\"{name}\"")[
                #                                ElDrinkoJinjaEnvironment._PRICE_FN].sum()
                #                            order["sum"] += price*i
                #                    else:
                #                        raise NotImplementedError
                #                    order["cart"][i] = obj
                _prices = {n: p for n, p in zip(
                    tsv.name, tsv['price (UAH/L)'])}
                price = sum([v*_prices[k] for k, v in order["cart"].items()])
            additional_context["totalPrice"] = price
            order["sum"] = additional_context["totalPrice"]
            additional_context["order"] = order

        context = {
            # FIXME: eliminate `products`
            "products": [list(r.values()) for r in tsv.to_dict(orient="records")],
            "beerlist_df": tsv,
            "BOTTLES": BOTTLE_TYPES,
            **additional_context,
        }
        self._logger.info(f"render {template_name} with {context}")
        return self.get_template(f"{template_name}.txt").render(context)


BOTTLE_TYPES = [ukrainian_floats.print(x, width=1) for x in [1]]
