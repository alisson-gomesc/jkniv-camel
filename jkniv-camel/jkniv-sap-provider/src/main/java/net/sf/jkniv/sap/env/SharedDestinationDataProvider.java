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
package net.sf.jkniv.sap.env;

import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * Each application using Java Connector 3 deals with destinations. A destination represents a logical address 
 * of an ABAP system and thus separates the destination configuration from application logic. JCo retrieves
 * the destination parameters required at runtime from DestinationDataProvider and ServerDataProvider registered
 * in the runtime environment. If no provider is registered, JCo uses the default implementation that reads the 
 * configuration from a properties file. It is expected that each environment provides a suitable 
 * implementation that meets security and other requirements. Furthermore, only one DestinationDataProvider 
 * and only one ServerDataProvider can be registered per process. The reason behind this design decision 
 * is the following: the provider implementations are part of the environment infrastructure. 
 * The implementation should not be application specific, and in particular must be separated from 
 * application logic. 
 */
public class SharedDestinationDataProvider implements DestinationDataProvider
{
    private static final Logger          LOG             = java.util.logging.Logger
            .getLogger("SharedDestinationDataProvider");
    private DestinationDataEventListener eL;
    private HashMap<String, Properties>  propsByDestName = new HashMap<String, Properties>();
    private static DestinationDataProvider instance;
    
    public SharedDestinationDataProvider()
    {
    }
    
    public static DestinationDataProvider getInstance()
    {
        if (instance == null)
            instance = new SharedDestinationDataProvider();
        return instance;
    }
    
    /**
     * The name of SAP <code>JCoDestination</code> name that identifies a physical destination of a function call.
     */
    public Properties getDestinationProperties(String destName)
    {
        try
        {
            //read the destination from DB
            Properties p = propsByDestName.get(destName);
            
            if (p != null)
            {
                //check if all is correct, for example
                if (p.isEmpty())
                    throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION,
                            "destination [" + destName + "] configuration is incorrect", null);
                
                return p;
            }
            return null;
        }
        catch (RuntimeException re)
        {
            throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR,
                    "Internal error to get config from destination [" + destName + "]", re);
        }
    }
    
    /**
     * An implementation supporting events has to retain the eventListener instance provided
     * by the JCo runtime. This listener instance shall be used to notify the JCo runtime
     * about all changes in destination configurations.
     * 
     * @param eventListener The DestinationDataEventListener interface reacts on the events 
     * that a DestinationDataProvider could fire if the destination configuration was changed
     */
    public void setDestinationDataEventListener(DestinationDataEventListener eventListener)
    {
        this.eL = eventListener;
    }
    
    private boolean hasDestName(String destName)
    {
        return propsByDestName.containsKey(destName);
    }
    
    public boolean supportsEvents()
    {
        return true;
    }
    
    /**
     * Update the connect properties from a specific <code>destName</code>
     * @param destName the name of <code>JCoDestination</code>
     * @param properties connection properties
     */
    public void changeProperties(String destName, Properties properties)
    {
        synchronized (propsByDestName)
        {
            boolean containsDestName = hasDestName(destName);
            if (containsDestName && properties == null)
            {
                LOG.fine("Jco Destination removing [" + destName + "] from Shared SAP data provider.");
                if (propsByDestName.remove(destName) != null)
                    eL.deleted(destName);
            }
            else if (!containsDestName && properties != null)
            {
                LOG.fine("Update Jco Destination [" + destName + "] from Shared SAP data provider.");
                propsByDestName.put(destName, properties);
                eL.updated(destName); // create or updated
            }
            else if (containsDestName)
            {
                LOG.warning("Destination name [" + destName + "] alright exists.");
            }
        }
    }
    
}
