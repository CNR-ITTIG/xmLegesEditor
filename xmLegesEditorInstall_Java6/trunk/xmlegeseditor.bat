REM autore Mirco Taddei <m.taddei@ittig.cnr.it>

echo off
set CLASSPATH=.;xmLegesCoreApi.jar;lib/activation.jar
set CLASSPATH=%CLASSPATH%;xmLegesCoreImpl.jar
set CLASSPATH=%CLASSPATH%;xmLegesEditorApi.jar
set CLASSPATH=%CLASSPATH%;xmLegesEditorImpl.jar
set CLASSPATH=%CLASSPATH%;lib/dom4j-1.5.2.jar
set CLASSPATH=%CLASSPATH%;lib/formsrt.jar
set CLASSPATH=%CLASSPATH%;lib/jaxen-1.1-beta-4.jar
set CLASSPATH=%CLASSPATH%;lib/log4j-1.2.9.jar
set CLASSPATH=%CLASSPATH%;lib/mail.jar
set CLASSPATH=%CLASSPATH%;lib/plastic-1.2.0.jar

set CLASSPATH=%CLASSPATH%;lib/xerces-2_9_1/serializer.jar
set CLASSPATH=%CLASSPATH%;lib/xerces-2_9_1/xercesImpl.jar
set CLASSPATH=%CLASSPATH%;lib/xerces-2_9_1/xml-apis.jar

set CLASSPATH=%CLASSPATH%;lib/xalan.jar
set CLASSPATH=%CLASSPATH%;lib/xsltc.jar


set CLASSPATH=%CLASSPATH%;lib/fop.jar
set CLASSPATH=%CLASSPATH%;lib/commons-logging-1.0.4.jar
set CLASSPATH=%CLASSPATH%;lib/xmlgraphics-commons-1.1.jar
set CLASSPATH=%CLASSPATH%;lib/commons-io-1.1.jar
set CLASSPATH=%CLASSPATH%;lib/batik.jar
set CLASSPATH=%CLASSPATH%;lib/avalon-framework-cvs-20020806.jar
set CLASSPATH=%CLASSPATH%;lib/logkit-1.0.1.jar
set CLASSPATH=%CLASSPATH%;lib/commons-codec-1.3.jar
set CLASSPATH=%CLASSPATH%;lib/commons-logging-1.1.jar
set CLASSPATH=%CLASSPATH%;lib/commons-logging-adapters-1.1.jar
set CLASSPATH=%CLASSPATH%;lib/commons-logging-api-1.1.jar
set CLASSPATH=%CLASSPATH%;lib/jdic/jdic.jar
set CLASSPATH=%CLASSPATH%;lib/commons-httpclient.jar
set CLASSPATH=%CLASSPATH%;lib/jakarta-slide-webdavlib-2.1.jar
set CLASSPATH=%CLASSPATH%;lib/jdom-1.0.jar


set CLASSPATH=%CLASSPATH%;lib/FontBox-0.1.0-dev.jar
set CLASSPATH=%CLASSPATH%;lib/jmyspell-core-1.0.0-beta-2.jar
set CLASSPATH=%CLASSPATH%;lib/jmyspell-swing-1.0.0-beta-2.jar
set CLASSPATH=%CLASSPATH%;lib/eclipse-xsd-2_4/org.eclipse.emf.common_2.4.0.v200804012208.jar
set CLASSPATH=%CLASSPATH%;lib/eclipse-xsd-2_4/org.eclipse.emf.ecore_2.4.0.v200804012208.jar
set CLASSPATH=%CLASSPATH%;lib/eclipse-xsd-2_4/org.eclipse.xsd_2.4.0.v200804012208.jar
set CLASSPATH=%CLASSPATH%;lib/PDFBox-0.7.3.jar

start /MIN jre\bin\javaw -Xmx256m it.cnr.ittig.services.manager.Run xmLegesEditor.xml images\editor\xmLegesEditor.png
