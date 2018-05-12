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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * This filter will skip all chained filter and write directly to the output
 * 
 * @author Robert Scholte
 * @since 4.0.0
 */
public class FastForwardFilter extends XMLFilterImpl
{
    private int reports = 0;
    
    private ContentHandler originalHandler;

    @Override
    public void startElement( String uri, String localName, String qName, Attributes atts )
        throws SAXException
    {
        if ( "reports".equals( localName ) )
        {
            reports++;
            originalHandler = getContentHandler();

            ContentHandler outputContentHandler = getContentHandler();
            while ( outputContentHandler instanceof XMLFilter )
            {
                outputContentHandler = ( (XMLFilter) outputContentHandler ).getContentHandler();
            }
            setContentHandler( outputContentHandler );
        }
        super.startElement( uri, localName, qName, atts );
    }
    
    @Override
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        if ( "reports".equals( localName ) )
        {
            reports--;
            
            if ( reports == 0 )
            {
                setContentHandler( originalHandler );
            }
        }
        
        super.endElement( uri, localName, qName );
    }
    
    
}
