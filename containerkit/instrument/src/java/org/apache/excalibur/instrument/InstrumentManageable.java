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

package org.apache.excalibur.instrument;

/**
 * Components which implement the InstrumentManageable Interface will have
 *  their InstrumentManager set by the component manager during their
 *  initialization phase.  Components which create child components needs to
 *  implement this interface in order for Instruments to be supported within
 *  those child components.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:28 $
 * @since 4.0
 */
public interface InstrumentManageable
{
    /**
     * Sets the InstrumentManager for child components.  Can be for special
     * purpose components, however it is used mostly internally.
     *
     * @param instrumentManager The InstrumentManager for the component to use.
     */
    void setInstrumentManager( InstrumentManager instrumentManager );
}