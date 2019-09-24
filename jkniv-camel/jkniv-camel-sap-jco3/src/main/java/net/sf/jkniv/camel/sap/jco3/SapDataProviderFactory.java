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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.ext.DestinationDataProvider;

public class SapDataProviderFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(SapDataProviderFactory.class);
    public static final String  SHARED_DATA_PROVIDER   = "net.sf.jkniv.sap.env.SharedDestinationDataProvider";
    public static final String  SHARED_SAPJCO_REGISTRY = "net.sf.jkniv.sap.env.SharedSapJcoRegistry";
    private static DestinationDataProvider sapDataProvider;
    
    public static DestinationDataProvider getInstance()
    {
        if (sapDataProvider == null)
        {
            setInstanceOfDataProvider();
        }
        if (sapDataProvider == null)
            sapDataProvider = new SapJcoDestinationDataProvider();
        
        LOG.debug("DestinationDataProvider instanceof {}",
                (sapDataProvider != null ? sapDataProvider.getClass().getName() : "null"));
        return sapDataProvider;
    }
    
    private static void setInstanceOfDataProvider()
    {
        try
        {
            Invoke GET_INSTANCE = new Invoke(SHARED_DATA_PROVIDER, "getInstance", null);
            sapDataProvider = (DestinationDataProvider) GET_INSTANCE.invoke(null);
        }
        catch (Exception e)
        {
            LOG.warn(
                    SHARED_DATA_PROVIDER
                            + " doesn't in the classpath [{}]. Add jkniv-sap-provider.jar file as shared library.",
                    e.getMessage());
        }
    }
    
    public static Invoke getSharedRegister()
    {
        try
        {
            return  new Invoke(SHARED_SAPJCO_REGISTRY, "register", new Class[]{DestinationDataProvider.class, String.class});
        }
        catch (Exception e)
        {
            LOG.warn(
                    SHARED_SAPJCO_REGISTRY
                    + " doesn't in the classpath [{}]. Add jkniv-sap-provider.jar file as shared library.",
                    e.getMessage());
        }
        return null;
    }

    public static Invoke getSharedUnregister()
    {
        try
        {
            return  new Invoke(SHARED_SAPJCO_REGISTRY, "unregister", new Class[]{DestinationDataProvider.class, String.class});
        }
        catch (Exception e)
        {
            LOG.warn(
                    SHARED_SAPJCO_REGISTRY
                    + " doesn't in the classpath [{}]. Add jkniv-sap-provider.jar file as shared library.",
                    e.getMessage());
        }
        return null;
    }
}
