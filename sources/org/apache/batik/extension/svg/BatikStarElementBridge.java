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

package org.apache.batik.extension.svg;

import java.awt.geom.GeneralPath;

import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.SVGDecoratedShapeElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.UnitProcessor;
import org.apache.batik.gvt.ShapeNode;
import org.w3c.dom.Element;

/**
 * Bridge class for a star element.
 *
 * @author <a href="mailto:thomas.deweese@kodak.com">Thomas Deweese</a>
 * @version $Id$
 */
public class BatikStarElementBridge 
    extends SVGDecoratedShapeElementBridge  
    implements BatikExtConstants {

    /**
     * Constructs a new bridge for the &lt;rect> element.
     */
    public BatikStarElementBridge() { /* nothing */ }

    /**
     * Returns the SVG namespace URI.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns 'rect'.
     */
    public String getLocalName() {
        return BATIK_EXT_STAR_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new BatikStarElementBridge();
    }

    /**
     * Constructs a star polygone according to the specified parameters.
     *
     * @param ctx the bridge context to use
     * @param e the element that describes a rect element
     * @param shapeNode the shape node to initialize
     */
    protected void buildShape(BridgeContext ctx,
                              Element e,
                              ShapeNode shapeNode) {

        UnitProcessor.Context uctx = UnitProcessor.createContext(ctx, e);
        String s;

        // 'cx' attribute - default is 0
        s = e.getAttributeNS(null, SVG_CX_ATTRIBUTE);
        float cx = 0;
        if (s.length() != 0) {
            cx = UnitProcessor.svgHorizontalCoordinateToUserSpace
                (s, SVG_CX_ATTRIBUTE, uctx);
        }

        // 'cy' attribute - default is 0
        s = e.getAttributeNS(null, SVG_CY_ATTRIBUTE);
        float cy = 0;
        if (s.length() != 0) {
            cy = UnitProcessor.svgVerticalCoordinateToUserSpace
                (s, SVG_CY_ATTRIBUTE, uctx);
        }

        // 'r' attribute - required
        s = e.getAttributeNS(null, SVG_R_ATTRIBUTE);
        float r;
        if (s.length() == 0)
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {SVG_R_ATTRIBUTE, s});
        r = UnitProcessor.svgOtherLengthToUserSpace
            (s, SVG_R_ATTRIBUTE, uctx);

        // 'ir' attribute - required
        s = e.getAttributeNS(null, BATIK_EXT_IR_ATTRIBUTE);
        float ir;
        if (s.length() == 0)
            throw new BridgeException
                (e, ERR_ATTRIBUTE_MISSING,
                 new Object[] {BATIK_EXT_IR_ATTRIBUTE, s});

        ir = UnitProcessor.svgOtherLengthToUserSpace
            (s, BATIK_EXT_IR_ATTRIBUTE, uctx);

        // 'sides' attribute - default is 3
        int sides = convertSides(e, BATIK_EXT_SIDES_ATTRIBUTE, 3);
        
        GeneralPath gp = new GeneralPath();
        double angle, x, y;
        for (int i=0; i<sides; i++) {
            angle    = (i)*(2*Math.PI/sides) - (Math.PI/2);
            x = cx + ir*Math.cos(angle);
            y = cy - ir*Math.sin(angle);
            if (i==0)
                gp.moveTo((float)x, (float)y);
            else
                gp.lineTo((float)x, (float)y);

            angle    = (i+0.5)*(2*Math.PI/sides) - (Math.PI/2);
            x = cx + r*Math.cos(angle);
            y = cy - r*Math.sin(angle);
            gp.lineTo((float)x, (float)y);
        }
        gp.closePath();

        shapeNode.setShape(gp);
    }

    /**
     * Stolen from AbstractSVGFilterPrimitiveElementBridge.
     * Converts on the specified filter primitive element, the specified
     * attribute that represents an integer and with the specified
     * default value.
     *
     * @param filterElement the filter primitive element
     * @param attrName the name of the attribute
     * @param defaultValue the default value of the attribute
     */
    protected static int convertSides(Element filterElement,
                                        String attrName,
                                        int defaultValue) {
        String s = filterElement.getAttributeNS(null, attrName);
        if (s.length() == 0) {
            return defaultValue;
        } else {
            int ret = 0;
            try {
                ret = SVGUtilities.convertSVGInteger(s);
            } catch (NumberFormatException ex) {
                throw new BridgeException
                    (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {attrName, s});
            }

            if (ret <3) 
                throw new BridgeException
                    (filterElement, ERR_ATTRIBUTE_VALUE_MALFORMED,
                     new Object[] {attrName, s});
            return ret;
        }
    }
}
