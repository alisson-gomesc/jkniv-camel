Title: troubleshooting JCoException

Troubleshooting
-------------
       
      
#### JCoException: (106) JCO_ERROR_RESOURCE: Destination does not exist

If you are using `jkniv-camel-sap-jco3-2.17.x` with multiples web application in same web container or server application instance (new mentions just web container).

    Message History
    -----------------------------------------------------------------------------------------------------------------------
    RouteId              ProcessorId          Processor                                                        Elapsed (ms)
    [hello-sapjco] [hello-sapjco-exec] [direct-vm://hello-sapjco-exec                                        ] [452]
    [hello-sapjco] [to26             ] [direct-vm:hello-sapjco-exec                                          ] [  4]
    [hello-sapjco] [setHeader5       ] [setHeader[SAPJco_I_BUKRS                                             ] [  0]
    [hello-sapjco] [setHeader6       ] [setHeader[SAPJco_I_DATE                                              ] [  0]
    [hello-sapjco] [to21             ] [jkniv-jco:sap-con?sapFunction=ZRFC_LISTPROD&sapDestName=XXXXX_ABAP_AS] [  1]
    Stacktrace
    -----------------------------------------------------------------------------------------------------------------------
    com.sap.conn.jco.JCoException: (106) JCO_ERROR_RESOURCE: Destination XXXXX_ABAP_AS_WITH_POOL does not exist
        at com.sap.conn.jco.rt.DefaultDestinationManager.update(DefaultDestinationManager.java:208)
        at com.sap.conn.jco.rt.DefaultDestinationManager.searchDestination(DefaultDestinationManager.java:374)
        at com.sap.conn.jco.rt.DefaultDestinationManager.getDestinationInstance(DefaultDestinationManager.java:89)
        at com.sap.conn.jco.JCoDestinationManager.getDestination(JCoDestinationManager.java:77)
        at net.sf.jkniv.camel.sap.jco3.SapJcoProducer.process(SapJcoProducer.java:56)
 
This message say the data provider cannot be find, because her not exist, probably `XXXXX_ABAP_AS_WITH_POOL` was not registered. 

The `jkniv-camel-sap-jco3-2.17.x` with multiples web applications deployed doesn't work under same web container because only one `DestinationDataProvider` can be registered per process and the class loaders from web container keep isolation between distinct webapps creating new instance from `DestinationDataProvider` in `net.sf.jkniv.camel.sap.jco3.SapJcoComponent`.

For solution that scenario it's needs upgrade to `jkniv-camel-sap-jco3-2.18.0` version and add the new jar `jkniv-sap-provider` for shared class path from web container, ex: `glassfish4\glassfish\domains\domain1\lib` for glassfish and `$CATALINA_HOME/lib`for tomcat, just `jkniv-sap-provider` needs for shared library. 

If there just one web application for web container to connect to SAP `jkniv-sap-provider` isn't necessary.
 
The folder structure looks like this:
 
    apache-tomcat/
         │
         ├─ bin/
         ├─ conf/
         ├─ ...
         ├─ lib/
         │   └─ jkniv-sap-provider.jar
         │
         ├─ webapps/
         │   │
         │   ├── webapp-serviceA/
         │   │    └─WEB-INF/
         │   │         └─lib/
         │   │            └─jkniv-camel-sap-jco3.jar
         │   │
         │   └── webapp-serviceB/
         │        └─WEB-INF/
         │             └─lib/
         │                └─jkniv-camel-sap-jco3.jar

         
### Unresolved case

DestinationDataProvider registered in the runtime environment allow only one DestinationDataProvider can be registered per process. 

That means when `webapp-serviceA` starts the DestinationDataProvider is registered and when `webapp-serviceB` starts he verify that DestinationDataProvider was registered and a new registry will not be made.

So if `webapp-serviceB` stops first that `webapp-serviceA` it will not be a problem because `webapp-serviceA` will continuous to working. But if `webapp-serviceA` stops first the DestinationDataProvider will be unregistered and `webapp-serviceB` will not work anymore, because without the registered DestinationDataProvider the component cannot to connect to SAP.

The workaround to resolve this it's a **manual procedure**, when the web applications starts in this order:

* 1) (active) `webapp-serviceA`  (process that made registry for DestinationDataProvider)
* 2) (active) `webapp-serviceB`
* 3) (active) `webapp-serviceC`
* 4) (active) `webapp-serviceD`

and the `webapp-serviceA` must be stopped, the another webapps `B`, `C` and `D` must be restarted manually.


*    (stopped)   `webapp-serviceA`  
* 1) (restarted) `webapp-serviceB`  (process that made registry for DestinationDataProvider)
* 2) (restarted) `webapp-serviceC`
* 3) (restarted) `webapp-serviceD`


