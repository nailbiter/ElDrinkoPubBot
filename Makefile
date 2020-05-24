.PHONY: all


#procedures
all: $(addprefix .,$(addsuffix .flag.txt,DevElDrinkoPubBot ElDrinkoPubBot ProtoElDrinkoPubBot))

#main
.%.flag.txt: ./bin/upload_setting.py %.json
	./$^ --id id --password $(shell cat secret.txt) --connection_string 'mongodb+srv://nailbiter:{{password}}@cluster0-ta3pc.gcp.mongodb.net/test?retryWrites=true&w=majority' --collection '_settings'
	touch $@
