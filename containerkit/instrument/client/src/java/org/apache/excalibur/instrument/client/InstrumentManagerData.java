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

package org.apache.excalibur.instrument.client;

public interface InstrumentManagerData
    extends Data
{
    /**
     * Returns the name.
     *
     * @return The name.
     */
    String getName();
    
    /**
     * Gets a thread-safe snapshot of the instrumentable list.
     *
     * @return A thread-safe snapshot of the instrumentable list.
     */
    InstrumentableData[] getInstrumentables();
    
    /**
     * Causes the the entire instrument tree to be updated in one call.  Very fast
     *  when it is known that all or most data has changed.
     *
     * @return true if successful.
     */
    boolean updateAll();
    
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
    void createInstrumentSample( String instrumentName,
                                 String description,
                                 long interval,
                                 int sampleCount,
                                 long leaseTime,
                                 int sampleType );
}
