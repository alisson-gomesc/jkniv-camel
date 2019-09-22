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

class SapJcoRegistry
{
    private static final Logger                  LOG        = LoggerFactory.getLogger(SapJcoComponent.class);
    
    /**
     * Register a {@code DestinationDataProvider}
     * @param myProvider a logical address of an ABAP system 
     * @param appName Name of application that is registered
     */
    public static void register(final DestinationDataProvider myProvider, final String appName)
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

    /**
     * Register a {@code DestinationDataProvider}
     * @param myProvider a logical address of an ABAP system 
     * @param appName Name of application that is registered
     */
    public static void undoRegister(final DestinationDataProvider myProvider, final String appName)
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
            //somebody else registered its implementation, stop the execution
        }
    }

    public static boolean hasRegister(String appName)
    {
        return true;
    }

}
