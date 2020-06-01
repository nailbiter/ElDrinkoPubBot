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
transitions.json: ./bin/process_transitions.py ../src/main/resources/transitions.json
	python3 $^ > $@
%.gv: templates/%.jinja.gv transitions.json
	jinja2 $^ > $@
%.md: templates/%.jinja.md transitions.json
	jinja2 $< transitions.json > $@
