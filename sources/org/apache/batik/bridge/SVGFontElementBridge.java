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

package org.apache.batik.bridge;

import org.apache.batik.gvt.text.ArabicTextHandler;
import org.apache.batik.gvt.font.GVTFontFace;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Bridge class for the &lt;font> element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGFontElementBridge extends AbstractSVGBridge {

    /**
     * Constructs a new bridge for the &lt;font> element.
     */
    public SVGFontElementBridge() {
    }

    /**
     * Returns 'font'.
     */
    public String getLocalName() {
        return SVG_FONT_TAG;
    }

    /**
     * Constructs a new SVGGVTFont that represents the specified &lt;font> element
     * at the requested size.
     *
     * @param ctx The current bridge context.
     * @param fontElement The font element to base the SVGGVTFont construction on.
     * @param textElement The text element that will use the new font.
     * @param size The size of the new font.
     * @param fontFace The font face object that contains the font attributes.
     *
     * @return The new SVGGVTFont.
     */
    public SVGGVTFont createFont(BridgeContext ctx,
                                 Element fontElement,
                                 Element textElement,
                                 float size,
                                 GVTFontFace fontFace) {


        // construct a list of glyph codes that this font can display and
        // a list of the glyph elements
        NodeList glyphElements = fontElement.getElementsByTagNameNS
	    (SVG_NAMESPACE_URI, SVG_GLYPH_TAG);
        int numGlyphs = glyphElements.getLength();
        String[] glyphCodes = new String[numGlyphs];
        String[] glyphNames = new String[numGlyphs];
        String[] glyphLangs = new String[numGlyphs];
        String[] glyphOrientations = new String[numGlyphs];
        String[] glyphForms = new String[numGlyphs];
        Element[] glyphElementArray = new Element[numGlyphs];

        for (int i = 0; i < numGlyphs; i++) {
            Element glyphElement = (Element)glyphElements.item(i);
            glyphCodes[i] = glyphElement.getAttributeNS(null, SVG_UNICODE_ATTRIBUTE);
            if (glyphCodes[i].length() > 1) {
                // ligature, may need to reverse if arabic so that it is in visual order
                if (ArabicTextHandler.arabicChar(glyphCodes[i].charAt(0))) {
                    glyphCodes[i] = (new StringBuffer(glyphCodes[i])).reverse().toString();
                }
            }
            glyphNames[i] = glyphElement.getAttributeNS(null, SVG_GLYPH_NAME_ATTRIBUTE);
            glyphLangs[i] = glyphElement.getAttributeNS(null, SVG_LANG_ATTRIBUTE);
            glyphOrientations[i] = glyphElement.getAttributeNS(null, SVG_ORIENTATION_ATTRIBUTE);
            glyphForms[i] = glyphElement.getAttributeNS(null, SVG_ARABIC_FORM_ATTRIBUTE);
            glyphElementArray[i] = glyphElement;
        }

        // get the missing glyph element
        NodeList missingGlyphElements = fontElement.getElementsByTagNameNS
	    (SVG_NAMESPACE_URI, SVG_MISSING_GLYPH_TAG);
        Element missingGlyphElement = null;
        if (missingGlyphElements.getLength() > 0) {
            missingGlyphElement = (Element)missingGlyphElements.item(0);
        }

        // get the hkern elements
        NodeList hkernElements = fontElement.getElementsByTagNameNS
	    (SVG_NAMESPACE_URI, SVG_HKERN_TAG);
        Element[] hkernElementArray = new Element[hkernElements.getLength()];

        for (int i = 0; i < hkernElementArray.length; i++) {
            Element hkernElement = (Element)hkernElements.item(i);
            hkernElementArray[i] = hkernElement;
        }

        // get the vkern elements
        NodeList vkernElements = fontElement.getElementsByTagNameNS
	    (SVG_NAMESPACE_URI, SVG_VKERN_TAG);
        Element[] vkernElementArray = new Element[vkernElements.getLength()];

        for (int i = 0; i < vkernElementArray.length; i++) {
            Element vkernElement = (Element)vkernElements.item(i);
            vkernElementArray[i] = vkernElement;
        }

        // return the new SVGGVTFont
        return new SVGGVTFont
            (size, fontFace, glyphCodes, glyphNames, glyphLangs,
             glyphOrientations, glyphForms, ctx,
             glyphElementArray, missingGlyphElement,
             hkernElementArray, vkernElementArray, textElement);
    }
}
