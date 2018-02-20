# JKNIV JCO3 for Sap with Apache Camel and Spring


### Introduction
An example which shows how to use the jkniv to connect to SAP.

### Requirements

    <dependency>
      <groupId>org.springframework</groupId>
      <artifactId>spring-web</artifactId>
      <version>4.1.9.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.apache.camel</groupId>
      <artifactId>camel-servletlistener</artifactId>
      <version>2.18.5</version>
    </dependency>
    <dependency>
      <groupId>org.apache.camel</groupId>
      <artifactId>camel-servlet</artifactId>
      <version>2.18.5</version>
    </dependency>


### Configure
To configure the example to connect at your environment you change some properties 
from file `jkniv-example-camel-sap-web\src\main\resources\camel-config.xml`.

The bean spring `bean-prop` keep the parameters to connect at SAP, change the values
to reflect your environment.

    <bean id="bean-prop" class="org.springframework.beans.factory.config.PropertiesFactoryBean">
      <property name="properties">
        <props>
          <prop key="jco.client.ashost">127.0.0.1</prop>
          <prop key="jco.client.sysnr">00</prop>
          <prop key="jco.client.client">600</prop>
          <prop key="jco.client.user">user</prop>
          <prop key="jco.client.passwd">secret</prop>
          <prop key="jco.client.lang">en</prop>
          <prop key="jco.destination.pool_capacity">3</prop>
          <prop key="jco.destination.peak_limit">10</prop>
        </props>
      </property>
    </bean>


Change the parameters from SAP RFC at camel route to use the parameters from your environment:
`sapFunction`, `sapDestName`, `sapJcoTable`


      <to uri="jkniv-jco:bean-prop?sapFunction=ZARQ_BUSCA_VEICULOS&amp;sapDestName=VEIC_ABAP_AS_WITH_POOL&amp;sapJcoTable=T_DADOS" />


### Build
You will need to package this example first:

	mvn package

### Run

To run the example deploy it in Apache Tomcat by copying the `.war` to the
deploy folder of Apache Tomcat.

And then hit this url from a web browser which has further
instructions

	http://localhost:8080/jkniv-sap-web/
<http://localhost:8080/jkniv-sap-web/>

To check if the Camel+Spring are working The servlet is located at

	http://localhost:8080/jkniv-sap-web/camel/hello
<http://localhost:8080/jkniv-sap-web/camel/hello>


### Run SAP example

To run the example that connect to SAP enviroment hit this url

    http://localhost:8080/jkniv-sap-web/camel/hello-sap-world-bean
<http://localhost:8080/jkniv-sap-web/camel/hello-sap-world-bean>


### Forum, Help, etc

mail me alisson.gomesc at gmail com

We appreciate any feedback you may have.  Enjoy!
