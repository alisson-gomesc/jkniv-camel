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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 * Supports communication with the SAP Server in inbound calls (Java calls ABAP) using JCo.
 *  
 */
public class SapJcoProducer extends DefaultProducer
{
    private static final Logger LOG = LoggerFactory.getLogger(SapJcoProducer.class);
    private SapJcoEndpoint      endpoint;
    
    public SapJcoProducer(SapJcoEndpoint endpoint)
    {
        super(endpoint);
        this.endpoint = endpoint;
    }
    
    
    
    public void process(Exchange exchange) throws Exception
    {
        JCoDestination destination = JCoDestinationManager.getDestination(endpoint.getSapDestName());
        JCoFunction function = destination.getRepository().getFunction(endpoint.getSapFunction());
        setParams(exchange, function);
        function.execute(destination);
        
        JCoTable codes = function.getTableParameterList().getTable(endpoint.getSapJcoTable());
        List<Object> result = new ArrayList<Object>();
        ParserResult<?> parserResult = getParserResult();
        for (int i = 0; i < codes.getNumRows(); i++)
        {
            codes.setRow(i);
            Object o = parserResult.getValues(codes, i);
            if (o != null || endpoint.isSupportsNull())
                result.add(o);
        }
        // preserve headers
        exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders());
        exchange.getOut().setBody(result);
    }
        
    @SuppressWarnings("unchecked")
    private <T> ParserResult<T> getParserResult()
    {
        ParserResult<T> result = null;
        try
        {
            result = (ParserResult<T>) endpoint.getClassToParserResultStrategy().newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeCamelException("Cannot create new instance of ["+endpoint.getParserResultStrategy()+"]", e);
        } 
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private void setParams(Exchange exchange, JCoFunction function)
    {
        Map<String, Object> params = getParams(exchange);
        Set<String> keys = params.keySet();
        for (String key : keys)
        {
            Object value = params.get(key);
            function.getImportParameterList().setValue(key, value);
            if (LOG.isDebugEnabled())
                LOG.debug("setting param=[{}] value=[{}], typeof=[{}], prefix=[{}]", key, value,
                        (value != null ? value.getClass().getName() : "null"), endpoint.getPrefixParams() );
        }
    }
    
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getParams(Exchange exchange)
    {
        Map<String, Object> params = exchange.getIn().getBody(Map.class);
        boolean usingPrefix = false;
        if (endpoint.isUseHeaderAsParam() || params == null || params.isEmpty())
        {
            Map<String, Object> paramsSap = exchange.getIn().getHeader(endpoint.getPrefixParams(), Map.class);
            if (paramsSap == null)
                paramsSap = exchange.getIn().getHeaders();
            
            Set<String> keys = paramsSap.keySet();
            params = new HashMap<String, Object>();
            for (String key : keys)
            {
                if (key.startsWith(endpoint.getPrefixParams()))
                {
                    usingPrefix = true;
                    String paramName = key.substring(endpoint.getPrefixParams().length());
                    Object value = paramsSap.get(key);
                    params.put(paramName, value);
                }
            }
        }
        if (endpoint.isUseHeaderAsParam() && !usingPrefix)
        {
            LOG.warn("When Endpoint option useHeaderAsParam=true the parameters name must be prefixed with [{}]", endpoint.getPrefixParams());
        }
        return params;
    }
    
}
