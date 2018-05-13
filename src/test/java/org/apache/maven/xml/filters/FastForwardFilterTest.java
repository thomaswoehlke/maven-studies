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

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.helpers.XMLFilterImpl;

public class FastForwardFilterTest extends AbstractXMLFilterTests
{
    
    private XMLFilter filter;

    @Before
    public void setUp() throws Exception
    {
        filter = new PrefixTagsFilter( "touched_" );
        XMLFilter filter1 = new FastForwardFilter();
        filter.setParent( filter1 );
    }

    @Test
    public void testProjectReports() throws Exception
    {
        String input = "<project><reports><project><reports>"
                        + "<SIMPLE/>"
                        + "</reports></project></reports></project>";
        String expected = "<touched_project><touched_reports><project><reports>"
                        + "<SIMPLE/>"
                        + "</reports></project></touched_reports></touched_project>";
        
        String actual = transform( input, filter );
        
        assertEquals( expected, actual );
    }

    @Test
    public void testProfileReports() throws Exception
    {
        String input = "<project><profile><reports><project><reports>"
                        + "<SIMPLE/>"
                        + "</reports></project></reports></profile></project>";
        String expected = "<touched_project><touched_profile><touched_reports><project><reports>"
                        + "<SIMPLE/>"
                        + "</reports></project></touched_reports></touched_profile></touched_project>";
        
        String actual = transform( input, filter );
        
        assertEquals( expected, actual );
    }

    @Test
    public void testPluginConfiguration() throws Exception
    {
        String input = "<project><build><plugins>"
            + "<plugin><configuration><plugin><configuration>"
            + "<SIMPLE/>"
            + "</configuration></plugin></configuration></plugin>"
            + "</plugins></build></project>";
        String expected = "<touched_project><touched_build><touched_plugins><touched_plugin>"
                        + "<touched_configuration><plugin><configuration>"
                        + "<SIMPLE/>"
                        + "</configuration></plugin></touched_configuration>"
                        + "</touched_plugin></touched_plugins></touched_build></touched_project>";

        String actual = transform( input, filter );

        assertEquals( expected, actual );
    }

    @Test
    public void testPluginGoals() throws Exception
    {
        String input = "<project><build><plugins>"
            + "<plugin><goals><plugin><goals>"
            + "<SIMPLE/>"
            + "</goals></plugin></goals></plugin>"
            + "</plugins></build></project>";
        String expected = "<touched_project><touched_build><touched_plugins><touched_plugin>"
                        + "<touched_goals><plugin><goals>"
                        + "<SIMPLE/>"
                        + "</goals></plugin></touched_goals>"
                        + "</touched_plugin></touched_plugins></touched_build></touched_project>";

        String actual = transform( input, filter );

        assertEquals( expected, actual );
    }

    @Test
    public void testExecutionConfiguration() throws Exception
    {
        String input = "<project><build><plugins><plugin><executions>"
                        + "<execution><configuration><execution><configuration>"
                        + "<SIMPLE/>"
                        + "</configuration></execution></configuration></execution>"
                        + "</executions></plugin></plugins></build></project>";
        String expected = "<touched_project><touched_build><touched_plugins><touched_plugin><touched_executions>"
                        + "<touched_execution><touched_configuration><execution><configuration>"
                        + "<SIMPLE/>"
                        + "</configuration></execution></touched_configuration></touched_execution>"
                        + "</touched_executions></touched_plugin></touched_plugins></touched_build></touched_project>";
        
        String actual = transform( input, filter );
        
        assertEquals( expected, actual );
    }

    @Test
    public void testReportSetConfiguration() throws Exception
    {
        String input = "<project><reporting><plugins><plugin><reportSets>"
                        + "<reportSet><configuration><reportSet><configuration>"
                        + "<SIMPLE/>"
                        + "</configuration></reportSet></configuration></reportSet>"
                        + "</reportSets></plugin></plugins></reporting></project>";
        String expected = "<touched_project><touched_reporting><touched_plugins><touched_plugin><touched_reportSets>"
                        + "<touched_reportSet><touched_configuration><reportSet><configuration>"
                        + "<SIMPLE/>"
                        + "</configuration></reportSet></touched_configuration></touched_reportSet>"
                        + "</touched_reportSets></touched_plugin></touched_plugins></touched_reporting></touched_project>";
        
        String actual = transform( input, filter );
        
        assertEquals( expected, actual );
    }

    
    class PrefixTagsFilter extends XMLFilterImpl
    {
        private final String prefix;
        
        public PrefixTagsFilter( String prefix )
        {
            this.prefix = prefix;
        }
        
        @Override
        public void startElement( String uri, String localName, String qName, Attributes atts )
            throws SAXException
        {
            super.startElement( uri, localName, prefix + qName, atts );
        }
        
        @Override
        public void endElement( String uri, String localName, String qName )
            throws SAXException
        {
            super.endElement( uri,  localName, prefix + qName );
        }
    }
}
