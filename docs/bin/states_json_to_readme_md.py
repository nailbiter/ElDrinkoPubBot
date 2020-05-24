#!/usr/bin/env python3
import json
import sys
import logging
from jinja2 import Template
import os


#global const's
MD_TEMPLATE = """
# docs for `ElDrinkoPubBot`

## state diagram

![](./states.svg)

## transitions

{%for i in range(data|length)-%}
* [{{state_name_to_num[data[i][0]]}} -> {{state_name_to_num[data[i][1]]}} (transition {{i}})](#transition-{{i}})
{%endfor%}

{%for i in range(data|length)-%}
## transition {{i}}

{%-for j in range(data[i][2]|length)%}

```
{{data[i][2][j]}}
```

{%-endfor%}
{%endfor-%}
"""
#global var's
#procedures
def post_process_data(data):
    for d in data:
        texts = []
        for m in d[2]:
            fn = f"../src/main/resources/{m}.txt"
            if os.path.isfile(fn):
                with open(fn) as f:
                    texts.append(f.read())
            else:
                texts.append(m)
        d[2] = texts
    return data

#main
with open(sys.argv[1]) as fn:
    data = json.load(fn)
data = post_process_data(data)    
states = set()
for i in data:
    states.add(i[0])
    states.add(i[1])
state_name_to_num = {name:i for i,name in enumerate(sorted(list(states)))}
print(Template(MD_TEMPLATE).render({"data":data,"state_name_to_num":state_name_to_num}))
