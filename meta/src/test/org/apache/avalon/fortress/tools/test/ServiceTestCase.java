/*
 * Created on 05/07/2004
 *
 */
package org.apache.avalon.fortress.tools.test;

import java.io.File;
import java.util.Collections;

import org.apache.avalon.fortress.tools.Component;
import org.apache.avalon.fortress.tools.Service;

import com.thoughtworks.qdox.model.JavaClass;

import junit.framework.TestCase;

/**
 * Pending
 * 
 * @author hammett
 */
public class ServiceTestCase extends TestCase
{
    private static final String TYPE_NAME = "org.apache.avalon.fortress.tools.Service";
    private static final String COMPONENT_TYPE_NAME = "org.apache.avalon.fortress.tools.ServiceImpl";
    
    private Service m_service;
    private File m_root;

    public ServiceTestCase(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        m_service = new Service( TYPE_NAME );
        
        m_root = new File("./tempfiles");
        m_root.mkdirs();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        m_root.delete();
    }

    public void testGetType()
    {
        assertEquals( TYPE_NAME, m_service.getType() );
    }

    public void testAddComponent()
    {
        JavaClass model = new JavaClass()
        {
            public String getFullyQualifiedName()
            {
                return getName();
            }

            public JavaClass getSuperJavaClass()
            {
                return null;
            }
        };
        model.setName( COMPONENT_TYPE_NAME );
        model.setTags( Collections.EMPTY_LIST );
        
        Component component = new Component( model );
        m_service.addComponent( component );
        
        assertNotNull( m_service.getComponents() );
        assertTrue( m_service.getComponents().hasNext() );
    }

    public void testSerialize() throws Exception
    {
        testAddComponent();
        
        m_service.serialize( m_root );
        
        File file = new File( m_root, "META-INF/services/" + TYPE_NAME );
       
        assertTrue( file.exists() );
    }
}
