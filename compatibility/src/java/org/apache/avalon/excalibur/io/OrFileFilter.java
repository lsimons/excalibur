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
package org.apache.avalon.excalibur.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Accepts a selection if it is acceptable to either of two {@link FilenameFilter}s.
 * This takes two {@link FilenameFilter}s as input.
 *
 * <p>Eg., to print all directories or <code>*.gif</code> files in the current directory:</p>
 *
 * <pre>
 * File dir = new File(".");
 * String[] files = dir.list( new OrFileFilter(
 *         new DirectoryFileFilter(),
 *         new ExtensionFileFilter(".gif")
 *         )
 *     );
 * for ( int i=0; i&lt;files.length; i++ )
 * {
 *     System.out.println(files[i]);
 * }
 * </pre>
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.5 $ $Date: 2004/04/26 10:23:06 $
 * @since 4.0
 */
public class OrFileFilter
        implements FilenameFilter
{
    private final FilenameFilter m_filter1;
    private final FilenameFilter m_filter2;

    public OrFileFilter( final FilenameFilter filter1, final FilenameFilter filter2 )
    {
        m_filter1 = filter1;
        m_filter2 = filter2;
    }

    public boolean accept( final File file, final String name )
    {
        return m_filter1.accept( file, name ) || m_filter2.accept( file, name );
    }
}



