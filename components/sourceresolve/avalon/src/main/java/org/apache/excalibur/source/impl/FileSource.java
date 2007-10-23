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

import java.io.File;
import java.net.MalformedURLException;

import org.apache.excalibur.source.SourceException;


/**
 * This class is here for compatibility with 2.x.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version $Id$
 */
public class FileSource extends org.apache.excalibur.source.factories.FileSource
{

    public FileSource(String scheme, File file) throws SourceException {
        super(scheme, file);
    }

    public FileSource(String uri) throws SourceException, MalformedURLException {
        super(uri);
    }
}
