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

import java.io.InputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

import org.apache.excalibur.instrument.client.InstrumentableData;
import org.apache.excalibur.instrument.client.InstrumentManagerConnection;
import org.apache.excalibur.instrument.client.InstrumentManagerData;

/**
 * A Connection to the remote InstrumentManager which connects using
 *  the HTTP connector.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:23 $
 * @since 4.1
 */
public class HTTPInstrumentManagerConnection
    extends InstrumentManagerConnection
{
    private URL m_url;
    
    /** Flag which keeps track of whether or not the remote server was there
    *   the last time we attempted to connect. */
    private boolean m_connected;
    
    /** If we ever decide that we are not talking to an Instrument Manager then
     *   disable the connection to avoid pounding the remote server with lots
     *   of 404 requests. */
    private boolean m_disabled;
    
    private HTTPInstrumentManagerData m_manager;
    
    private List m_leasedSamples = new ArrayList();
    private HTTPInstrumentSampleData[] m_leasedSampleAry;

    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new HTTPInstrumentManagerConnection.
     */
    public HTTPInstrumentManagerConnection( URL url )
    {
        m_url = url;
        m_connected = false;
        
        m_manager = new HTTPInstrumentManagerData( this );
    }
    
    /*---------------------------------------------------------------
     * InstrumentManagerConnection Methods
     *-------------------------------------------------------------*/
    public void enableLogging( Logger logger )
    {
        super.enableLogging( logger );
        m_manager.enableLogging( logger.getChildLogger( "manager" ) );
    }
    
    /**
     * Returns the key used to identify this object.
     *
     * @return The key used to identify this object.
     */
    public Object getKey()
    {
        return m_url;
    }
    
    /**
     * Returns true if connected.
     *
     * @return True if connected.
     */
    public boolean isConnected()
    {
        return m_connected;
    }
    
    /**
     * Returns the Instrument Manager.
     *
     * @return The Instrument Manager.
     */
    public InstrumentManagerData getInstrumentManager()
    {
        return m_manager;
    }
    
    /**
     * Returns the title to display in the tab for the connection.
     *
     * @return The tab title.
     */
    public String getTabTitle()
    {
        if ( m_disabled )
        {
            return "[DISABLED] " + super.getTabTitle();
        }
        else
        {
            return super.getTabTitle();
        }
    }

    /**
     * Invokes GC on the JVM running the InstrumentManager.
     */
    protected void invokeGC()
    {
        getState( "gc.xml" );
    }

    /**
     * Saves the current state into a Configuration.
     *
     * @return The state as a Configuration.
     */
    public Configuration saveState()
    {
        synchronized( this )
        {
            DefaultConfiguration state = (DefaultConfiguration)super.saveState();
            
            state.setAttribute( "url", m_url.toExternalForm() );
            
            return state;
        }
    }

    /**
     * Loads the state from a Configuration object.
     *
     * @param state Configuration object to load state from.
     *
     * @throws ConfigurationException If there were any problems loading the
     *                                state.
     */
    public void loadState( Configuration state )
        throws ConfigurationException
    {
        synchronized( this )
        {
            super.loadState( state );
            
            // URL will have already been set.
        }
    }
    
    /**
     * Gets a thread-safe snapshot of the leased sample list.
     *
     * @return A thread-safe snapshot of the leased sample list.
     */
    /*
    public InstrumentSampleData[] getLeasedSamples()
    {
        InstrumentSampleData[] samples = m_leasedSampleAry;
        if ( samples == null )
        {
            synchronized ( m_leasedSamples )
            {
                m_leasedSampleAry = new InstrumentSampleData[m_leasedSamples.size()];
                m_leasedSamples.toArray( m_leasedSampleAry );
                samples = m_leasedSampleAry;
            }
        }
        return samples;
    }
    */
    
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
    /**
     * Returns the URL of the remote InstrumentManager.
     *
     * @return The URL of the remote InstrumentManager.
     */
    URL getURL()
    {
        return m_url;
    }
    
    /**
     * Requests the current state of the object at the specified path.
     *  If the request fails for any reason, including not being valid
     *  content then the method will return null.
     *
     * @param path The path of the object whose state is requested.
     *
     * @return The state as a Configuration object, or null if it failed.
     */
    Configuration getState( String path )
    {
        if ( m_disabled )
        {
            return null;
        }
        
        URL url;
        try
        {
            url = new URL( m_url, path );
        }
        catch ( MalformedURLException e )
        {
            getLogger().debug( "Request failed.", e );
            return null;
        }
        
        try
        {
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            
            m_connected = true;
            
            if ( conn.getResponseCode() == conn.HTTP_OK )
            {
                InputStream is = conn.getInputStream();
                try
                {
                    DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
                    try
                    {
                        return builder.build( is );
                    }
                    catch ( ConfigurationException e )
                    {
                        getLogger().warn( "Invalid XML reveived from the server.", e );
                        return null;
                    }
                    catch ( org.xml.sax.SAXException e )
                    {
                        getLogger().warn( "Invalid XML reveived from the server.", e );
                        return null;
                    }
                }
                finally
                {
                    is.close();
                }
            }
            else
            {
                if ( ( conn.getResponseCode() == 404 )
                    && path.startsWith( "instrument-manager.xml" ) )
                {
                    getLogger().warn( "Requested " + url + " resulted in error code 404.  "
                        + "Most likely not an Instrument Manager, disabling future requests." );
                    m_disabled = true;
                }
                else
                {
                    getLogger().debug( "Response: " + conn.getResponseCode() + " : "
                        + conn.getResponseMessage() );
                }
                return null;
            }
        }
        catch ( IOException e )
        {
            String msg = e.getMessage();
            if ( msg == null )
            {
                msg = e.toString();
            }
            
            if ( msg.indexOf( "Connect" ) >= 0 )
            {
                // Hide the stack trace as the server is simply down.
                getLogger().debug( "Request failed.  URL: " + url + "  Error: " + msg );
            }
            else
            {
                getLogger().debug( "Request failed.  URL: " + url + "  Error: ", e );
            }
            
            m_connected = false;
            return null;
        }
    }
    
    /*
    private void updateLeasedSamples()
    {
        InstrumentSampleData[] samples = getLeasedSamples();
        for ( int i = 0; i < samples.length; i++ )
        {
            InstrumentSampleData sample = samples[i];
            sample.updateLease();
        }
    }
    */
}