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

package org.apache.excalibur.instrument.manager.impl;

/**
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 */
class XMLUtil
{
    /*---------------------------------------------------------------
     * Static Methods
     *-------------------------------------------------------------*/
    public static String makeSafeAttribute( String attribute )
    {
        // TODO:
        return attribute;
    }
    
    public static String buildLine( String indent, boolean packed, String line )
    {
        if ( packed )
        {
            return line;
        }
        else
        {
            return indent + line + "\n";
        }
    }
    
    /*---------------------------------------------------------------
     * Constructors
     *-------------------------------------------------------------*/
    /**
     * Not instantiable.
     */
    private XMLUtil()
    {
    }
}

