/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.parser;

import java.io.*;

import org.apache.batik.test.*;

/**
 * To test the length parser.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class LengthParserTest extends AbstractTest {

    protected String sourceLength;
    protected String destinationLength;

    protected StringBuffer buffer;
    protected String resultLength;

    /**
     * Creates a new LengthParserTest.
     * @param slength The length to parse.
     * @param dlength The length after serialization.
     */
    public LengthParserTest(String slength, String dlength) {
        sourceLength = slength;
        destinationLength = dlength;
    }

    public TestReport runImpl() throws Exception {
        LengthParser pp = new LengthParser();
        pp.setLengthHandler(new TestHandler());

        try {
            pp.parse(new StringReader(sourceLength));
        } catch (ParseException e) {
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode("parse.error");
            report.addDescriptionEntry("exception.text", e.getMessage());
            report.setPassed(false);
            return report;
        }

        if (!destinationLength.equals(resultLength)) {
            DefaultTestReport report = new DefaultTestReport(this);
            report.setErrorCode("invalid.parsing.events");
            report.addDescriptionEntry("expected.text", destinationLength);
            report.addDescriptionEntry("generated.text", resultLength);
            report.setPassed(false);
            return report;
        }

        return reportSuccess();
    }

    class TestHandler extends DefaultLengthHandler {
        public TestHandler() {}

        public void startLength() throws ParseException {
            buffer = new StringBuffer();
        }
        
        public void lengthValue(float v) throws ParseException {
            buffer.append(v);
        }

        public void em() throws ParseException {
            buffer.append("em");
        }

        public void ex() throws ParseException {
            buffer.append("ex");
        }

        public void in() throws ParseException {
            buffer.append("in");
        }

        public void cm() throws ParseException {
            buffer.append("cm");
        }

        public void mm() throws ParseException {
            buffer.append("mm");
        }

        public void pc() throws ParseException {
            buffer.append("pc");
        }

        public void pt() throws ParseException {
            buffer.append("pt");
        }

        public void px() throws ParseException {
            buffer.append("px");
        }

        public void percentage() throws ParseException {
            buffer.append("%");
        }

        public void endLength() throws ParseException {
            resultLength = buffer.toString();
        }
    }
}
