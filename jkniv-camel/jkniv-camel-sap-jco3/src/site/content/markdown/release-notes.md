Title: Release Notes

Release Notes
-------------

Welcome to the `2.18.0.RC1` release that supports several web applications using `jkniv-jco` in the same web container. 
   
       
#### jkniv-camel-sap-jco3-2.18.0.RC1
 - Add supports to deploy many web applications at one web container or server application.
 - Fix update properties for destination name provider for each create end point.
 - Component extending `DefaultComponent` instead `UriEndpointComponent` that are deprecated.
 
#### jkniv-camel-sap-jco3-2.17.4
 - preserver the header message after call `<to uri="jkniv-jco:...`.

#### jkniv-camel-sap-jco3-2.17.3
 - First release.


#### Getting the Distributions

| Description | Link Downloads |
|-------------|---------------|
|Binary Distribution|[jkniv-camel-sap-jco3-2.18.0.RC1.jar](https://sourceforge.net/projects/jkniv/files/jkniv-camel/jkniv-jco/jkniv-camel-sap-jco3-2.18.0.RC1.jar/download)|
|Source Distributions|[jkniv-camel-sap-jco3-2.18.0.RC1-sources.jar](https://sourceforge.net/projects/jkniv/files/jkniv-camel/jkniv-jco/jkniv-camel-sap-jco3-2.18.0.RC1-sources.jar/download)|
|Java Doc|[jkniv-camel-sap-jco3-2.18.0.RC1-javadoc.jar](https://sourceforge.net/projects/jkniv/files/jkniv-camel/jkniv-jco/jkniv-camel-sap-jco3-2.18.0.RC1-javadoc.jar/download)|


#### Getting the Binaries using Maven

    <dependency>
      <groupId>net.sf.jkniv</groupId>
      <artifactId>jkniv-camel-sap-jco3</artifactId>
      <version>2.18.0.RC1</version>
    </dependency>


For deploy many web application in the same web container or application server copy the jar file for the shared folder.
    
    <dependency>
      <groupId>net.sf.jkniv</groupId>
      <artifactId>jkniv-sap-provider</artifactId>
      <version>2.18.0.RC1</version>
      <scope>provided</scope>
    </dependency>
    
    