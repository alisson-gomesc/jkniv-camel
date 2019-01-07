Title: Verify SAP installation

Verifying SAP Installation
-------------



[root@sapserve ~]# scp /opt/sap/sapjco3.jar /opt/sap/libsapjco3.so root@myhome:/opt/sap


Adding {sapjco3-install-path} to the `LD_LIBRARY_PATH` environment variable:

    [root@myhome ~]# mkdir /opt/sap
    [root@sapserve ~]# scp sapjco3.jar libsapjco3.so root@myhome:/opt/sap
      root@myhome's password:
      sapjco3.jar                                                                     100% 1344KB   1.3MB/s   00:00
      libsapjco3.so                                                                   100% 5201KB   5.1MB/s   00:00
    [root@myhome ~]# vim /etc/profile.d/sap.sh

Content `sap.sh` file 

    export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/sap




### Falied SAP Installation

    [root@myhome ~]# java -jar lib/sapjco3.jar -stdout
    java.lang.UnsatisfiedLinkError: no sapjco3 in java.library.path
            at java.lang.ClassLoader.loadLibrary(ClassLoader.java:1867)
            at java.lang.Runtime.loadLibrary0(Runtime.java:870)
            at java.lang.System.loadLibrary(System.java:1122)
            at com.sap.conn.jco.rt.DefaultJCoRuntime.loadJCoLibrary(DefaultJCoRuntime.java:772)
            at com.sap.conn.jco.rt.DefaultJCoRuntime.registerNativeMethods(DefaultJCoRuntime.java:382)
            at com.sap.conn.jco.rt.JCoRuntime.registerNatives(JCoRuntime.java:1200)
            at com.sap.conn.rfc.driver.CpicDriver.<clinit>(CpicDriver.java:792)
            at com.sap.conn.rfc.engine.DefaultRfcRuntime.getVersion(DefaultRfcRuntime.java:36)
            at com.sap.conn.rfc.api.RfcApi.RfcGetVersion(RfcApi.java:238)
            at com.sap.conn.jco.rt.MiddlewareJavaRfc.<clinit>(MiddlewareJavaRfc.java:217)
            at com.sap.conn.jco.rt.DefaultJCoRuntime.initialize(DefaultJCoRuntime.java:98)
            at com.sap.conn.jco.rt.JCoRuntimeFactory.<clinit>(JCoRuntimeFactory.java:23)
            at com.sap.conn.jco.rt.About.<init>(About.java:42)
            at com.sap.conn.jco.rt.About.main(About.java:81)
    java.lang.ExceptionInInitializerError: JCo initialization failed with java.lang.UnsatisfiedLinkError: no sapjco3 in java.library.path
            at com.sap.conn.jco.rt.MiddlewareJavaRfc.<clinit>(MiddlewareJavaRfc.java:229)
            at com.sap.conn.jco.rt.DefaultJCoRuntime.initialize(DefaultJCoRuntime.java:98)
            at com.sap.conn.jco.rt.JCoRuntimeFactory.<clinit>(JCoRuntimeFactory.java:23)
            at com.sap.conn.jco.rt.About.<init>(About.java:42)
            at com.sap.conn.jco.rt.About.main(About.java:81)
    
    --------------------------------------------------------------------------------------
    |                                 SAP Java Connector                                 |
    |                Copyright (c) 2000-2014 SAP AG. All rights reserved.                |
    |                                Version Information                                 |
    --------------------------------------------------------------------------------------
    Java Runtime:
     Operating System:       Linux 3.10.0-862.2.3.el7.x86_64 for amd64
     Java VM:                1.8.0_171 Oracle Corporation
     Default charset:        UTF-8
    Versions:
     JCo API:                3.0.11 (2014-04-15)
     JCo middleware:         unknown
     JCo library:            unknown
    Library Paths:
     Path to JCo archive:    /opt/tomcat9/lib/sapjco3.jar
     Path to JCo library:    not found
    Initialization:
     JCo error:              java.lang.ExceptionInInitializerError: JCo initialization failed with java.lang.UnsatisfiedLinkError: no sapjco3 in java.library.path
            at com.sap.conn.jco.rt.MiddlewareJavaRfc.<clinit>(MiddlewareJavaRfc.java:229)
            at com.sap.conn.jco.rt.DefaultJCoRuntime.initialize(DefaultJCoRuntime.java:98)
            at com.sap.conn.jco.rt.JCoRuntimeFactory.<clinit>(JCoRuntimeFactory.java:23)
            at com.sap.conn.jco.rt.About.<init>(About.java:42)
            at com.sap.conn.jco.rt.About.main(About.java:81)
    
    --------------------------------------------------------------------------------------
    |                                      Manifest                                      |
    --------------------------------------------------------------------------------------
    Manifest-Version: 1.0
    Ant-Version: Apache Ant 1.6.4
    Created-By: 5.1.028 (SAP AG)
    Specification-Title: SAP Java Connector v3
    Specification-Version: 3.0.11
    Specification-Vendor: SAP AG, Walldorf
    Implementation-Title: com.sap.conn.jco
    Implementation-Version: 20140415 1946 [3.0.11 (2014-04-15)]
    Implementation-Vendor-Id: com.sap
    Implementation-Vendor: SAP AG, Walldorf
    Main-Class: com.sap.conn.jco.rt.About
    --------------------------------------------------------------------------------------


### Successfully SAP Installation

    [root@myhome tomcat9]# java -jar lib/sapjco3.jar -stdout
    --------------------------------------------------------------------------------------
    |                                 SAP Java Connector                                 |
    |                Copyright (c) 2000-2014 SAP AG. All rights reserved.                |
    |                                Version Information                                 |
    --------------------------------------------------------------------------------------
    Java Runtime:
     Operating System:       Linux 2.6.32-696.28.1.el6.x86_64 for amd64
     Java VM:                1.8.0_171 Oracle Corporation
     Default charset:        UTF-8
    Versions:
     JCo API:                3.0.11 (2014-04-15)
     JCo middleware:         JavaRfc 2.2.12
     JCo library:            720.612
    Library Paths:
     Path to JCo archive:    /opt/tomcat9/lib/sapjco3.jar
     Path to JCo library:    /opt/sap/libsapjco3.so
    --------------------------------------------------------------------------------------
    |                                      Manifest                                      |
    --------------------------------------------------------------------------------------
    Manifest-Version: 1.0
    Ant-Version: Apache Ant 1.6.4
    Created-By: 5.1.028 (SAP AG)
    Specification-Title: SAP Java Connector v3
    Specification-Version: 3.0.11
    Specification-Vendor: SAP AG, Walldorf
    Implementation-Title: com.sap.conn.jco
    Implementation-Version: 20140415 1946 [3.0.11 (2014-04-15)]
    Implementation-Vendor-Id: com.sap
    Implementation-Vendor: SAP AG, Walldorf
    Main-Class: com.sap.conn.jco.rt.About
    --------------------------------------------------------------------------------------

