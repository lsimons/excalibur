/*
 * Copyright 2003-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package org.apache.avalon.fortress.examples.interceptors.business.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.avalon.fortress.ExtendedMetaInfo;
import org.apache.avalon.fortress.impl.interceptor.AbstractInterceptor;

/**
 * @author <a href="mailto:dev@excalibur.apache.org">Excalibur Development Team</a>
 */
public class TransactionalInterceptor extends AbstractInterceptor
{

    /**
     * Document me!
     *  
     * @see org.apache.avalon.fortress.interceptor.Interceptor#intercept(java.lang.Object, org.apache.avalon.fortress.ExtendedMetaInfo, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object intercept(Object instance, ExtendedMetaInfo meta, Method method, Object[] args)
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // TODO Auto-generated method stub
        return super.intercept(instance, meta, method, args);
    }

}
