#!/bin/sh

cd `dirname $0`

XMLEGESEDITOR_HOME=$PWD

export XMLEGESEDITOR_HOME
echo $XMLEGESEDITOR_HOME

java -jar $XMLEGESEDITOR_HOME/Uninstaller/uninstaller.jar