/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.excalibur.source.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.excalibur.source.Source;

/**
 * A factory for source types supported by
 * <a href="http://jakarta.apache.org/commons/sandbox/vfs">Commons VFS</a>.
 *
 * @avalon.component
 * @avalon.service type=SourceFactory
 * @x-avalon.info name=commons-vfs-source
 * @x-avalon.lifestyle type=singleton
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id$
 */
public class CommonsVFSSourceFactory extends org.apache.excalibur.source.factories.CommonsVFSSourceFactory
    implements ThreadSafe, LogEnabled
{
    /**
     * Returns a {@link CommonsVFSSource} instance primed with the specified location
     *
     * @param location source location
     * @param parameters source parameters
     * @throws IOException if an IO error occurs
     * @throws MalformedURLException if a URL is malformed
     * @see org.apache.excalibur.source.SourceFactory#getSource(java.lang.String, java.util.Map)
     */
    public Source getSource( String location, Map parameters ) throws IOException, MalformedURLException
    {
        final Source source = new CommonsVFSSource( location, parameters );
        ContainerUtil.enableLogging(source, m_logger);
        return source;
    }

    /**
     * Releases the given source.
     *
     * @param source source to release
     * @see org.apache.excalibur.source.SourceFactory#release(org.apache.excalibur.source.Source)
     */
    public void release( Source source )
    {
        // Nothing to do here
    }

    /**
     * Enables logging for this source.
     *
     * @param logger {@link Logger} instance to use
     * @see org.apache.avalon.framework.logger.LogEnabled
     *      #enableLogging(org.apache.avalon.framework.logger.Logger)
     */
    public void enableLogging(final Logger logger) {
        m_logger = logger;
    }

    /** Our logging target */
    private Logger m_logger;
}
