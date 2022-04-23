echo off
set classes=bin
set lib=lib/server
set classes=%classes%;%lib%/antlr-2.7.6.jar
set classes=%classes%;%lib%/asm.jar
set classes=%classes%;%lib%/asm-attrs.jar
set classes=%classes%;%lib%/cglib-2.1.3.jar
set classes=%classes%;%lib%/commons-collections.jar
set classes=%classes%;%lib%/commons-pool-1.3.jar
set classes=%classes%;%lib%/hibernate3.jar
set classes=%classes%;%lib%/jta.jar
set classes=%classes%;%lib%/mysql-connector-java-5.0.7-bin.jar
set lib=lib/common
set classes=%classes%;%lib%/activation.jar
set classes=%classes%;%lib%/commons-logging.jar
set classes=%classes%;%lib%/dom4j-1.6.1.jar
set classes=%classes%;%lib%/log4j-1.2.8.jar
set classes=%classes%;%lib%/mail.jar

rem echo %classes%

java -cp %classes% starcorp.server.shell.Shell