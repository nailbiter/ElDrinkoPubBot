# docs for `ElDrinkoPubBot`

## state diagram

![](./states.svg)

## transitions

{%-for c in correspondence%}
* [{{c[0]}} -> {{c[1]}} (transition `t_{{c[3]['correspondence'][:cut]}}`)](#transition-t_{{c[3]['correspondence'][:cut]}})
{%-endfor%}

## transition conditions legend

* `KEY(0)`, `KEY(1)` etc. means that you have to press on first (resp. second etc.) button of the keyboard; note that
keyboard currently consists of two columns and we count buttons [row-wise](https://www.geeksforgeeks.org/row-wise-vs-column-wise-traversal-matrix/);
* `KEY(ANY)` means that for the transition, you just need to press *any* button;
* `HI` means that for the transition, you need to enter half-integer, i.e. `1.5` or `1,5` or `1.0` or `1,0` or `1`;
* `PN` means that for the transition, you need to enter phone number (i.e. `34589345`);
* `T` means that for the transition, you need to enter any text;
* `M(...)` means that for the transition, you need to enter text `...`;


{%for c in correspondence_code_to_i%}
## transition t_{{c[:cut]}}

{%-for c in transitions[c]%}

### [`{{c.message}}`](../src/main/resources/{{c.message}}.txt)

```
{{res_files[c.message]}}
```

{%-endfor%}
{%endfor%}
