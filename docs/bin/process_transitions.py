import json
import sys

#main

with open(sys.argv[1]) as f:
    d = json.load(f)
    transition_codes_set = {c[3] for c in d["correspondence"]}
    cut = 0
    while(len({s[:cut] for s in transition_codes_set})<len(transition_codes_set)):
        cut += 1
    d["cut"] = cut
    d["correspondence_code_to_i"] = {n:i for i,n in enumerate(sorted(list(transition_codes_set)))}
    
    d["state_name_to_i"] = {n:i 
            for i,n 
            in enumerate(sorted(list(set.union(*[set(c[:2]) for c in d["correspondence"]]))))
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
    d["res_files"] = res_files
    print(json.dumps(d,sort_keys=True,indent=2))
