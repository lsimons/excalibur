/* 
 * Copyright 2004 The Apache Software Foundation
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

package org.apache.avalon.fortress;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.fortress.attributes.AttributeInfo;
import org.apache.avalon.fortress.attributes.AttributeLevel;

/**
 * Pending
 * 
 * @author <a href="mailto:dev@excalibur.apache.org">Excalibur Development Team</a>
 */
public class ExtendedMetaInfo
{
    private static final AttributeInfo[] EMPTY = new AttributeInfo[0]; 
    
    private final AttributeInfo[] m_classAttributes;
    private final Map m_method2Attributes;
    
    public ExtendedMetaInfo( AttributeInfo[] attributes )
    {
        final List classLevel = new ArrayList();
        m_method2Attributes = new HashMap(); 
        
        for (int i = 0; i < attributes.length; i++)
        {
            final AttributeInfo attribute = attributes[i];
            
            if (attribute.getAttributeLevel() == AttributeLevel.ClassLevel)
            {
                classLevel.add( attribute );
            }
            else if (attribute.getAttributeLevel() == AttributeLevel.MethodLevel)
            {
                if (attribute.getMethod() != null)
                {
                    associateMethodAttribute( attribute );
                }
            }
        }
        
        m_classAttributes = (AttributeInfo[]) classLevel.toArray( new AttributeInfo[0] );
    }
    
    public AttributeInfo[] getClassAttributes()
    {
        return m_classAttributes;
    }
    
    public AttributeInfo[] getAttributesForMethod( final Method method )
    {
        List attributes = obtainAttributeList( method );
        
        if (attributes == null)
        {
            return EMPTY;
        }
        
        return (AttributeInfo[]) attributes.toArray( new AttributeInfo[0] );
    }
    
    private void associateMethodAttribute( AttributeInfo attribute )
    {
        List attrs = obtainAttributeList( attribute.getMethod() );
        
        if (attrs == null)
        {
            attrs = new ArrayList();
            m_method2Attributes.put( attribute.getMethod(), attrs );
        }
        
        attrs.add( attribute );
    }

    private List obtainAttributeList( final Method method )
    {
        return (List) m_method2Attributes.get( method );
    }
}
