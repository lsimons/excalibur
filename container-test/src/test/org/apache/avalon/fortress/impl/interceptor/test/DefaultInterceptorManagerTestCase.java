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

package org.apache.avalon.fortress.impl.interceptor.test;

import org.apache.avalon.fortress.ContainerManager;
import org.apache.avalon.fortress.impl.DefaultContainerManager;
import org.apache.avalon.fortress.impl.InterceptorEnabledContainer;
import org.apache.avalon.fortress.impl.interceptor.TailInterceptor;
import org.apache.avalon.fortress.impl.interceptor.test.components.CustomerDataAccessObject;
import org.apache.avalon.fortress.impl.interceptor.test.examples.ValidInterceptor;
import org.apache.avalon.fortress.interceptor.Interceptor;
import org.apache.avalon.fortress.interceptor.InterceptorManager;
import org.apache.avalon.fortress.util.FortressConfig;
import org.apache.avalon.framework.container.ContainerUtil;

import junit.framework.TestCase;

/**
 * Pending
 * 
 * @author <a href="mailto:dev@excalibur.apache.org">Excalibur Development Team</a>
 */
public class DefaultInterceptorManagerTestCase extends TestCase
{
    private InterceptorEnabledContainer m_container;
    private InterceptorManager m_interManager;
    
    /**
     * Constructor for DefaultInterceptorManagerTestCase.
     * @param name
     */
    public DefaultInterceptorManagerTestCase(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        m_container = createContainer();
        m_interManager = m_container.getInterceptorManager();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
        
        m_container.dispose();
    }

    public void testAddInterceptor() throws Exception
    {
        m_interManager.add( "dao", "key", ValidInterceptor.class.getName() );
        String[] families = m_interManager.getFamilies();
        
        assertNotNull( families );
        assertEquals( 1, families.length );
        assertEquals( "dao", families[0] );
    }

    public void testRemoveInterceptor() throws Exception
    {
        testAddInterceptor();
        
        m_interManager.remove( "dao", "key" );
        String[] families = m_interManager.getFamilies();
        
        assertNotNull( families );
        assertEquals( 0, families.length );
    }

    public void testChain() throws Exception
    {
        testAddInterceptor();
        
        Interceptor interceptor = m_interManager.buildChain( "dao" );
        assertNotNull( interceptor );
        assertEquals( ValidInterceptor.class, interceptor.getClass() );
        assertNotNull( interceptor.getNext() );
        
        // Setting the next 
        
        interceptor = interceptor.getNext();
        assertNotNull( interceptor );
        assertEquals( TailInterceptor.class, interceptor.getClass() );
        assertNull( interceptor.getNext() );
    }

    private InterceptorEnabledContainer createContainer() throws Exception
    {
        final FortressConfig config = new FortressConfig();
        config.setContainerClass( InterceptorEnabledContainer.class );
        config.setContextDirectory( "./" );
        config.setWorkDirectory( "./" );
        
        final String BASE = "resource://org/apache/avalon/fortress/test/data/";
        config.setContainerConfiguration( BASE + "test1.xconf" );
        config.setLoggerManagerConfiguration( BASE + "test1.xlog" );

        final ContainerManager cm = new DefaultContainerManager( config.getContext() );
        ContainerUtil.initialize( cm );

        return (InterceptorEnabledContainer) cm.getContainer();
    }
    
    public void testGetComponent() throws Exception
    {
        testAddInterceptor();
        
        m_container.get( CustomerDataAccessObject.ROLE, "*" );
        
        
    }

}
