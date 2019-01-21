Title: jkniv jco component

Jco Component
--------------------

The **jkniv-jco:** component allows you to work with Sap Jco3 library.

This component uses the sap JCo product version 3.x from SAP AG to handling sap connection.

Maven users will need to add the following dependency to their pom.xml for this component:

    <dependency>
      <groupId>net.sf.jkniv</groupId>
      <artifactId>jkniv-camel-sap-jco3</artifactId>
      <version>2.19.0</version>
    </dependency>


#### NEWS

Short release notes.

##### version 2.19.0 released
 Release that supports JCoStructure as PARAMETERS INPUT.

##### version 2.18.0.RC1 released
 Release that supports several web applications using `jkniv-jco` in the same web container.

##### version 2.17.4 released
 There are a bug in version 2.17.3, it doesn't preserver the header message, this was fixed in version 2.17.4.

Prerequisites
--------------------

- JDK 1.8 or superior
- sap Jco 3 and dependencies (jar, dll/so libraries)

URI format
--------------------

From 2.17.3 could only act as a producer endpoint (e.g. `to()`).

The `jkniv-jco` component uses the following endpoint URI notation:

    jkniv-jco:propertiesFile[?options]


Options
--------------------


| Option       | Type   | Default | Description |
| ------------ | -------|---------|-------------|
| sapFunction  | String |         |  JCo function name |
| sapDestName  | String |         | Set the name of SAP JCoDestination name that identifies a physical destination of a function call |
| sapJcoTable  | String |         | The name of SAP JCoTable that encapsulates a output data table |
| sapJcoTableIn| String |         | The name of SAP JCoTable that encapsulates a input table parameters (*since 2.19.0 version*)|
| prefixParams | String | SAPJco_ | The prefix name of parameters,  default is `SAPJco_`, the prefix value is used when the parameters came from header message.|
| parserResultStrategy  | String |  | Allows to plugin to use a custom `net.sf.jkniv.camel.sap.jco3.ParserResult` that extract the returned values from SAP JCo function |
| useHeaderAsParam | boolean | false | indicate to lookup the parameters from JCo at header message, default is the body message. When your value is `true` the option `prefixParams` is mandatory, cannot be empty or null. |
| supportsNull | boolean | false | Indicate to component to keep the **null** rows from SAP, default behavior skip the **null** rows |


Result
--------------------

By default the result is returned in the OUT body as an `ArrayList<HashMap<String, Object>>`. The List object contains the list of rows and the Map objects contain each row with the String key as the column name. This version doesn't supports outputClass to specify a bean like POJO to change the return object or list of objects.


Installing Sap Java Connector
--------------------


You can download the [SAP JCo 3.x] (http://service.sap.com/connectors).
The SAP JCo installation files are composed for:

| Windows     | Linux     |
| ----------- | --------- |
| sapjco3.jar | sapjco3.jar |
| sapjco3.dll | libsapjco3.so |


After obtaining the SAP JCo you must follow the SAP manual to configure the `dll` or `so` 
library and put the `sapjco3.jar` at classpath from your application.

Read the manual [Components of SAP Communication Technology](https://help.sap.com/saphelp_nwpi711/helpdata/en/48/707c54872c1b5ae10000000a42189c/frameset.htm) to configure the libraries from SAP client.


Camel+Spring Configuration
--------------------

This is a snippet code, from Spring bean, configuring the properties values to connect at SAP Server:

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"...
    
    <bean id="sap-conn" class="org.springframework.beans.factory.config.PropertiesFactoryBean">
      <property name="properties">
        <props>
          <prop key="jco.client.ashost">mysaphost</prop>
          <prop key="jco.client.user">username</prop>
          <prop key="jco.client.passwd">secret</prop>
          <prop key="jco.client.client">600</prop>
          <prop key="jco.client.lang">en</prop>
          <prop key="jco.destination.pool_capacity">3</prop>
          <prop key="jco.destination.peak_limit">10</prop>
          <prop key="jco.client.sysnr">00</prop>
        </props>
      </property>
    </bean>


JNDI Configuration
--------------------

If you are using Application Server or Web Container it's possible configure the properties file as JNDI resource. This example show the xml snippet to configure the JNDI with `sap-conn` name at Glassfish server. 

    <resources>
      ...
      <custom-resource factory-class="org.glassfish.resources.custom.factory.PropertiesFactory" description="Properties to connect SAP enviroment" res-type="java.util.Properties" jndi-name="sap-conn">
        <property name="jco.client.ashost" value="mysaphost"></property>
        <property name="jco.client.user" value="username"></property>
        <property name="jco.client.passwd" value="secret"></property>
        <property name="jco.client.client" value="600"></property>
        <property name="jco.client.lang" value="en"></property>
        <property name="jco.destination.pool_capacity" value="3"></property>
        <property name="jco.destination.peak_limit" value="10"></property>
        <property name="jco.client.sysnr" value="00"></property>
      </custom-resource>
    </resources>

The configuration file it's from Glassfish is `SystemDrive:\glassfish4\glassfish\domains\domain1\config\domain.xml`.


Sample DSL XML
--------------------

In the following example, we using the connection `sap-conn` to connect to SAP server and invoke the SAP function `ZSEARCH_VEHICLES` with destination name `SAPCON_ABAP_AS_WITH_POOL` parsing the values from `T_DATA` data structure. 

    <to uri="jkniv-jco:sap-conn?sapFunction=ZSEARCH_VEHICLES&sapDestName=SAPCON_ABAP_AS_WITH_POOL&sapJcoTable=T_DATA" />
    
The jkniv-jco component first try lookup the properties file using JNDI, if it isn't found try to lookup using bean properties from Spring using `CamelContextHelper.mandatoryLookup(...)`.

    
#### Lookup Parameters

`jkniv-jco` must lookup the parameters from the message body or message headers. The first option it's a Map from body (`exchange.getIn().getBody(Map.class)`) and all keys are sent as parameters to jco component. 

To lookup the parameters from message header the option `useHeaderAsParam` must be set to `true`. All parameters names start with `SAPJco_` are sent to jco component. The prefix can be changed setting the option `prefixParams`.
 
    ...
    <camel:setHeader headerName="SAPJco_I_BUKRS">
      <simple resultType="java.lang.String">0200</simple>
    </camel:setHeader>
    <to uri="jkniv-jco:sap-conn?sapFunction=ZSEARCH_VEHICLES&sapDestName=SAPCON_ABAP_AS_WITH_POOL&sapJcoTable=T_DATA&useHeaderAsParam=true" />
    ...
    

#### Other example

    <camel:route id="hello-sap-world-jndi">
      <camel:description>Sample to get data from SAP using connection properties with jndi</camel:description>
      <from uri="servlet:///hello-sap-world-jndi" />
      <log message="Request to SAP world" loggingLevel="INFO" />
      <to uri="jkniv-jco:sap-conn?sapFunction=ZSEARCH_VEHICLES&sapDestName=SAPCON_ABAP_AS_WITH_POOL&sapJcoTable=T_DATA" />
      <to uri="mock:result" />
    </camel:route>
    
    
    
#### Example using sapJcoTableIn data

RFC data structure:

    Tables:
    +-------------+-------+
    | PARAMETERS 'TABLES' |
    +-------------+-------+
    |TBL_FAT_ITENS|TBL_MSG|
    +-------------+-------+
        
    [ZFIT_FAT_HEADER
    MANDT                           ,MANDT                           ,C,3,0,3,0,6,0,0,Principal
    MONAT                           ,MONAT                           ,N,2,3,2,3,4,6,0,Ref. month
    GJAHR                           ,GJAHR                           ,N,4,5,4,5,8,10,0,Reference
    DTSTAR                          ,                                ,D,8,9,8,9,16,18,0,Start date
    DTEND                           ,                                ,D,8,17,8,17,16,34,0,End date
    TOTREC                          ,                                ,P,2,25,4,25,4,50,0,total of records
    DAYS_OFF                        ,                                ,P,2,27,4,29,4,54,0,Days off
    TOT_DICOUNT                     ,                                ,P,4,29,8,33,8,58,2,Total discount
    Record length: 41,66
    ]
    
    ------------------------------------------------------------------------------------
    |---|--|----|--------|--------|--------|--------|----------------|
    | STRUCTURE 'ZFIT_FAT_HEADER'
    |---|--|----|--------|--------|--------|--------|----------------|
    |MAN|MO|GJAH|DTSTAR  |DTEND   |TOTREC  |DAYS_OFF|TOT_DICOUNT     |
    |---|--|----|--------|--------|--------|--------|----------------|
    |012|34|5678|90123456|78901234|   5   6|   7   8|   9   0   1   2|
    |---|--|----|--------|--------|--------|--------|----------------|
    |   |00|0000|00000000|00000000|0000000C|0000000C|000000000000000C|
    |---|--|----|--------|--------|--------|--------|----------------|
    
    | TABLE 'ZFIT_FAT_SERVICE'
    +---+-------+----+------+------+------+------+--------+--------+--------+------------+------+
    |MAN|KUNNR  |GJAH|DTSTAR|DTEND |PLATE |SERIAL|SERVICE |DAYS_OFF|DEFLATOR|TOT_DICOUNT |TOTAL |
    +---+-------+----+------+------+------+------+--------+--------+--------+------------+------+

The parameter `sapJcoTableIn` is a `java.util.Map` input.

    /**
     * Generate input data for SAP RFC finance
     */
    public class BillingProcessor implements Processor
    {    
        public BillingProcessor()
        {
        }
        
        /**
         * Build input SAP RFC Finance from BillingReport data
         */
        @Override
        public void process(Exchange exchange)
        {
            BillingReport data = exchange.getIn().getBody(BillingReport.class);
            List<BillingReportRow> details = data.getDetails();
            Map<String, Object> iFatHeader = toIFatHeader(data, details);        
            List<Map<String, Object>> tblFatItens = new ArrayList<>();
            for (BillingReportRow item : details)
            {
                Map<String, Object> tblFatItem = toTblFatItem(item);
                tblFatItens.add(tblFatItem);            
            }
            iFatHeader.put("TBL_FAT_ITENS", tblFatItens);
            
            exchange.getIn().setBody(iFatHeader);
        }
        
        private Map<String, Object> toIFatHeader(BillingReport data, List<BillingReportRow> details)
        {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Map<String, Object> iFatHeader = new HashMap<>();
            iFatHeader.put("DTSTAR", sdf.format(data.getStart()));
            iFatHeader.put("DTEND", sdf.format(data.getEnd()));
            iFatHeader.put("TOTREC", details != null ? details.size() : 0);
            iFatHeader.put("DAYS_OFF", data.getQtdUnTransmissionDays());
            iFatHeader.put("TOT_DICOUNT", data.getDeflatorTotal());
            return iFatHeader;
        }
        
        private Map<String, Object> toTblFatItem(BillingReportRow item)
        {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Map<String, Object> tblFatItem = new HashMap<>();
            tblFatItem.put("DTSTAR", sdf.format(item.getStartBillingDate()));
            tblFatItem.put("DTEND", sdf.format(item.getEndBillingDate()));
            tblFatItem.put("PLATE", item.getPlate());
            tblFatItem.put("SERIAL", item.getSerial());
            tblFatItem.put("SERVICE", item.getServices());
            tblFatItem.put("DAYS_OFF", item.getQtdUnTransmissionDays());
            tblFatItem.put("DEFLATOR", item.getDeflator());
            tblFatItem.put("TOT_DICOUNT", item.getDeflatorTotal());
            tblFatItem.put("TOTAL", item.getTransmissionDays());
            return tblFatItem;
        }
    }
    
Camel route to send data for SAP RFC.

    <camel:route id="billing-send">
      <camel:description>Billing process</camel:description>
      <from uri="direct:billing-send" />
        <camel:process ref="billingProcessor" />
        <log message="Sending data to SAP" loggingLevel="DEBUG" logName="billing" />
        <to uri="jkniv-jco:sap-conn?sapFunction=ZRFC_FINANCE&amp;sapDestName=AS_WITH_POOL&amp;sapJcoTable=TBL_MSG&amp;sapJcoTableIn=TBL_FAT_ITENS" />
        <log message="Save BILLING-RESPONSE in MQ to send database: ${body}" loggingLevel="DEBUG" logName="billing" />      
    </camel:route>