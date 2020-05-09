#!/bin/sh

name=`openssl rand -hex 12|tr -d '\n'`
echo $name|tr -d '\n'|pbcopy
touch ../resources/$name.txt
