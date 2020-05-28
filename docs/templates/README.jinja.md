# docs for `ElDrinkoPubBot`

## state diagram

![](./states.svg)

## transitions

{%for c in correspondence-%}
* [{{c[0]}} -> {{c[1]}} (transition `t_{{c[2][:cut]}}`)](#transition-t_{{c[2][:cut]}})
{%endfor-%}



{%for c in correspondence_code_to_i%}
## transition t_{{c[:cut]}}

{%-for c in transitions[c]%}

### `{{c.message}}`

```
{{res_files[c.message]}}
```

{%-endfor%}
{%endfor%}
