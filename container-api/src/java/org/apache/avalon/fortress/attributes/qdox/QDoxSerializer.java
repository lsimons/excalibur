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

package org.apache.avalon.fortress.attributes.qdox;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.avalon.fortress.ExtendedMetaInfo;
import org.apache.avalon.fortress.attributes.AttributeInfo;
import org.apache.avalon.fortress.attributes.AttributeLevel;

import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * Implements a serialization and deserialization 
 * mechanism for QDox
 * 
 * @author <a href="mailto:dev@excalibur.apache.org">Excalibur Development Team</a>
 */
public class QDoxSerializer
{
    private static final QDoxSerializer m_instance = new QDoxSerializer();
    
    private QDoxSerializer()
    {
    }
    
    public static QDoxSerializer instance()
    {
        return m_instance;
    }

    public void serialize( final ObjectOutputStream stream, final JavaClass clazz )
        throws IOException
    {
        DocletTag[] tags = clazz.getTags();

        for (int i = 0; i < tags.length; i++)
        {
            DocletTag tag = tags[i];
            
            if (tag.getName().equalsIgnoreCase("author"))
            {
                continue;
            }
            
            stream.writeInt( 1 );
            QDoxSerializer.instance().serialize( stream, tag );
        }

        // TODO: Do we need to inspect super classes?
        final JavaMethod[] methods = clazz.getMethods();
        
        for ( int i = 0; i < methods.length; i++ )
        {
            JavaMethod method = methods[i];
            tags = method.getTags();
                
            if (tags.length == 0)
            {
                continue;
            }
                
            for (int j = 0; j < tags.length; j++)
            {
                DocletTag tag = tags[j];
                stream.writeInt( 1 );
                QDoxSerializer.instance().serialize( stream, method, tag );
            }
        }

        stream.writeInt( 0 );
    }

    public ExtendedMetaInfo deserialize( final InputStream stream, final Class target )
        throws IOException
    {
        final MethodInfoHelper methodHelper = new MethodInfoHelper( target );
         
        final ObjectInputStream inStream = new ObjectInputStream( stream );
        
        final List attributes = new ArrayList();
        
        try
        {
            while ( inStream.readInt() == 1 )
            {
                QDoxSerializer.TagInfo info = QDoxSerializer.instance().deserialize( inStream );
                    
                if (info == null)
                {
                    continue;
                }
                
                AttributeInfo attribute = null;
                 
                if (info.getMethod() != null)
                {
                    attribute = buildAttributeInfoForMethod( info, methodHelper );
                }
                else
                {
                    attribute = buildAttributeInfoForClass( info );
                }
                
                attributes.add( attribute );
            }
        }
        catch(ClassNotFoundException ex)
        {
            // is ignoring a good strategy? Don't think so
            // but let's sticky with it at the moment.
        }
        finally
        {
            inStream.close();
        }
            
        AttributeInfo[] attrs = (AttributeInfo[]) attributes.toArray( new AttributeInfo[0] ); 
        
        return new ExtendedMetaInfo( attrs );
    }
    
    public void serialize( final ObjectOutputStream stream, final DocletTag tag )
        throws IOException
    {
        serialize( stream, null, tag );
    }

    public void serialize( final ObjectOutputStream stream, final JavaMethod method, final DocletTag tag )
        throws IOException
    {
        stream.writeObject( new TagInfo( tag, method ) );
    }
    
    private TagInfo deserialize( final ObjectInputStream stream )
        throws IOException, ClassNotFoundException
    {
        return (TagInfo) stream.readObject();
    }

    private static AttributeInfo buildAttributeInfoForMethod( final QDoxSerializer.TagInfo info, 
        final MethodInfoHelper methodHelper )
    {
        Method classMethod = methodHelper.obtainRealMethod( info.getMethod() );

        return new AttributeInfo( info.getTag().getName(), buildAttributes( info ), 
            AttributeLevel.MethodLevel, classMethod );
    }

    private static AttributeInfo buildAttributeInfoForClass( final QDoxSerializer.TagInfo info )
    {
        return new AttributeInfo( info.getTag().getName(), buildAttributes( info ), 
            AttributeLevel.ClassLevel );
    }
        
    private static Map buildAttributes( final QDoxSerializer.TagInfo info )
    {
        Map parameters = Collections.EMPTY_MAP;
            
        String[] params = info.getTag().getParameters();
            
        if (params.length != 0)
        {
            parameters = new TreeMap( String.CASE_INSENSITIVE_ORDER );
                
            for (int i = 0; i < params.length; i++)
            {
                final String property = params[i];
                final int equalIndex = property.indexOf( '=' );
                    
                String key = property;
                String value = "";
                    
                if ( equalIndex != -1 )
                {
                    key = property.substring( 0, equalIndex );
                    value = property.substring( equalIndex + 1 );
                }
                    
                parameters.put( key, value );
            }
        }
            
        return parameters;
    }
    
    public static class TagInfo implements Serializable
    {
        private final DocletTag m_tag;
        private final JavaMethod m_method;
        
        public TagInfo( final DocletTag tag, final JavaMethod method )
        {
            m_tag = tag;
            m_method = method;
        }
        
        public DocletTag getTag()
        {
            return m_tag;
        }
        
        public JavaMethod getMethod()
        {
            return m_method;
        }
    }

    /**
     * Pending
     * 
     * @author hammett
     */
    public static class MethodInfoHelper
    {
        private final Map m_key2Method = new HashMap();
        
        /**
         * Pending
         * 
         * @param class1
         */
        public MethodInfoHelper(Class targetClass)
        {
            final Method[] methods = targetClass.getMethods();
            
            for (int i = 0; i < methods.length; i++)
            {
                Method method = methods[i];
                m_key2Method.put( buildKey( method ), method );
            }
        }
        
        /**
         * Pending
         * 
         * @param javaMethod
         * @return
         */
        public Method obtainRealMethod( JavaMethod javaMethod )
        {
            return (Method) m_key2Method.get( buildKey( javaMethod ) );
        }

        /**
         * Pending
         * 
         * @param method
         * @return
         */
        private String buildKey(final Method method)
        {
            StringBuffer sb = new StringBuffer();
            sb.append( method.getReturnType().getName() );
            sb.append( ' ' );
            sb.append( method.getDeclaringClass().getName() );
            sb.append( ' ' );
            sb.append( method.getName() );

            Class[] parameters = method.getParameterTypes();

            for (int i = 0; i < parameters.length; i++)
            {
                Class parameter = parameters[i];
                sb.append( ' ' );
                sb.append( parameter.getName() );
            }
            
            return sb.toString();
        }

        /**
         * Pending
         * 
         * @param method
         * @return
         */
        private String buildKey(final JavaMethod method)
        {
            StringBuffer sb = new StringBuffer();
            sb.append( method.getReturns().getValue() );
            sb.append( ' ' );
            sb.append( method.getParentClass().getFullyQualifiedName() );
            sb.append( ' ' );
            sb.append( method.getName() );

            JavaParameter[] parameters = method.getParameters();

            for (int i = 0; i < parameters.length; i++)
            {
                JavaParameter parameter = parameters[i];
                sb.append( ' ' );
                sb.append( parameter.getType().getValue() );
            }
            
            return sb.toString();
        }
    }
}
