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

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.CamelContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * Represents the component that manages {@link SapJcoEndpoint}.
 */
public class SapJcoComponent extends DefaultComponent //UriEndpointComponent
{
    
    private static final Logger                  LOG        = LoggerFactory.getLogger(SapJcoComponent.class);
    private static final DestinationDataProvider myProvider = SapDataProviderFactory.getInstance();
    
    public SapJcoComponent()
    {
        super();
        //super(SapJcoEndpoint.class);
        LOG.info("SapJcoDestinationDataProvider instance hashCode: " + myProvider.hashCode());
    }
    
    public SapJcoComponent(CamelContext context)
    {
        //super(context, SapJcoEndpoint.class);
        super(context);
    }
    
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception
    {
        SapJcoEndpoint endpoint = new SapJcoEndpoint(uri, this);
        Properties prop = lookup(remaining);
        setProperties(endpoint, parameters);
        endpoint.setPropSapConnection(prop);
        register();
        setProperty(endpoint.getSapDestName(),prop);
        return endpoint;
    }
    
    private Properties lookup(String remaining)
    {
        Properties prop = null;
        Object resource = JndiResources.lookup(remaining);
        if (resource != null)
        {
            if (resource instanceof Properties)
                prop = (Properties) resource;
            else
                throw new RuntimeCamelException("Resource with name [" + remaining
                        + "] must be an instance of java.util.Properties to connect with Sap JCo");
        }
        else
        {
            prop = CamelContextHelper.mandatoryLookup(getCamelContext(), remaining, Properties.class);
            LOG.info("lookup successfully properties in camel context [" + remaining + "]");
        }
        return prop;
    }
    
    @Override
    protected void doShutdown() throws Exception
    {
        unregistryDataProvider();
        super.doShutdown();
    }
    
    @Override
    protected void doStop() throws Exception
    {
        unregistryDataProvider();
        super.doStop();
    }
    
    private void unregistryDataProvider()
    {
        try
        {
            if (com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered())
            {
                com.sap.conn.jco.ext.Environment.unregisterDestinationDataProvider(myProvider);
                LOG.info("Destination Data Provider was unregistered [{}] successfully.", myProvider);
            }
        }
        catch (IllegalStateException providerAlreadyRegisteredException)
        {
            LOG.error("Cannot deregister the DestinationDataProvider", providerAlreadyRegisteredException);
            //somebody else registered its implementation, 
            //stop the execution
        }
    }
    
    static void register()
    {
        //register the provider with the JCo environment;
        //catch IllegalStateException if an instance is already registered
        try
        {
            if (!com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered())
            {
                com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(myProvider);
                LOG.info("Destination Data Provider was register [{}] successfully.", myProvider);
            }
            else
                LOG.debug("Jco Destination global Environment data provider alright registered");
        }
        catch (IllegalStateException providerAlreadyRegisteredException)
        {
            //somebody else registered its implementation, 
            //stop the execution
            throw new Error(providerAlreadyRegisteredException);
        }
        //set properties for the destination and ...
    }
    
    static void setProperty(String destName, Properties props)
    {
        if (myProvider instanceof SapJcoDestinationDataProvider)
            ((SapJcoDestinationDataProvider) myProvider).changeProperties(destName, props);
        else if (myProvider != null
                && SapDataProviderFactory.SHARED_DATA_PROVIDER.equals(myProvider.getClass().getName()))
        {
            invoke("changeProperties", new Class[]{String.class, Properties.class}, new Object[]{destName, props});
        }
    }

    private static void invoke(String methodName, Class<?>[] classes, Object[] params)
    {
        try
        {
            Method method = myProvider.getClass().getDeclaredMethod(methodName, classes);
            method.invoke(myProvider, params);
        }
        catch (Exception e)
        {
            LOG.error("Cannot invoke "+methodName+"(...)", e);
        }
    }
    
}
