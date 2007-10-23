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

import java.util.Map;

import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.vfs.FileSystemException;

/**
 * Source implementation that provides resolver access to all protocols
 * supported by <a href="http://jakarta.apache.org/commons/sandbox/vfs">Commons VFS</a>.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id$
 * @since Nov 19, 2004 10:54:02 AM
 */
public class CommonsVFSSource extends org.apache.excalibur.source.factories.CommonsVFSSource
    implements LogEnabled {

    public CommonsVFSSource(String location, Map parameters)
            throws FileSystemException {
        super(location, parameters);
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
