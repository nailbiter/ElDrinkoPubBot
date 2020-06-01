import json
import sys
from logging import basicConfig, INFO, info


#global const's
SRC_WILDCASE_STATE="(any state)"
#procedures
def add_fields(d):
    res = dict()
    correspondence = d["correspondence"]
    for c in correspondence:
        if c[0] is None:
            c[0] = SRC_WILDCASE_STATE
    transition_codes_set = {c[3] for c in correspondence}
    cut = 0
    while(len({s[:cut] for s in transition_codes_set})<len(transition_codes_set)):
        cut += 1
    res["cut"] = cut
    res["correspondence_code_to_i"] = {
            n:i 
            for i,n 
            in enumerate(sorted(list(transition_codes_set)))
            }
    
    state_set = set.union(*[set(c[:2]) for c in correspondence])
    res["SRC_WILDCASE_STATE"] = SRC_WILDCASE_STATE
    res["state_name_to_i"] = {n:i 
            for i,n 
            in enumerate(sorted(list(state_set)))
            }
    for k in d["transitions"]:
        if type(d["transitions"][k]) is dict:
            d["transitions"][k] = [d["transitions"][k]]
    res_codes_set = set()
    for k in d["transitions"]:
        for m in d["transitions"][k]:
            res_codes_set.add(m["message"])
    res_files = dict()
    for code in res_codes_set:        
        with open(f"../src/main/resources/{code}.txt") as f:
            res_files[code] = f.read()
    res["res_files"] = res_files
    return res

#main
basicConfig(level=INFO)
with open(sys.argv[1]) as f:
    d = json.load(f)
    print(json.dumps({**add_fields(d),**d},sort_keys=True,indent=2))
