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

import java.util.Map;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.util.CamelContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the component that manages {@link SapJcoEndpoint}.
 */
public class SapJcoComponent extends DefaultComponent
{
    private static final Logger                  LOG        = LoggerFactory.getLogger(SapJcoComponent.class);
    
    public SapJcoComponent()
    {
        super();
        LOG.trace("SapJcoComponent instanced successfully");
    }
    
    public SapJcoComponent(CamelContext context)
    {
        super(context);
        LOG.trace("SapJcoComponent instanced successfully");
    }
    
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception
    {
        SapJcoEndpoint endpoint = new SapJcoEndpoint(uri, this);
        Properties prop = lookup(remaining);
        setProperties(endpoint, parameters);
        endpoint.setPropSapConnection(prop);
        SapJcoRegistry.register();
        SapJcoRegistry.setProperty(endpoint.getSapDestName(), prop);
        LOG.trace("SapJcoComponent create Endpoint successfully");
        return endpoint;
    }
    
    @Override
    protected void doShutdown() throws Exception
    {
        SapJcoRegistry.unregister();
        super.doShutdown();
    }
    
    @Override
    protected void doStop() throws Exception
    {
        SapJcoRegistry.unregister();
        super.doStop();
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
    
    /*
    static void setProperty(String destName, Properties props)
    {
        if (MY_PROVIDER instanceof SapJcoDestinationDataProvider)
            ((SapJcoDestinationDataProvider) MY_PROVIDER).changeProperties(destName, props);
        else if (MY_PROVIDER != null
                && SapDataProviderFactory.SHARED_DATA_PROVIDER.equals(MY_PROVIDER.getClass().getName()))
        {
            CHANGE_PROPERTIES.invoke(MY_PROVIDER, new Object[] { destName, props });
            //invoke("changeProperties", new Class[]{String.class, Properties.class}, new Object[]{destName, props});
        }
    }
    */
    
    /*
    private static void invoke(String methodName, Class<?>[] classes, Object[] params)
    {
        try
        {
            Method method = myProvider.getClass().getDeclaredMethod(methodName, classes);
            method.invoke(myProvider, params);
        }
        catch (Exception e)
        {
            LOG.error("Cannot invoke " + methodName + "(...)", e);
        }
    }
    */
}
