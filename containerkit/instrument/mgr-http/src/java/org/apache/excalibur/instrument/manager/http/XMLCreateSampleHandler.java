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

package org.apache.excalibur.instrument.manager.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.excalibur.instrument.manager.http.server.HTTPRedirect;
import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.instrument.manager.InstrumentDescriptor;
import org.apache.excalibur.instrument.manager.InstrumentSampleDescriptor;
import org.apache.excalibur.instrument.manager.NoSuchInstrumentException;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/03/06 14:01:28 $
 * @since 4.1
 */
public class XMLCreateSampleHandler
    extends AbstractXMLHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new XMLCreateSampleHandler.
     *
     * @param manager Reference to the DefaultInstrumentManager.
     */
    public XMLCreateSampleHandler( DefaultInstrumentManager manager )
    {
        super( "/create-sample.xml", manager );
    }
    
    /*---------------------------------------------------------------
     * AbstractHTTPURLHandler Methods
     *-------------------------------------------------------------*/
    /**
     * Handles the specified request.
     *
     * @param The full path being handled.
     * @param parameters A Map of the parameters in the request.
     * @param os The PrintWriter to write the result to.
     */
    public void doGet( String path, Map parameters, PrintWriter out )
        throws IOException
    {
        String name = getParameter( parameters, "name" );
        String description = getParameter( parameters, "description" );
        long interval = getLongParameter( parameters, "interval" );
        int size = getIntegerParameter( parameters, "size" );
        long lease = getLongParameter( parameters, "lease" );
        int type = getIntegerParameter( parameters, "type" );
        boolean packed = ( getParameter( parameters, "packed", null ) != null );
        
        InstrumentDescriptor desc;
        try
        {
            desc = getInstrumentManager().locateInstrumentDescriptor( name );
        }
        catch ( NoSuchInstrumentException e )
        {
            throw new FileNotFoundException(
                "The specified instrument does not exist: " + name );
        }
        
        // Register the new lease
        InstrumentSampleDescriptor sample =
            desc.createInstrumentSample( description, interval, size, lease, type );
        
        out.println( InstrumentManagerHTTPConnector.XML_BANNER );
        outputSample( out, sample, "", packed );
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}
