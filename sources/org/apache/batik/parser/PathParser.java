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

import java.io.IOException;

/**
 * This class implements an event-based parser for the SVG path's d
 * attribute values.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class PathParser extends NumberParser {

    /**
     * The path handler used to report parse events.
     */
    protected PathHandler pathHandler;

    /**
     * Creates a new PathParser.
     */
    public PathParser() {
	pathHandler = DefaultPathHandler.INSTANCE;
    }

    /**
     * Allows an application to register a path handler.
     *
     * <p>If the application does not register a handler, all
     * events reported by the parser will be silently ignored.
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the parser must begin using the new
     * handler immediately.</p>
     * @param handler The transform list handler.
     */
    public void setPathHandler(PathHandler handler) {
	pathHandler = handler;
    }

    /**
     * Returns the path handler in use.
     */
    public PathHandler getPathHandler() {
	return pathHandler;
    }

    protected void doParse() throws ParseException, IOException {
	pathHandler.startPath();

	current = reader.read();
	loop: for (;;) {
            try {
                switch (current) {
                case 0xD:
                case 0xA:
                case 0x20:
                case 0x9:
                    current = reader.read();
                    break;
                case 'z':
                case 'Z':
                    current = reader.read();
                    pathHandler.closePath();
                    break;
                case 'm':
                    parsem();
                case 'l':
                    parsel();
                    break;
                case 'M':
                    parseM();
                case 'L':
                    parseL();
                    break;
                case 'h':
                    parseh();
                    break;
                case 'H':
                    parseH();
                    break;
                case 'v':
                    parsev();
                    break;
                case 'V':
                    parseV();
                    break;
                case 'c':
                    parsec();
                    break;
                case 'C':
                    parseC();
                    break;
                case 'q':
                    parseq();
                    break;
                case 'Q':
                    parseQ();
                    break;
                case 's':
                    parses();
                    break;
                case 'S':
                    parseS();
                    break;
                case 't':
                    parset();
                    break;
                case 'T':
                    parseT();
                    break;
                case 'a':
                    parsea();
                    break;
                case 'A':
                    parseA();
                    break;
                case -1:
                    break loop;
                default:
                    reportError("character.unexpected",
                                new Object[] { new Integer(current) });
                    skipSubPath();
                }
	    } catch (ParseException e) {
                errorHandler.error(e);
                skipSubPath();
            }
	}

	skipSpaces();
	if (current != -1) {
	    reportError("end.of.stream.expected",
			new Object[] { new Integer(current) });
	}

	pathHandler.endPath();
    }

    /**
     * Parses a 'm' command.
     */
    protected void parsem() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

        float x = parseFloat();
        skipCommaSpaces();
        float y = parseFloat();

        pathHandler.movetoRel(x, y);
        skipCommaSpaces();
    }

    /**
     * Parses a 'l' command.
     */
    protected void parsel() throws ParseException, IOException {
	if (current == 'l') {
	    current = reader.read();
	}
	skipSpaces();
        for (;;) {
	    switch (current) {
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
                float x = parseFloat();
                skipCommaSpaces();
                float y = parseFloat();

                pathHandler.linetoRel(x, y);
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'M' command.
     */
    protected void parseM() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

        float x = parseFloat();
        skipCommaSpaces();
        float y = parseFloat();
	    
        pathHandler.movetoAbs(x, y);
        skipCommaSpaces();
    }

    /**
     * Parses a 'L' command.
     */
    protected void parseL() throws ParseException, IOException {
	if (current == 'L') {
	    current = reader.read();
	}
	skipSpaces();
        for (;;) {
	    switch (current) {
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
                float x = parseFloat();
                skipCommaSpaces();
                float y = parseFloat();

                pathHandler.linetoAbs(x, y);
                break;
	    default:
                return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'h' command.
     */
    protected void parseh() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
                float x = parseFloat();
                pathHandler.linetoHorizontalRel(x);
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'H' command.
     */
    protected void parseH() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
                float x = parseFloat();
                pathHandler.linetoHorizontalAbs(x);
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'v' command.
     */
    protected void parsev() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
                float x = parseFloat();
                pathHandler.linetoVerticalRel(x);
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'V' command.
     */
    protected void parseV() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
                float x = parseFloat();
                pathHandler.linetoVerticalAbs(x);
		break;
	    default:
		return;
	    }
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'c' command.
     */
    protected void parsec() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();
	
	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

            float x1 = parseFloat();
            skipCommaSpaces();
            float y1 = parseFloat();
            skipCommaSpaces();
            float x2 = parseFloat();
            skipCommaSpaces();
            float y2 = parseFloat();
            skipCommaSpaces();
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.curvetoCubicRel(x1, y1, x2, y2, x, y);
	    skipCommaSpaces();
	}
    }		

    /**
     * Parses a 'C' command.
     */
    protected void parseC() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();
	
	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

            float x1 = parseFloat();
            skipCommaSpaces();
            float y1 = parseFloat();
            skipCommaSpaces();
            float x2 = parseFloat();
            skipCommaSpaces();
            float y2 = parseFloat();
            skipCommaSpaces();
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.curvetoCubicAbs(x1, y1, x2, y2, x, y);
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'q' command.
     */
    protected void parseq() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

            float x1 = parseFloat();
            skipCommaSpaces();
            float y1 = parseFloat();
            skipCommaSpaces();
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.curvetoQuadraticRel(x1, y1, x, y);
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'Q' command.
     */
    protected void parseQ() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

            float x1 = parseFloat();
            skipCommaSpaces();
            float y1 = parseFloat();
            skipCommaSpaces();
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.curvetoQuadraticAbs(x1, y1, x, y);
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 's' command.
     */
    protected void parses() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }
	    
            float x2 = parseFloat();
            skipCommaSpaces();
            float y2 = parseFloat();
            skipCommaSpaces();
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.curvetoCubicSmoothRel(x2, y2, x, y);
	    skipCommaSpaces();
	}
    }		

    /**
     * Parses a 'S' command.
     */
    protected void parseS() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }
	    
            float x2 = parseFloat();
            skipCommaSpaces();
            float y2 = parseFloat();
            skipCommaSpaces();
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.curvetoCubicSmoothAbs(x2, y2, x, y);
	    skipCommaSpaces();
	}
    }		

    /**
     * Parses a 't' command.
     */
    protected void parset() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();
            
            pathHandler.curvetoQuadraticSmoothRel(x, y);
	    skipCommaSpaces();
	}		
    }

    /**
     * Parses a 'T' command.
     */
    protected void parseT() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.curvetoQuadraticSmoothAbs(x, y);
	    skipCommaSpaces();
	}		
    }

    /**
     * Parses a 'a' command.
     */
    protected void parsea() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

            float rx = parseFloat();
            skipCommaSpaces();
            float ry = parseFloat();
            skipCommaSpaces();
            float ax = parseFloat();
            skipCommaSpaces();
		
            boolean laf;
            switch (current) {
            case '0':
                laf = false;
                break;
            case '1':
                laf = true;
                break;
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                skipSubPath();
                return;
            }

            current = reader.read();
            skipCommaSpaces();

            boolean sf;
            switch (current) {
            case '0':
                sf = false;
                break;
            case '1':
                sf = true;
                break;
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                skipSubPath();
                return;
            }

            current = reader.read();
            skipCommaSpaces();

            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.arcRel(rx, ry, ax, laf, sf, x, y);
	    skipCommaSpaces();
	}
    }

    /**
     * Parses a 'A' command.
     */
    protected void parseA() throws ParseException, IOException {
	current = reader.read();
	skipSpaces();

	for (;;) {
	    switch (current) {
	    default:
		return;
	    case '+': case '-': case '.':
	    case '0': case '1': case '2': case '3': case '4':
	    case '5': case '6': case '7': case '8': case '9':
	    }

            float rx = parseFloat();
            skipCommaSpaces();
            float ry = parseFloat();
            skipCommaSpaces();
            float ax = parseFloat();
            skipCommaSpaces();
            
            boolean laf;
            switch (current) {
            case '0':
                laf = false;
                break;
            case '1':
                laf = true;
                break;
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                skipSubPath();
                return;
            }
            
            current = reader.read();
            skipCommaSpaces();

            boolean sf;
            switch (current) {
            case '0':
                sf = false;
                break;
            case '1':
                sf = true;
                break;
            default:
                reportError("character.unexpected",
                            new Object[] { new Integer(current) });
                skipSubPath();
                return;
            }

            current = reader.read();
            skipCommaSpaces();
            float x = parseFloat();
            skipCommaSpaces();
            float y = parseFloat();

            pathHandler.arcAbs(rx, ry, ax, laf, sf, x, y);
	    skipCommaSpaces();
	}
    }

    /**
     * Skips a sub-path.
     */
    protected void skipSubPath() throws ParseException, IOException {
	for (;;) {
	    switch (current) {
	    case 'm':
	    case 'M':
		return;
	    default:
		if (current == -1) {
		    return;
		}
		current = reader.read();
	    }
	}
    }
}
