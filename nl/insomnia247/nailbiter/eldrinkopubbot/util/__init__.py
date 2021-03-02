"""===============================================================================

        FILE: /home/pi/Documents/forgithub/ElDrinkoPubBot/nl/insomnia247/nailbiter/eldrinkopubbot/util/_init__.py

       USAGE: (not intended to be directly executed)

 DESCRIPTION: 

     OPTIONS: ---
REQUIREMENTS: ---
        BUGS: ---
       NOTES: ---
      AUTHOR: Alex Leontiev (alozz1991@gmail.com)
ORGANIZATION: 
     VERSION: ---
     CREATED: 2021-02-02T22:47:20.235202
    REVISION: ---

==============================================================================="""

import re
import logging


def parse_ukrainian_float(s):
    m = re.match(r"(-?\d+)(,\d+)?", s)
    assert m is not None
    return float(s.replace(",", "."))

# FIXME: eliminate this
#    public static String ProcessTemplate(String templateName, Map<String, Object> additionalContext, Tsv tsv) {
#        TemplateEngine _jinjava = new TemplateEngine();
#        Map<String,Object> context = new HashMap<>();
#        List<List<String>> products = tsv.getRecords();
#        context.put("products",products);
#        if(additionalContext!=null) {
#            for(String k:additionalContext.keySet()) {
#                context.put(k,additionalContext.get(k));
#            }
#        }
#        _Log.info(SecureString.format("_ProcessTemplate: context before rendering: %s",context));
#
#        _Log.info(SecureString.format("getting resource %s",templateName));
#        String template = MiscUtils.GetResource(templateName);
#        _Log.info(SecureString.format("template: %s",template));
#        String renderedTemplate = MiscUtils.GetResource(templateName);
#        renderedTemplate = _jinjava.render(template, context);
#        _Log.info(SecureString.format("renderedTemplate: %s",renderedTemplate));
#        return renderedTemplate;
#    }


def add_logger(f):
    logger = logging.getLogger(f.__name__)

    def _f(*args, **kwargs):
        return f(*args, logger=logger, **kwargs)
    _f.__name__ = f.__name__
    return _f


@add_logger
def _myprintf_int(x, logger=None):
    logger.debug(f"x: {x}")
    res = f"{int(x):02d}"
    logger.debug(f"res: {res}")
    return res


@add_logger
def _myprintf(x, logger=None):
    logger.debug(f"x: {x}")
    res = f"{float(x):.2f}".replace(".", ",")
    logger.debug(f"res: {res}")
    return res


@add_logger
def process_template(jinja_env, template_name, additional_context, tsv,logger=None):
    context = {
        "products": [list(r.values())for r in tsv.to_dict(orient="records")],
        "beerlist_df": tsv,
        **({} if additional_context is None else additional_context),
    }
    #FIXME: do not do this every time (only on init)
    jinja_env.filters["myprintf_int"] = _myprintf_int
    jinja_env.filters["myprintf"] = _myprintf
    logger.info(f"render {template_name} with {context}")
    return jinja_env.get_template(f"{template_name}.txt").render(context)
