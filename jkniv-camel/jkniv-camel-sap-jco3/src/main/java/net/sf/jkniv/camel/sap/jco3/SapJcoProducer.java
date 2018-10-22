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

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.apache.camel.impl.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * Supports communication with the SAP Server in inbound calls (Java calls ABAP) using JCo.
 *  
 */
public class SapJcoProducer extends DefaultProducer
{
    private static final Logger LOG = LoggerFactory.getLogger(SapJcoProducer.class);
    private SapJcoEndpoint      endpoint;
    private static final Set<Class<?>> SUPPORTED_TYPES = new HashSet<Class<?>>();
    static {
        SUPPORTED_TYPES.add(String.class);
        SUPPORTED_TYPES.add(boolean.class);
        SUPPORTED_TYPES.add(char.class);
        SUPPORTED_TYPES.add(byte.class);
        SUPPORTED_TYPES.add(short.class);
        SUPPORTED_TYPES.add(int.class);
        SUPPORTED_TYPES.add(long.class);
        SUPPORTED_TYPES.add(float.class);
        SUPPORTED_TYPES.add(double.class);
        SUPPORTED_TYPES.add(Boolean.class);
        SUPPORTED_TYPES.add(Character.class);
        SUPPORTED_TYPES.add(Byte.class);
        SUPPORTED_TYPES.add(Short.class);
        SUPPORTED_TYPES.add(Integer.class);
        SUPPORTED_TYPES.add(Long.class);
        SUPPORTED_TYPES.add(Float.class);
        SUPPORTED_TYPES.add(Double.class);
        SUPPORTED_TYPES.add(Void.class);
        SUPPORTED_TYPES.add(Date.class);
        SUPPORTED_TYPES.add(Calendar.class);
        SUPPORTED_TYPES.add(LocalDate.class);
        SUPPORTED_TYPES.add(LocalDateTime.class);
        SUPPORTED_TYPES.add(LocalTime.class);
        SUPPORTED_TYPES.add(Duration.class);
        SUPPORTED_TYPES.add(Instant.class);
    }
    
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
            throw new RuntimeCamelException(
                    "Cannot create new instance of [" + endpoint.getParserResultStrategy() + "]", e);
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    private void setParams(Exchange exchange, JCoFunction function)
    {
        Map<String, Object> params = getParams(exchange);
        Set<String> keys = params.keySet();
        //Find structure parameter with this name and set the appropriate values
        JCoFieldIterator iter = function.getImportParameterList().getFieldIterator();
        while (iter.hasNextField())
        {
            JCoField f = iter.nextField();
            //LOG.debug("field={}, isTable={}, isStructure={}", f.getName(), f.isTable(), f.isStructure());
            if (f.isStructure())
            {
                setStructureParameter(f, params);
            }
            else
            {
                Object value = params.get(f.getName());
                if (isSupportedType(value))
                {
                    function.getImportParameterList().setValue(f.getName(), value);
                    if (LOG.isDebugEnabled())
                        LOG.debug("setting param=[{}] value=[{}], typeof=[{}], prefix=[{}]", f.getName(), value,
                            (value != null ? value.getClass().getName() : "null"), endpoint.getPrefixParams());
                }
                else
                    LOG.info("Type [{}] not is supported as parameter try a type of {}", value.getClass(), SUPPORTED_TYPES);
            }
        }
        JCoTable codes = function.getTableParameterList().getTable(endpoint.getSapJcoTableIn());
        if (codes != null)
        {
            //LOG.debug("{}", codes);
            Object tableIn = getParams(exchange).get(endpoint.getSapJcoTableIn());
            if (tableIn instanceof List)
            {
                List<Map<String, Object>> list = (List<Map<String, Object>>) tableIn;
                setTableParameter(codes, list);
            }
            else if(tableIn != null)
            {
                LOG.warn("The parameter sapJcoTableIn must be instance of List of Map, type [{}] isn't supported", tableIn.getClass());
            }
        }        
    }
    
    /**
     * Sets a single Importing or Changing parameter that is a structure
     * @param f field name of parameter
     * @param map the value of the parameter
     */
    public void setStructureParameter(JCoField f, Map<String, Object> map)
    {
        Iterator fieldIter = map.entrySet().iterator();
        JCoStructure structure = f.getStructure();
        while (fieldIter.hasNext())
        {
            Entry field = (Map.Entry) fieldIter.next();
            String k = field.getKey().toString();
            Object v = field.getValue();
            if(isSupportedType(v))
            {
                structure.setValue(k, v);
                if (LOG.isDebugEnabled())
                    LOG.debug("setting param=[{}] value=[{}], typeof=[{}], prefix=[{}]", k, v,
                            (v != null ? v.getClass().getName() : "null"), endpoint.getPrefixParams());
            }
        }
    }
    
    /**
     *  Sets a single Table parameter that is a structure
     * @param table JCo table for input parameters
     * @param list The value of the parameter (A List of HashMap)
     */
    @SuppressWarnings("unchecked")
    private void setTableParameter(JCoTable table, List<Map<String, Object>> list)
    {
        Iterator it = list.listIterator();
        while (it.hasNext())
        {
            table.appendRow();
            Map<String, Object> params = (Map<String, Object>) it.next();
            Iterator itParams = params.entrySet().iterator();
            while (itParams.hasNext())
            {
                Entry<String, Object> entry = (Map.Entry<String, Object>) itParams.next();
                table.setValue(entry.getKey(), entry.getValue());
                if (LOG.isDebugEnabled())
                    LOG.debug("setting param=[{}] value=[{}], typeof=[{}]", entry.getKey(), entry.getValue(),
                        (entry.getValue() != null ? entry.getValue().getClass().getName() : "null"));

            }
        }
    }
    
    /*
    public void setStructureParameter(JCoFunction function, String name, Map<String, Object> map)
    {
        //Find structure parameter with this name and set the appropriate values
        JCoFieldIterator iter = function.getImportParameterList().getFieldIterator();
        while(iter.hasNextField())
        {
            JCoField f = iter.nextField();
            if(f.getName().equals(name) & f.isStructure())
            {
                Iterator fieldIter = map.entrySet().iterator();
                JCoStructure structure = f.getStructure();
                while(fieldIter.hasNext())
                {
                     Entry field = (Map.Entry)fieldIter.next();
                     structure.setValue(field.getKey().toString(), field.getValue().toString());
                     LOG.debug("setting param=[{}] value=[{}], typeof=[{}], prefix=[{}]", field.getKey().toString(), field.getValue().toString(),
                             field.getValue().getClass().getName(), endpoint.getPrefixParams() );
                }
            }
        }
    }
    */
    
    /*
     *  Sets a single Table parameter that is a structure
     * @param name the name of the parameter
     * @param list The value of the parameter (A LinkedList of LinkedHashmaps)
     *
    private void setTableParameter(JCoFunction function,  String name, LinkedList list)
    {
        //Find table parameter with this name and set the appropriate valies
        JCoFieldIterator iter = function.getTableParameterList().getFieldIterator();
        while(iter.hasNextField())
        {
            JCoField f = iter.nextField();
            if(f.getName().equals(name) & f.isTable() )
            {
                Iterator recordIter = list.listIterator();
                JCoTable table = f.getTable();
                while(recordIter.hasNext())
                {
                   table.appendRow();
                   LinkedHashMap fields = (LinkedHashMap)recordIter.next();
                   Iterator fieldIter = fields.entrySet().iterator();
                   while(fieldIter.hasNext())
                   {
                         Entry field = (Map.Entry)fieldIter.next();
                         table.setValue(field.getKey().toString(), field.getValue().toString());
                   }
                }
            }
        }
    }
    */
    
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
            LOG.warn("When Endpoint option useHeaderAsParam=true the parameters name must be prefixed with [{}]",
                    endpoint.getPrefixParams());
        }
        return params;
    }

    private boolean isSupportedType(Object value)
    {
        boolean supported = true;
        if(value != null)
        {
            supported = SUPPORTED_TYPES.contains(value.getClass());
        }
        return supported;
    }
}
