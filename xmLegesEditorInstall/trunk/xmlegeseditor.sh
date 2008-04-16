#!/bin/sh

cd `dirname $0`

XMLEGESEDITOR_HOME=$PWD

export XMLEGESEDITOR_HOME
echo $XMLEGESEDITOR_HOME

cd $XMLEGESEDITOR_HOME/ 

export CLASSPATH=.:xmLegesCoreApi.jar:lib/activation.jar
export CLASSPATH=$CLASSPATH:xmLegesCoreImpl.jar
export CLASSPATH=$CLASSPATH:xmLegesEditorApi.jar
export CLASSPATH=$CLASSPATH:xmLegesEditorImpl.jar
export CLASSPATH=$CLASSPATH:lib/dom4j-1.5.2.jar
export CLASSPATH=$CLASSPATH:lib/formsrt.jar
export CLASSPATH=$CLASSPATH:lib/jazzy-core.jar
export CLASSPATH=$CLASSPATH:lib/jaxen-1.1-beta-4.jar
export CLASSPATH=$CLASSPATH:lib/log4j-1.2.9.jar
export CLASSPATH=$CLASSPATH:lib/mail.jar
export CLASSPATH=$CLASSPATH:lib/plastic-1.2.0.jar
export CLASSPATH=$CLASSPATH:lib/xercesImpl-2.7.1.jar
export CLASSPATH=$CLASSPATH:lib/xml-apis-2.7.1.jar
export CLASSPATH=$CLASSPATH:lib/fop.jar
export CLASSPATH=$CLASSPATH:lib/commons-logging-1.0.4.jar
export CLASSPATH=$CLASSPATH:lib/xmlgraphics-commons-1.1.jar
export CLASSPATH=$CLASSPATH:lib/commons-io-1.1.jar
export CLASSPATH=$CLASSPATH:lib/batik.jar
export CLASSPATH=$CLASSPATH:lib/avalon-framework-cvs-20020806.jar
export CLASSPATH=$CLASSPATH:lib/logkit-1.0.1.jar
export CLASSPATH=$CLASSPATH:lib/commons-codec-1.3.jar
export CLASSPATH=$CLASSPATH:lib/commons-logging-1.1.jar
export CLASSPATH=$CLASSPATH:lib/commons-logging-adapters-1.1.jar
export CLASSPATH=$CLASSPATH:lib/commons-logging-api-1.1.jar
export CLASSPATH=$CLASSPATH:lib/xsc.jar
export CLASSPATH=$CLASSPATH:lib/jdic/jdic.jar
export CLASSPATH=$CLASSPATH:lib/commons-httpclient.jar
export CLASSPATH=$CLASSPATH:lib/jakarta-slide-webdavlib-2.1.jar
export CLASSPATH=$CLASSPATH:lib/jdom-1.0.jar

export CLASSPATH=$CLASSPATH:lib/FontBox-0.1.0-dev.jar
export CLASSPATH=$CLASSPATH:lib/PDFBox-0.7.3.jar

export LC_ALL=it_IT@euro
export LANG=it_IT@euro

chmod +x jre/bin/*
jre/bin/java -Xmx256m it.cnr.ittig.services.manager.Run xmLegesEditor.xml /images/editor/xmLegesEditor.png
