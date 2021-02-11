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
def parse_ukrainian_float(s):
    m = re.match(r"(-?\d+)(,\d+)?",s)
    assert m is not None
    return float(s.replace(",","."))

#FIXME: eliminate this
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
def process_template(jinja_env,template_name,additional_context, tsv):
    context = {}
    context["products"] = [list(r.values())for r in tsv.to_dict(orient="records")]
    if additional_context is not None:
        for k,v in additional_context.items():
            context[k] = v
    jinja_env.filters["myprintf_int"] = lambda x:f"{int(x):02d}"
    jinja_env.filters["myprintf"] = lambda x:f"{float(x):.2d}".replace(".",",")
    return jinja_env.get_template(f"{template_name}.txt").render(context)
