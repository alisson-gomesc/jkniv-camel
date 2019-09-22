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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.ext.DestinationDataProvider;

public class SapDataProviderFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(SapDataProviderFactory.class);
    public static final String SHARED_DATA_PROVIDER = "net.sf.jkniv.sap.env.SharedDestinationDataProvider";
    private static DestinationDataProvider sapDataProvider;
    
    public static DestinationDataProvider getInstance()
    {
        if (sapDataProvider == null)
        {
            setInstance();
        }
        if (sapDataProvider == null)
            sapDataProvider = new SapJcoDestinationDataProvider();
            
        LOG.debug("DestinationDataProvider instanceof {}", (sapDataProvider != null ? sapDataProvider.getClass().getName() : "null"));
        return sapDataProvider;
    }
    
    private static void setInstance()
    {
        try
        {
            Class clazz = Class.forName(SHARED_DATA_PROVIDER);
            Method method = clazz.getDeclaredMethod("getInstance");
            sapDataProvider = (DestinationDataProvider) method.invoke(null);
        }
        catch (Exception e)
        {
            LOG.warn(SHARED_DATA_PROVIDER+" doesn't in the classpath [{}]. Add jkniv-sap-provider.jar file as shared library.", e.getMessage());
        }
    }
}
