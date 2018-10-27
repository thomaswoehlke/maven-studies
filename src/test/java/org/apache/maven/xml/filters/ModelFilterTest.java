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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author Robert Scholte
 * @since 4.0.0
 */
public class ModelFilterTest extends AbstractXMLFilterTests
{
    private ModelFilter filter;

    @Before
    public void setUp() throws Exception
    {
        filter = new ModelFilter();
        // order matters!!
        filter.addFilter( new ThisXMLFilter( x -> "1.0.0" ) );
        filter.addFilter( new FastForwardFilter() );
        filter.addFilter( new ParentXMLFilter( x -> Optional.of( new ParentXMLFilter.RelativeProject( "GROUPID",
                                                                                                      "ARTIFACTID",
                                                                                                      "1.0.0" ) ) ) );
        filter.addFilter( new ReactorDependencyXMLFilter( x -> "2.0.0" ) );
    }
    
    @Test
    public void testParentNoRelativePath() throws Exception
    {
        
        String input = "<project><parent>"
            + "<groupId>GROUPID</groupId>"
            + "<artifactId>ARTIFACTID</artifactId>"
            + "<version>VERSION</version>"
            + "</parent></project>";
        String expected = input;

        String actual = transform( input, filter );

        assertEquals( expected, actual );
    }
    
    @Test
    public void testThisVersion() throws Exception
    {
        String input = "<project><parent>"
            + "<groupId>GROUPID</groupId>"
            + "<artifactId>ARTIFACTID</artifactId>"
            + "<version>${this.version}</version>"
            + "</parent></project>";
        
        String expected = "<project><parent>"
                        + "<groupId>GROUPID</groupId>"
                        + "<artifactId>ARTIFACTID</artifactId>"
                        + "<version>1.0.0</version>"
                        + "</parent></project>";

        String actual = transform( input, filter );

        assertEquals( expected, actual );
    }
    
    @Test
    public void testNoVersion() throws Exception
    {
        String input = "<project><parent>"
            + "<groupId>GROUPID</groupId>"
            + "<artifactId>ARTIFACTID</artifactId>"
            + "<relativePath>RELATIVEPATH</relativePath>"
            + "</parent></project>";
        
        String expected = "<project><parent>"
                        + "<groupId>GROUPID</groupId>"
                        + "<artifactId>ARTIFACTID</artifactId>"
                        + "<version>1.0.0</version>"
                        + "</parent></project>";

        String actual = transform( input, filter );

        assertEquals( expected, actual );
    }
    
    // reports are deprecated and should not be resolved
    @Test
    public void testReports() throws Exception
    {
        String input = "<project><reports><parent>"
            + "<groupId>GROUPID</groupId>"
            + "<artifactId>ARTIFACTID</artifactId>"
            + "<relativePath>RELATIVEPATH</relativePath>"
            + "</parent>"
            + "</reports></project>";
        
        String expected = input;

        String actual = transform( input, filter );

        assertEquals( expected, actual );
    }

}
