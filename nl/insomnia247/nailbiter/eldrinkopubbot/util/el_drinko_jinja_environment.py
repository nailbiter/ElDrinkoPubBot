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

@add_logger
def _myprintf_int(x, logger=None):
    logger.debug(f"x: {x}")
    res = f"{int(x):02d}"
    logger.debug(f"res: {res}")
    return res

class ElDrinkoJinjaEnvironment(Environment):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.filters["myprintf_int"] = _myprintf_int
        self.filters["myprintf"] = ukrainian_floats.print
        self._logger = logging.getLogger(self.__class__.__name__)

    def process_template(self, template_name, additional_context, tsv):
        context = {
            # FIXME: eliminate
            "products": [list(r.values()) for r in tsv.to_dict(orient="records")],
            "beerlist_df": tsv,
            **({} if additional_context is None else additional_context),
        }
        self._logger.info(f"render {template_name} with {context}")
        return self.get_template(f"{template_name}.txt").render(context)
