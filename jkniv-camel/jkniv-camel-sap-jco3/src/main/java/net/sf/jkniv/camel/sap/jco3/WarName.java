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

import java.io.File;
import java.net.URISyntaxException;

import org.mockito.internal.stubbing.answers.Returns;

/**
 * 
 * @author Alisson Gomes
 * @since 2.20.0
 */
class WarName
{
    public static String getName() 
    {
        String path = getDirUri();
        String[] pathSplitted = path.split("\\\\");
        int i = 0;
        for(; i<pathSplitted.length; i++)
        {
            if ("WEB-INF".equals(pathSplitted[i]))
                    break;
        }
        if (i == pathSplitted.length)
            return path;
        
        return pathSplitted[i-1]; 
    }
    
    private static String getDirUri() {
        final String UNKNOW = "UNKNOW";
        try
        {
            return new File(WarName.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath();
        }
        catch (URISyntaxException ignoreUnknowAppName) {}
        return UNKNOW;
    }

}
