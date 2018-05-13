package org.apache.maven.xml.filters;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * 
 * @author Robert Scholte
 * @since 4.0.0
 *
 */
public class ModelFilter
    extends XMLFilterImpl
{
    private final XMLFilterChainBuilder builder;

    public ModelFilter() throws SAXException
    {
        this( XMLReaderFactory.createXMLReader() );
    }

    public ModelFilter( XMLReader parent )
    {
        builder = new XMLFilterChainBuilder( parent );
        
        setParent( builder.build() );
    }

    public ModelFilter addFilter( XMLFilter filter )
    {
        builder.addFilter( filter );
        
        setParent( builder.build() );
        
        return this;
    }

    private static class XMLFilterChainBuilder
    {
        private XMLReader parent;

        XMLFilterChainBuilder( XMLReader parent )
        {
            this.parent = parent;
        }

        XMLFilterChainBuilder addFilter( XMLFilter filter )
        {
            filter.setParent( parent );
            parent = filter;
            return this;
        }

        XMLReader build()
        {
            return parent;
        }
    }
}
