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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * 
 * @author Alisson Gomes
 * @since 2.20.0
 */
class SapJcoRegistry
{
    private static final Logger                  LOG;
    /** a logical address of an ABAP system  */
    private static final DestinationDataProvider MY_PROVIDER;
    private static Invoke                        CHANGE_PROPERTIES;
    private static Invoke                        REGISTER;
    private static Invoke                        UNDO_REGISTER;
    private static String                        APP_NAME;
    static
    {
        LOG = LoggerFactory.getLogger(SapJcoRegistry.class);
        MY_PROVIDER = SapDataProviderFactory.getInstance();
        REGISTER = SapDataProviderFactory.getSharedRegister();
        UNDO_REGISTER = SapDataProviderFactory.getSharedUnregister();
        APP_NAME = WarName.getName();
        CHANGE_PROPERTIES = new Invoke(MY_PROVIDER.getClass(), "changeProperties", new Class[]
        { String.class, Properties.class });
        
    }
    
    /**
     * Register a {@code DestinationDataProvider}
     */
    public static void register()
    {
        if (REGISTER != null)
            sharedRegister();
        else
            localRegister();
    }

    /**
     * Register a {@code DestinationDataProvider} using {@code jkniv-sap-provider} a shared register.
     */
    private static void sharedRegister()
    {
        REGISTER.invoke(new Object[]{MY_PROVIDER, APP_NAME});
    }
    
    /**
     * Register a {@code DestinationDataProvider} using {@code jkniv-camel-sap-jco3} component.
     */
    private static void localRegister()
    {
        //register the provider with the JCo environment;
        //catch IllegalStateException if an instance is already registered
        try
        {
            if (!com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered())
            {
                com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(MY_PROVIDER);
                LOG.info("Destination Data Provider was register [{}] successfully.", MY_PROVIDER);
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
    }
    /**
     * Undo register a {@code DestinationDataProvider}
     */
    public static void unregister()
    {
        if (UNDO_REGISTER != null)
            sharedUndoRegister();
        else
            localUndoRegister();
    }
    
    /**
     * Undo register a {@code DestinationDataProvider} using {@code jkniv-sap-provider} a shared component.
     */
    private static void sharedUndoRegister()
    {
        UNDO_REGISTER.invoke(new Object[]{MY_PROVIDER, APP_NAME});
    }
    
    /**
     * Undo register a {@code DestinationDataProvider}
     */
    public static void localUndoRegister()
    {
        try
        {
            if (com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered())
            {
                com.sap.conn.jco.ext.Environment.unregisterDestinationDataProvider(MY_PROVIDER);
                LOG.info("Destination Data Provider was unregistered [{}] successfully.", MY_PROVIDER);
            }
        }
        catch (IllegalStateException providerAlreadyRegisteredException)
        {
            LOG.error("Cannot deregister the DestinationDataProvider", providerAlreadyRegisteredException);
            //somebody else registered its implementation, stop the execution
        }
    }
    
    public static boolean hasRegister(String appName)
    {
        return true;
    }
    
    public static void setProperty(String destName, Properties props)
    {
        if (MY_PROVIDER instanceof SapJcoDestinationDataProvider)
            ((SapJcoDestinationDataProvider) MY_PROVIDER).changeProperties(destName, props);
        else if (MY_PROVIDER != null
                && SapDataProviderFactory.SHARED_DATA_PROVIDER.equals(MY_PROVIDER.getClass().getName()))
        {
            CHANGE_PROPERTIES.invoke(MY_PROVIDER, new Object[]
            { destName, props });
        }
    }
    
}
