/*
 * Created on 05/07/2004
 *
 */
package org.apache.avalon.fortress.tools.test;

import java.io.File;
import java.util.Collections;

import junit.framework.TestCase;

import org.apache.avalon.fortress.tools.Component;

import com.thoughtworks.qdox.model.JavaClass;

/**
 * Pending
 * 
 * @author hammett
 */
public class ComponentTestCase extends TestCase
{
    private static final String COMPONENT_TYPE_NAME = "org.apache.avalon.fortress.tools.ServiceImpl";
    
    private Component m_component;
    private final File m_root = new File("./tempfiles");

    public ComponentTestCase(String name)
    {
        super(name);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
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
        
        m_component = new Component( model );
        
        m_root.mkdirs();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();

        m_root.delete();
    }

    public void testGetType()
    {
        assertEquals( COMPONENT_TYPE_NAME, m_component.getType() );
    }

    public void testSerialize() throws Exception
    {
        m_component.serialize( m_root );
        
        String fileName = COMPONENT_TYPE_NAME.replace( '.', '/' ).concat( ".meta" );
        
        File file = new File( m_root, fileName );
        
        assertTrue( file.exists() );
    }
}