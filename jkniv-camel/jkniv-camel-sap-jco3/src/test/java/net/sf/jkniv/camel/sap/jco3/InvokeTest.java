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


public class InvokeTest
{
    @Test
    public void whenInvokeWithoutParams()
    {
        Invoke invoke = new Invoke(SapJcoDestinationDataProvider.class, "supportsEvents", null);
        SapJcoDestinationDataProvider provider = new SapJcoDestinationDataProvider();
        Object answer = invoke.invoke(provider, null);
        assertThat(answer, instanceOf(boolean.class));
        assertThat((boolean)answer, is(true));
    }
    
    @Test
    public void whenInvokeWithParams()
    {
        Invoke invoke = new Invoke(String.class, "indexOf", new Class[]{String.class});
        String abcdef = "abcdef";
        Object answer = invoke.invoke(abcdef, new Object[]{"c"});
        assertThat(answer, instanceOf(Integer.class));
        assertThat((int)answer, is(2));
    }
}
