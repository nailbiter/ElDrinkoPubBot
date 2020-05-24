.PHONY: all


#procedures
all: README.html

#main
README.md: states.svg
%.html: %.md
	pandoc -s --css=$(HOME)/for/misc/formarkdown.css  -V lang=en $<  -o $@
%.svg: %.gv
	dot -Tsvg $< > $@
