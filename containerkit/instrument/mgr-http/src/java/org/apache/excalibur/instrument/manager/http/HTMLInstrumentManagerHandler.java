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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.excalibur.instrument.manager.DefaultInstrumentManager;
import org.apache.excalibur.instrument.manager.InstrumentableDescriptor;

/**
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.6 $ $Date: 2004/03/06 14:01:28 $
 * @since 4.1
 */
public class HTMLInstrumentManagerHandler
    extends AbstractHTMLHandler
{
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Creates a new HTMLInstrumentManagerHandler.
     *
     * @param manager Reference to the DefaultInstrumentManager.
     */
    public HTMLInstrumentManagerHandler( DefaultInstrumentManager manager )
    {
        super( "/instrument-manager.html", manager );
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
        long oldMemory = getLongParameter( parameters, "oldMemory", 0 );
        long newMemory = getLongParameter( parameters, "newMemory", 0 );
        
        String gcLabel = "<a href='gc.html'>Perform Garbage Collection</a>";
        if ( ( oldMemory != 0 ) && ( newMemory != 0 ) )
        {
            gcLabel = gcLabel + " (Freed: " + ( oldMemory - newMemory ) + "bytes.  "
                + "Now " + newMemory + " bytes.";
        }
        
        // This is the root
        out.println( "<html>" );
        out.println( "<head><title>" + getInstrumentManager().getDescription()
            + "</title></head>" );
        out.println( "<body>" );
        
        breadCrumbs( out, false );

        
        out.println( "<h2>Instrument Manager</h2>" );
        startTable( out );
        tableRow( out, 0, "Name", getInstrumentManager().getName() );
        tableRow( out, 0, "Description", getInstrumentManager().getDescription() );
        tableRow( out, 0, "GC", gcLabel );
        endTable( out );
        
        InstrumentableDescriptor[] instrumentables =
            getInstrumentManager().getInstrumentableDescriptors();
        if ( instrumentables.length > 0 )
        {
            out.println( "<h2>Instrumentables</h2>" );
            outputInstrumentables( out, instrumentables );
        }
        
        footer( out );
        
        out.println( "</body>" );
        out.println( "</html>" );
    }
            
    /*---------------------------------------------------------------
     * Methods
     *-------------------------------------------------------------*/
}
