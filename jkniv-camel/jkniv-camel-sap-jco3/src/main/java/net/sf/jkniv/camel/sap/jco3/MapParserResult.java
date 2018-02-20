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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.JCoRecordMetaData;
import com.sap.conn.jco.JCoTable;

public class MapParserResult implements ParserResult<Map<String, Object>>
{
    private static final Logger LOG = LoggerFactory.getLogger(MapParserResult.class);
    
    @Override
    public Map<String, Object> getValues(JCoTable codes, int row)
    {
        Map<String, Object> values = new HashMap<String, Object>();
        JCoRecordMetaData recordMetaData = codes.getRecordMetaData();
        if (codes != null && codes.getNumColumns() > 0)
        {
            for (int i = 0; i < codes.getNumColumns(); i++)
            {
                String dataKey = recordMetaData.getName(i);
                Object o = codes.getValue(i);
                values.put(dataKey, o);
                
                if (LOG.isDebugEnabled())
                    LOG.debug("[{},{}] dataKey=[{}], type=[{}], description=[{}], value=[{}]", row, i, dataKey,
                            (o == null ? null : o.getClass().getName()), recordMetaData.getDescription(i), o);
            }
        }
        return values;
    }
    
}
