REM autore Mirco Taddei <m.taddei@ittig.cnr.it>

set CLASSPATH=.;nireditor.jar;lib/activation.jar
set CLASSPATH=%CLASSPATH%;lib/dom4j-1.5.2.jar
set CLASSPATH=%CLASSPATH%;lib/formsrt.jar
set CLASSPATH=%CLASSPATH%;lib/jazzy-core.jar
set CLASSPATH=%CLASSPATH%;lib/jaxen-1.1-beta-4.jar
set CLASSPATH=%CLASSPATH%;lib/log4j-1.2.9.jar
set CLASSPATH=%CLASSPATH%;lib/mail.jar
set CLASSPATH=%CLASSPATH%;lib/plastic-1.2.0.jar
set CLASSPATH=%CLASSPATH%;lib/xercesImpl-2.7.1.jar
set CLASSPATH=%CLASSPATH%;lib/xml-apis-2.7.1.jar
set CLASSPATH=%CLASSPATH%;lib/fop.jar
set CLASSPATH=%CLASSPATH%;lib/batik.jar
set CLASSPATH=%CLASSPATH%;lib/avalon-framework-cvs-20020806.jar

jre\bin\java -Xmx512m it.cnr.ittig.services.manager.Run
