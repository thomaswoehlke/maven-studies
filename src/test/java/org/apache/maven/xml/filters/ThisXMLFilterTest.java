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

import org.junit.Test;
import org.xml.sax.XMLFilter;

public class ThisXMLFilterTest extends AbstractXMLFilterTests
{
    private XMLFilter filter = new ThisXMLFilter( t-> "THIS" );
    
    @Test
    public void testReplaceAll() throws Exception
    {
        String input = "<project><name>${this.version}</name></project>";
        String expected = "<project><name>THIS</name></project>"; 

        String actual = transform( input, filter );

        assertEquals( expected, actual );
    }
    
    @Test
    public void testReplaceSegment() throws Exception
    {
        String input = "<project><name>Project ${this.version}</name></project>";
        String expected = "<project><name>Project THIS</name></project>"; 

        String actual = transform( input, filter );

        assertEquals( expected, actual );
    }

}
