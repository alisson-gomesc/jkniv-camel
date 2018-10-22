/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.jkniv.camel.sap.jco3;

import java.util.Properties;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

/**
 * Represents a sap jco endpoint.
 */
@UriEndpoint(scheme = "jkniv-jco", title = "jkniv-jco", syntax = "jkniv-jco:name", label = "jkniv-jco")
public class SapJcoEndpoint extends DefaultEndpoint
{
    @UriPath
    @Metadata(required = "true")
    private String     name;
    
    @UriParam(defaultValue = "")
    private String     sapFunction          = "";
    
    @UriParam(defaultValue = "")
    private String     sapDestName          = "";
    
    @UriParam(defaultValue = "")
    private String     sapJcoTable          = "";

    @UriParam(defaultValue = "")
    private String     sapJcoTableIn          = "";

    @UriParam(defaultValue = "SAPJco_")
    private String     prefixParams         = "SAPJco_";
    
    @UriParam(defaultValue = "net.sf.jkniv.camel.sap.jco3.MapParserResult")
    private String     parserResultStrategy = "net.sf.jkniv.camel.sap.jco3.MapParserResult";
    
    @UriParam(defaultValue = "false", description="The parameters stay at header from Message, default is false")
    private boolean    useHeaderAsParam     = false;

    @UriParam(defaultValue = "false", description="Result can be contains null values, default is false")
    private boolean    supportsNull = false;

    private Class     classToParserResultStrategy;
    private Properties propSapConnection;
    
    public SapJcoEndpoint()
    {
    }
    
    public SapJcoEndpoint(String uri, SapJcoComponent component)
    {
        super(uri, component);
    }
    
    public SapJcoEndpoint(String endpointUri)
    {
        super(endpointUri);
    }
    
    public Producer createProducer() throws Exception
    {
        return new SapJcoProducer(this);
    }
    
    /*
    public Consumer createConsumer(Processor processor) throws Exception {
        return new SapJcoConsumer(this, processor);
    }
    */
    public Consumer createConsumer(Processor processor) throws Exception
    {
        throw new UnsupportedOperationException("Not supported operation createConsumer");
    }
    
    public boolean isSingleton()
    {
        return true;
    }
    
    /**
     * Some description of this option, and what it does
     * @param name endpoint
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    /**
     * The name of SAP <code>JCoFunction</code>
     * @return the name of function
     * @see com.sap.conn.jco.JCoFunction
     */
    public String getSapFunction()
    {
        return sapFunction;
    }
    
    /**
     * Define the name of SAP <code>JCoFunction</code>
     * @param sapFunction name of SAP function
     */
    public void setSapFunction(String sapFunction)
    {
        this.sapFunction = sapFunction;
    }
    
    /**
     * The name of SAP <code>JCoDestination</code> name that identifies a physical destination of a function call. 
     * It contains all required properties in order to connect to an SAP system. 
     * @return the name of <code>JCoDestination</code>
     * @see com.sap.conn.jco.JCoDestination
     */
    public String getSapDestName()
    {
        return sapDestName;
    }
    
    /**
     * Set the name of SAP <code>JCoDestination</code> name that identifies a physical destination of a function call
     * @param sapDestName the name of <code>JCoDestination</code>
     */
    public void setSapDestName(String sapDestName)
    {
        this.sapDestName = sapDestName;
    }
    
    /**
     * The name of SAP <code>JCoTable</code> that encapsulates a database table 
     * @return the name of SAP <code>JCoTable</code> .
     * @see com.sap.conn.jco.JCoTable
     */
    public String getSapJcoTable()
    {
        return sapJcoTable;
    }
    
    /**
     * The name of SAP <code>JCoTable</code> 
     * @param sapJcoTable name of SAP <code>JCoTable</code> 
     */
    public void setSapJcoTable(String sapJcoTable)
    {
        this.sapJcoTable = sapJcoTable;
    }
    
    /**
     * The name of SAP <code>JCoTable</code> that encapsulates a input of table parameters 
     * @return the name of SAP <code>JCoTable</code> .
     * @see com.sap.conn.jco.JCoTable
     */
    public String getSapJcoTableIn()
    {
        return sapJcoTableIn;
    }
    
    
    /**
     * The name of SAP <code>JCoTable</code> 
     * @param sapJcoTableIn name of SAP <code>JCoTable</code> 
     */
    public void setSapJcoTableIn(String sapJcoTableIn)
    {
        this.sapJcoTableIn = sapJcoTableIn;
    }
    
    /**
     * The prefix name of parameters. Default is <code>SAPJco_</code>
     * @return prefix name of JCo parameters
     */
    public String getPrefixParams()
    {
        return prefixParams;
    }
    
    /**
     * Define prefix name for JCo parameters. Default is <code>SAPJco_</code>
     * 
     * @param prefixParams name of prefix
     */
    public void setPrefixParams(String prefixParams)
    {
        this.prefixParams = prefixParams;
    }
    
    /**
     * Parameters of JCo.
     * @return true if parameters from JCo come from header message, false otherwise.
     */
    public boolean isUseHeaderAsParam()
    {
        return useHeaderAsParam;
    }
    
    /**
     * Define if JCo parameters are in header message.
     * @param useHeaderAsParam parameters from JCo come from header message
     */
    public void setUseHeaderAsParam(boolean useHeaderAsParam)
    {
        this.useHeaderAsParam = useHeaderAsParam;
    }
    
    /**
     * When <code>supportsNull</code> is <code>true</code> the list of result could be null values.
     * 
     * @return <code>false</code> if result of JCo function supports <code>null</code> values, <code>true</code> otherwise.
     */
    public boolean isSupportsNull()
    {
        return supportsNull;
    }
    
    /**
     * set <code>true</code> to support null values at result list.
     * 
     * @param supportsNull indicate if result list could be contains <code>null</code> values. 
     */
    public void setSupportsNull(boolean supportsNull)
    {
        this.supportsNull = supportsNull;
    }
    
    /**
     * Class name from implementation of {@code ParserResult}
     * @return the name of concrete class that implements {@code ParserResult}
     */
    public String getParserResultStrategy()
    {
        return parserResultStrategy;
    }
    
    public <T> Class<T> getClassToParserResultStrategy()
    {
        if (classToParserResultStrategy == null)
        {
            classToParserResultStrategy = forName(parserResultStrategy);
        }
        return classToParserResultStrategy;
    }
    
    /**
     * Define the class name that implements {@code ParserResult}
     * @param parserResultStrategy class name to parse result 
     * @throws RuntimeCamelException when {@code net.sf.jkniv.camel.sap.jco3.ParserResult} implementation cannot be found
     */
    public void setParserResultStrategy(String parserResultStrategy)
    {
        classToParserResultStrategy = forName(parserResultStrategy);
        this.parserResultStrategy = parserResultStrategy;
    }
    
    private Class forName(String parserResultStrategy)
    {
        try
        {
            Class clazz = SapJcoEndpoint.class.forName(parserResultStrategy);
            if (ParserResult.class.isAssignableFrom(clazz))
                return clazz;
        }
        catch (ClassNotFoundException e)
        {
        }
        // must be not reach here
        throw new RuntimeCamelException(
                "Class [" + parserResultStrategy + "] doesn't assignable to [" + ParserResult.class.getName() + "] or not found at classpath");
    }
    
    public void setPropSapConnection(Properties propSapConnection)
    {
        this.propSapConnection = propSapConnection;
    }
    
    public Properties getPropSapConnection()
    {
        return propSapConnection;
    }
}
