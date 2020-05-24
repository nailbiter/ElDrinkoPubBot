.PHONY: all
.INTERMEDIATE: states.gv


#procedures
all: README.html

#main
README.md: states.svg
%.html: %.md
	pandoc -s --css=$(HOME)/for/misc/formarkdown.css  -V lang=en $<  -o $@
%.svg: %.gv
	dot -Tsvg $< > $@
states.gv: bin/states_json_to_states_gv.py states.json
	./$^ > $@
README.md: bin/states_json_to_readme_md.py states.json
	./$^ > $@
