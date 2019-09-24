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
package net.sf.jkniv.camel.sap;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sap.conn.jco.ext.DestinationDataProvider;

import net.sf.jkniv.sap.env.SharedDestinationDataProvider;
import net.sf.jkniv.sap.env.SharedSapJcoRegistry;

public class SharedSapJcoRegistryTest
{

    @Test
    public void whenRegistryNullValues()
    {
        SharedSapJcoRegistry.register(null, null);
        assertThat(SharedSapJcoRegistry.hasRegister(null), is(true));
    }
    
    @Test
    public void whenRegistryDestinationDataProvider()
    {
        DestinationDataProvider provider =  SharedDestinationDataProvider.getInstance();
        SharedSapJcoRegistry.register(provider, "test");
        assertThat(SharedSapJcoRegistry.hasRegister("test"), is(true));
    }
    
    @Test
    public void whenRegistryAndUndoregisterDestinationDataProvider()
    {
        DestinationDataProvider provider =  SharedDestinationDataProvider.getInstance();
        SharedSapJcoRegistry.register(provider, "test");
        assertThat(SharedSapJcoRegistry.hasRegister("test"), is(true));
        SharedSapJcoRegistry.unregister(provider, "test");
        assertThat(SharedSapJcoRegistry.hasRegister("test"), is(false));
    }
    
    @Test
    public void whenRegistryManyApplications()
    {
        DestinationDataProvider provider =  SharedDestinationDataProvider.getInstance();
        SharedSapJcoRegistry.register(provider, "app1");
        SharedSapJcoRegistry.register(provider, "app2");
        SharedSapJcoRegistry.register(provider, "app3");
        SharedSapJcoRegistry.register(provider, "app4");

        assertThat(SharedSapJcoRegistry.hasRegister("app1"), is(true));
        assertThat(SharedSapJcoRegistry.hasRegister("app2"), is(true));
        assertThat(SharedSapJcoRegistry.hasRegister("app3"), is(true));
        assertThat(SharedSapJcoRegistry.hasRegister("app4"), is(true));

        SharedSapJcoRegistry.unregister(provider, "app4");
        assertThat(SharedSapJcoRegistry.hasRegister("app4"), is(false));

        SharedSapJcoRegistry.unregister(provider, "app3");
        assertThat(SharedSapJcoRegistry.hasRegister("app3"), is(false));
        
        SharedSapJcoRegistry.unregister(provider, "app2");
        assertThat(SharedSapJcoRegistry.hasRegister("app2"), is(false));
        
        SharedSapJcoRegistry.unregister(provider, "app1");
        assertThat(SharedSapJcoRegistry.hasRegister("app1"), is(false));
    }

}
