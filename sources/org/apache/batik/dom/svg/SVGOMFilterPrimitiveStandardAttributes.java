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

package org.apache.batik.dom.svg;

import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes;

/**
 * This class represents a SVGElement with support for standard filter
 * attributes.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public abstract class SVGOMFilterPrimitiveStandardAttributes
    extends SVGStylableElement
    implements SVGFilterPrimitiveStandardAttributes {

    /**
     * Creates a new SVGOMFilterPrimitiveStandardAttributes object.
     */
    protected SVGOMFilterPrimitiveStandardAttributes() {
    }

    /**
     * Creates a new SVGOMFilterPrimitiveStandardAttributes object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    protected SVGOMFilterPrimitiveStandardAttributes(String prefix,
                                                     AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getX()}.
     */
    public SVGAnimatedLength getX() {
        return getAnimatedLengthAttribute
            (null, SVG_X_ATTRIBUTE, SVG_FILTER_PRIMITIVE_X_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getY()}.
     */
    public SVGAnimatedLength getY() {
        return getAnimatedLengthAttribute
            (null, SVG_Y_ATTRIBUTE, SVG_FILTER_PRIMITIVE_Y_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getWidth()}.
     */
    public SVGAnimatedLength getWidth() {
        return getAnimatedLengthAttribute
            (null, SVG_WIDTH_ATTRIBUTE,
             SVG_FILTER_PRIMITIVE_WIDTH_DEFAULT_VALUE,
             SVGOMAnimatedLength.HORIZONTAL_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getHeight()}.
     */
    public SVGAnimatedLength getHeight() {
        return getAnimatedLengthAttribute
            (null, SVG_HEIGHT_ATTRIBUTE,
             SVG_FILTER_PRIMITIVE_HEIGHT_DEFAULT_VALUE,
             SVGOMAnimatedLength.VERTICAL_LENGTH);
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGFilterPrimitiveStandardAttributes#getResult()}.
     */
    public SVGAnimatedString getResult() {
        return getAnimatedStringAttribute(null, SVG_RESULT_ATTRIBUTE);
    }
}
