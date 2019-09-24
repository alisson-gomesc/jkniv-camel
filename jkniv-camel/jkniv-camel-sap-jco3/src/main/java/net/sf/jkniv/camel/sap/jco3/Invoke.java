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

/**
 * 
 * @author Alisson Gomes
 * @since 2.20.0
 */
class Invoke
{
    private static final Logger LOG = LoggerFactory.getLogger(Invoke.class);
    private Method              method;

    public Invoke(String classToInvoke, String methodName, Class<?>[] classesParamTypes)
    {
        try
        {
            this.method = Class.forName(classToInvoke).getDeclaredMethod(methodName, classesParamTypes);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(
                    "Cannot found method [" + methodName + "] with params type of:" + getParamTypes(classesParamTypes), e);
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(
                    "Cannot found method [" + methodName + "] with params type of:" + getParamTypes(classesParamTypes), e);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException(
                    "Class not found [" + classToInvoke + "] with params type of:" + getParamTypes(classesParamTypes), e);
        }
    }

    public Invoke(Class<?> classToInvoke, String methodName, Class<?>[] classesParamTypes)
    {
        try
        {
            this.method = classToInvoke.getDeclaredMethod(methodName, classesParamTypes);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(
                    "Cannot found method [" + methodName + "] with params type of:" + getParamTypes(classesParamTypes), e);
        }
        catch (SecurityException e)
        {
            throw new RuntimeException(
                    "Cannot found method [" + methodName + "] with params type of:" + getParamTypes(classesParamTypes), e);
        }
    }
    
    public Object invoke(Object instance, Object[] params)
    {
        Object answer = null;
        try
        {
            answer = method.invoke(instance, params);
        }
        catch (Exception e)
        {
            LOG.error("Cannot invoke " + method.getName() + ", param types of:"+ getParamTypes(method.getParameterTypes()) , e);
        }
        return answer;
    }
    
    public Object invoke()
    {
        Object answer = null;
        try
        {
            answer = method.invoke(null);
        }
        catch (Exception e)
        {
            LOG.error("Cannot invoke " + method.getName() + ", param types of:"+ getParamTypes(method.getParameterTypes()) , e);
        }
        return answer;
    }
    
    public Object invoke(Object[] params)
    {
        Object answer = null;
        try
        {
            answer = method.invoke(null, params);
        }
        catch (Exception e)
        {
            LOG.error("Cannot invoke " + method.getName() + ", param types of:"+ getParamTypes(method.getParameterTypes()) , e);
        }
        return answer;
    }
    
    private String getParamTypes(Class<?>[] classesParamTypes)
    {
        StringBuilder sb = new StringBuilder();
        for(Class<?> c : classesParamTypes)
            sb.append(" ").append(c.getName());
        return sb.toString();
    }
    
}
