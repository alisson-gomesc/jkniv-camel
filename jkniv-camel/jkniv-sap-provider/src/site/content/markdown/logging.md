Title: Logging

Logging
-------------
       
`jkniv-camel-sap-jco3` provides logging information through the use of Simple Logging Facade for Java or (SLF4J) serves as a simple facade or abstraction for various logging frameworks, e.g. java.util.logging, log4j and logback, allowing the end user to plug in the desired logging framework at deployment time. 

<a href="http://www.slf4j.org/">More information.</a>


### Sample XML log4j config

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
    <log4j:configuration debug="true" xmlns:log4j='http://jakarta.apache.org/log4j/'>

     <appender name="console" class="org.apache.log4j.ConsoleAppender">
      <layout class="org.apache.log4j.PatternLayout">
       <param name="ConversionPattern" value="%d{YYYY-MM-dd HH:mm:ss.SSS} [%t] [%-5p] %C.%M - %m%n" />
      </layout>
     </appender>

     <logger name="net.sf.jkniv.camel.sap.jco3" additivity="false">
      <level value="debug"/> 
      <appender-ref ref="console" />
     </logger>  
  
     <root>
      <level value="info" />
      <appender-ref ref="console" />
     </root>
    </log4j:configuration>
