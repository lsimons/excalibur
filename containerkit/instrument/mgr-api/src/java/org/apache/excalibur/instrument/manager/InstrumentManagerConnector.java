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

package org.apache.excalibur.instrument.manager;

/**
 * Interface for classes which can be registered as Connectors for
 *  InstrumentManagers.
 *
 * The DefaultInstrumentManager is smart about handling connectors which
 *  implement the LogEnabled, Configurable, Initializable, Startable and
 *  Disposable interfaces.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
public interface DefaultInstrumentManagerConnector
{
    /**
     * Set the InstrumentManager to which the Connecter will provide
     *  access.  This method is called before the new connector is
     *  configured or started.
     */
    //void setInstrumentManager( DefaultInstrumentManager manager );
}
