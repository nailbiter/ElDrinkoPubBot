#!/usr/bin/env python3
import json
import sys
import logging
from jinja2 import Template


#global const's
GV_TEMPLATE = """
digraph {
    {%-for d in data%}
    {{state_name_to_num[d[0]]}} -> {{state_name_to_num[d[1]]}};
    {%-endfor%}
}
"""
#global var's
#procedures

#main
with open(sys.argv[1]) as fn:
    data = json.load(fn)
states = set()
for i in data:
    states.add(i[0])
    states.add(i[1])
state_name_to_num = {name:i for i,name in enumerate(sorted(list(states)))}
print(Template(GV_TEMPLATE).render({"data":data,"state_name_to_num":state_name_to_num}))