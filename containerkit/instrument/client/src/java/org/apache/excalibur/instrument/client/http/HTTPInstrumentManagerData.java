/* 
 * Copyright 2002-2004 The Apache Software Foundation
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

package org.apache.excalibur.instrument.client.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;

import org.apache.excalibur.instrument.client.InstrumentableData;
import org.apache.excalibur.instrument.client.InstrumentManagerData;

class HTTPInstrumentManagerData
    extends AbstractHTTPData
    implements InstrumentManagerData
{
    /* Name of the remote object. */
    private String m_name;
    
    private List m_instrumentables = new ArrayList();
    private HTTPInstrumentableData[] m_instrumentableAry;
    private Map m_instrumentableMap = new HashMap();
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new HTTPInstrumentManagerData.
     */
    HTTPInstrumentManagerData( HTTPInstrumentManagerConnection connection )
    {
        super( connection, connection.getURL().toExternalForm() );
        
        m_name = connection.getURL().toExternalForm();
    }
    
    /*---------------------------------------------------------------
     * AbstractHTTPData Methods
     *-------------------------------------------------------------*/
    /**
     * Update the contents of the object using values from the Configuration object.
     *
     * @param configuration Configuration object to load from.
     * @param recurse True if state should be ignored and we should drill down
     *                using data in this configuration.
     *
     * @throws ConfigurationException If there are any problems.
     */
    protected void update( Configuration configuration, boolean recurse )
        throws ConfigurationException
    {
        super.update( configuration );
        
        m_name = configuration.getAttribute( "name" );
        
        if ( getLogger().isDebugEnabled() )
        {
            getLogger().debug(
                "Updated InstrumentManager '" + getName() + "' to version " + getStateVersion() );
        }
        
        Configuration[] instrumentableConfs = configuration.getChildren( "instrumentable" );
        for ( int i = 0; i < instrumentableConfs.length; i++ )
        {
            Configuration iaConf = instrumentableConfs[i];
            String iaName = iaConf.getAttribute( "name" );
            int iaStateVersion = iaConf.getAttributeAsInteger( "state-version" );
            
            HTTPInstrumentableData iaData;
            synchronized ( m_instrumentables )
            {
                iaData = (HTTPInstrumentableData)m_instrumentableMap.get( iaName );
                if ( iaData == null )
                {
                    // It is new.
                    iaData = new HTTPInstrumentableData( this, iaName );
                    iaData.enableLogging( getLogger().getChildLogger( iaName ) );
                    m_instrumentables.add( iaData );
                    m_instrumentableAry = null;
                    m_instrumentableMap.put( iaName, iaData );
                }
            }
            
            if ( recurse )
            {
                iaData.update( iaConf, recurse );
            }
            else
            {
                if ( iaStateVersion != iaData.getStateVersion() )
                {
                    // Needs to be updated.
                    iaData.update();
                }
            }
        }
    }
    
    /**
     * Causes the InstrumentManagerData to update itself with the latest data
     *  from the server.
     *
     * @return true if successful.
     */
    public boolean update()
    {
        HTTPInstrumentManagerConnection connection =
            (HTTPInstrumentManagerConnection)getConnection();
        
        Configuration configuration = connection.getState( "instrument-manager.xml?packed=true" );
        if ( configuration != null )
        {
            try
            {
                update( configuration, false );
                
                //updateLeasedSamples();
                
                return true;
            }
            catch ( ConfigurationException e )
            {
                getLogger().debug( "Unable to update.", e );
            }
        }
        return false;
    }
    
    /*---------------------------------------------------------------
     * InstrumentManagerData Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the name.
     *
     * @return The name.
     */
    public String getName()
    {
        return m_name;
    }
    
    /**
     * Gets a thread-safe snapshot of the instrumentable list.
     *
     * @return A thread-safe snapshot of the instrumentable list.
     */
    public InstrumentableData[] getInstrumentables()
    {
        HTTPInstrumentableData[] instrumentables = m_instrumentableAry;
        if ( instrumentables == null )
        {
            synchronized ( m_instrumentables )
            {
                m_instrumentableAry = new HTTPInstrumentableData[m_instrumentables.size()];
                m_instrumentables.toArray( m_instrumentableAry );
                instrumentables = m_instrumentableAry;
            }
        }
        return instrumentables;
    }
    
    /**
     * Causes the the entire instrument tree to be updated in one call.  Very fast
     *  when it is known that all or most data has changed.
     *
     * @return true if successful.
     */
    public boolean updateAll()
    {
        HTTPInstrumentManagerConnection connection =
            (HTTPInstrumentManagerConnection)getConnection();
        
        Configuration configuration =
            connection.getState( "instrument-manager.xml?packed=true&recurse=true" );
        if ( configuration != null )
        {
            try
            {
                update( configuration, true );
                
                //updateLeasedSamples();
                
                return true;
            }
            catch ( ConfigurationException e )
            {
                getLogger().debug( "Unable to update.", e );
            }
        }
        return false;
    }
    
    /**
     * Requests that a sample be created or that its lease be updated.
     *
     * @param instrumentName The full name of the instrument whose sample is
     *                       to be created or updated.
     * @param description Description to assign to the new sample.
     * @param interval Sample interval of the new sample.
     * @param sampleCount Number of samples in the new sample.
     * @param leaseTime Requested lease time.  The server may not grant the full lease.
     * @param sampleType The type of sample to be created.
     */
    public void createInstrumentSample( String instrumentName,
                                        String description,
                                        long interval,
                                        int sampleCount,
                                        long leaseTime,
                                        int sampleType )
    {
        HTTPInstrumentManagerConnection connection =
            (HTTPInstrumentManagerConnection)getConnection();
        
        connection.getState( "create-sample.xml?name=" + urlEncode( instrumentName )
            + "&description=" + urlEncode( description ) + "&interval=" + interval
            + "&size=" + sampleCount + "&lease=" + leaseTime + "&type=" + sampleType );
    }
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}