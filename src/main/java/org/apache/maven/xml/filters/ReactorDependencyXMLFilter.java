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

import java.util.function.Function;

import org.apache.maven.xml.SAXEventUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Will apply the version if the dependency is part of the reactor
 * 
 * @author Robert Scholte
 * @since 4.0.0
 */
public class ReactorDependencyXMLFilter extends XMLFilterImpl
{

    // states
    private static final int GROUPID = 1;

    private static final int ARTIFACTID = 2;

    private static final int OTHER = 0;

    private int state;

    private boolean hasVersion;

    private String groupId;

    private String artifactId;

    private final Function<String, String> reactorVersionMapper;

    public ReactorDependencyXMLFilter( Function<String, String> reactorVersionMapper )
    {
        this.reactorVersionMapper = reactorVersionMapper;
    }

    @Override
    public void startElement( String uri, String localName, String qName, Attributes atts )
        throws SAXException
    {
        if ( "groupId".equals( localName ) )
        {
            state = GROUPID;
        }
        else if ( "artifactId".equals( localName ) )
        {
            state = ARTIFACTID;
        }
        else
        {
            state = OTHER;
        }

        if ( "version".equals( localName ) )
        {
            hasVersion = true;
        }
        super.startElement( uri, localName, qName, atts );
    }

    @Override
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        if ( state == GROUPID )
        {
            groupId = new String( ch, start, length );
        }
        else if ( state == ARTIFACTID )
        {
            artifactId = new String( ch, start, length );
        }
        super.characters( ch, start, length );
    }

    @Override
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        if ( "dependency".equals( localName ) && !hasVersion )
        {
            String version = getVersion();

            // dependency is not part of reactor, probably it is managed
            if ( version != null )
            {
                String versionQName = SAXEventUtils.renameQName( qName, "version" );
                super.startElement( uri, "version", versionQName, null );
                super.characters( version.toCharArray(), 0, version.length() );
                super.endElement( uri, "version", versionQName );
            }
        }
        super.endElement( uri, localName, qName );
    }

    private String getVersion()
    {
        return reactorVersionMapper.apply( groupId + ':' + artifactId );
    }
    
}
