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

import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;

import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.svg.XMLBaseSupport;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Bridge class for the &lt;altGlyph> element.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class SVGAltGlyphElementBridge extends AbstractSVGBridge
                                      implements ErrorConstants {

    /**
     * Constructs a new bridge for the &lt;altGlyph> element.
     */
    public SVGAltGlyphElementBridge() {
    }

    /**
     * Returns 'altGlyph'.
     */
    public String getLocalName() {
        return SVG_ALT_GLYPH_TAG;
    }

    /**
     * Constructs an array of Glyphs that represents the specified
     * &lt;altGlyph> element at the requested size.
     *
     * @param ctx The current bridge context.
     * @param altGlyphElement The altGlyph element to base the SVGGVTGlyphVector
     * construction on.
     * @param fontSize The font size of the Glyphs to create.
     *
     * @return The new SVGGVTGlyphVector or null if any of the glyphs are
     * unavailable.
     */
    public Glyph[] createAltGlyphArray(BridgeContext ctx,
                                       Element altGlyphElement,
                                       float fontSize,
                                       AttributedCharacterIterator aci) {

        // get the referenced element
        String uri = XLinkSupport.getXLinkHref(altGlyphElement);

        Element refElement = null;

        try {
            refElement = ctx.getReferencedElement(altGlyphElement, uri);
        } catch (BridgeException e) {
            if (ERR_URI_UNSECURE.equals(e.getCode())) {
                ctx.getUserAgent().displayError(e);
            }
        }

        if (refElement == null) {
            // couldn't find the referenced element
            return null;
        }

        // if the referenced element is a glyph
        if (refElement.getTagName().equals(SVG_GLYPH_TAG)) {

            Glyph glyph = getGlyph(ctx, uri, altGlyphElement, fontSize, aci);

            if (glyph == null) {
                // failed to create a glyph for the specified glyph uri
                return null;
            }

            Glyph[] glyphArray = new Glyph[1];
            glyphArray[0] = glyph;
            return glyphArray;
        }

        // else should be an altGlyphDef element
        if (refElement.getTagName().equals(SVG_ALT_GLYPH_DEF_TAG)) {

            // if not local import the referenced altGlyphDef
            // into the current document
            SVGOMDocument document
                = (SVGOMDocument)altGlyphElement.getOwnerDocument();
            SVGOMDocument refDocument
                = (SVGOMDocument)refElement.getOwnerDocument();
            boolean isLocal = (refDocument == document);

            Element localRefElement = (isLocal) ? refElement
                                 : (Element)document.importNode(refElement, true);
            if (!isLocal) {
                // need to attach the imported element to the document and
                // then compute the styles and uris
                String base = XMLBaseSupport.getCascadedXMLBase(altGlyphElement);
                Element g = document.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
                g.appendChild(localRefElement);
                g.setAttributeNS(XMLBaseSupport.XML_NAMESPACE_URI,
                                 "xml:base",
                                 base);
                CSSUtilities.computeStyleAndURIs(refElement, 
                                                 localRefElement, 
                                                 uri);
            }

            // look for glyphRef children
            NodeList altGlyphDefChildren = localRefElement.getChildNodes();
            boolean containsGlyphRefNodes = false;
            int numAltGlyphDefChildren = altGlyphDefChildren.getLength();
            for (int i = 0; i < numAltGlyphDefChildren; i++) {
                Node altGlyphChild = altGlyphDefChildren.item(i);
                if (altGlyphChild.getNodeType() == Node.ELEMENT_NODE) {
                    if (((Element)altGlyphChild).getTagName().equals(SVG_GLYPH_REF_TAG)) {
                        containsGlyphRefNodes = true;
                        break;
                    }
                }
            }
            if (containsGlyphRefNodes) { // process the glyphRef children

                NodeList glyphRefNodes
                    = localRefElement.getElementsByTagNameNS(SVG_NAMESPACE_URI,
							     SVG_GLYPH_REF_TAG);
                int numGlyphRefNodes = glyphRefNodes.getLength();
                Glyph[] glyphArray = new Glyph[numGlyphRefNodes];
                for (int i = 0; i < numGlyphRefNodes; i++) {
                    // get the referenced glyph element
                    Element glyphRefElement = (Element)glyphRefNodes.item(i);
                    String glyphUri = XLinkSupport.getXLinkHref(glyphRefElement);

                    Glyph glyph
                        = getGlyph(ctx, glyphUri, glyphRefElement, fontSize, aci);
                    if (glyph == null) {
                        // failed to create a glyph for the specified glyph uri
                        return null;
                    }
                    glyphArray[i] = glyph;
                }
                return glyphArray;

            } else { // try looking for altGlyphItem children

                NodeList altGlyphItemNodes
                    = localRefElement.getElementsByTagNameNS
		    (SVG_NAMESPACE_URI, SVG_ALT_GLYPH_ITEM_TAG);
                int numAltGlyphItemNodes = altGlyphItemNodes.getLength();
                if (numAltGlyphItemNodes > 0) {
                    boolean foundMatchingGlyph = false;
                    Glyph[] glyphArray = null;

                    //look through all altGlyphItem to find the one
                    //that have all its glyphs available

                    for (int i = 0; i < numAltGlyphItemNodes && !foundMatchingGlyph ; i++) {

                        // try to find a resolvable glyphRef
                        Element altGlyphItemElement = (Element)altGlyphItemNodes.item(i);
                        NodeList altGlyphRefNodes
                            = altGlyphItemElement.getElementsByTagNameNS
			    (SVG_NAMESPACE_URI, SVG_GLYPH_REF_TAG);
                        int numAltGlyphRefNodes = altGlyphRefNodes.getLength();

                        glyphArray = new Glyph[numAltGlyphRefNodes];

                        // consider that all glyphs are available
                        // and check if they can be found
                        foundMatchingGlyph = true;

                        for (int j = 0; j < numAltGlyphRefNodes; j++) {
                            // get the referenced glyph element
                            Element glyphRefElement = (Element)altGlyphRefNodes.item(j);
                            String glyphUri = XLinkSupport.getXLinkHref(glyphRefElement);

                            Glyph glyph = getGlyph(ctx, glyphUri, glyphRefElement, fontSize, aci);
                            if (glyph != null) {
                                // found a matching glyph for this altGlyphItem
                                glyphArray[j] = glyph;
                            }
                            else{
                                //this altGlyphItem is not good
                                //seek for the next one
                                foundMatchingGlyph = false;
                                break;
                            }
                        }
                    }
                    if (!foundMatchingGlyph) {
                        // couldn't find a  alGlyphItem
                        // with all its glyphs available
                        // so stop and return null
                        return null;
                    }
                    
                    return glyphArray;
                }
            }
        }


        /*
        // reference is not to a valid element type, throw an exception
        throw new BridgeException(altGlyphElement, ERR_URI_BAD_TARGET,
                                  new Object[] {uri});
        */
        //reference not valid, no altGlyph created
        return null;
    }


    /**
     * Returns a Glyph object that represents the glyph at the specified URI
     * scaled to the required font size.
     *
     * @param ctx The bridge context.
     * @param glyphUri The URI of the glyph to retreive.
     * @param altGlyphElement The element that references the glyph.
     * @param fontSize Indicates the required size of the glyph.
     * @return The Glyph or null if the glyph URI is not available.
     */
    private Glyph getGlyph(BridgeContext ctx,
                           String glyphUri,
                           Element altGlyphElement,
                           float fontSize,
                           AttributedCharacterIterator aci) {

        Element refGlyphElement = null;
        try {
            refGlyphElement = ctx.getReferencedElement(altGlyphElement, 
                                                       glyphUri);
        } catch (BridgeException e) {
            // this is ok, it is possible that the glyph at the given
            // uri is not available. 

            // Display an error message if a security exception occured
            if (ERR_URI_UNSECURE.equals(e.getCode())) {
                ctx.getUserAgent().displayError(e);
            }
        }

        if (refGlyphElement == null
            || !refGlyphElement.getTagName().equals(SVG_GLYPH_TAG)) {
            // couldn't find the referenced glyph element,
            // or referenced element not a glyph
            return null;
        }

        // see if the referenced glyph element is local
        SVGOMDocument document
            = (SVGOMDocument)altGlyphElement.getOwnerDocument();
        SVGOMDocument refDocument
            = (SVGOMDocument)refGlyphElement.getOwnerDocument();
        boolean isLocal = (refDocument == document);

        // if not local, import both the glyph and its font-face element
        Element localGlyphElement = null;
        Element localFontFaceElement = null;
        Element localFontElement = null;
        if (isLocal) {
            localGlyphElement = refGlyphElement;
            localFontElement = (Element)localGlyphElement.getParentNode();
            NodeList fontFaceElements
                = localFontElement.getElementsByTagNameNS
		(SVG_NAMESPACE_URI, SVG_FONT_FACE_TAG);
            if (fontFaceElements.getLength() > 0) {
                localFontFaceElement = (Element)fontFaceElements.item(0);
            }

        } else {
            // import the whole font
            localFontElement = (Element)document.importNode
                (refGlyphElement.getParentNode(), true);
            String base = XMLBaseSupport.getCascadedXMLBase(altGlyphElement);
            Element g = document.createElementNS(SVG_NAMESPACE_URI, SVG_G_TAG);
            g.appendChild(localFontElement);
            g.setAttributeNS(XMLBaseSupport.XML_NAMESPACE_URI,
                             "xml:base",
                             base);
            CSSUtilities.computeStyleAndURIs(
                (Element)refGlyphElement.getParentNode(), 
                localFontElement, glyphUri);

            // get the local glyph element
            String glyphId = refGlyphElement.getAttributeNS
                (null, SVG_ID_ATTRIBUTE);
            NodeList glyphElements = localFontElement.getElementsByTagNameNS
		(SVG_NAMESPACE_URI, SVG_GLYPH_TAG);
            for (int i = 0; i < glyphElements.getLength(); i++) {
                Element glyphElem = (Element)glyphElements.item(i);
                if (glyphElem.getAttributeNS(null, SVG_ID_ATTRIBUTE).equals(glyphId)) {
                    localGlyphElement = glyphElem;
                    break;
                }
            }
            // get the local font-face element
            NodeList fontFaceElements
                = localFontElement.getElementsByTagNameNS
		(SVG_NAMESPACE_URI, SVG_FONT_FACE_TAG);
            if (fontFaceElements.getLength() > 0) {
                localFontFaceElement = (Element)fontFaceElements.item(0);
            }
        }

        // if couldn't find the glyph or its font-face return null
        if (localGlyphElement == null || localFontFaceElement == null) {
            return null;
        }

        SVGFontFaceElementBridge fontFaceBridge
            = (SVGFontFaceElementBridge)ctx.getBridge(localFontFaceElement);
        SVGFontFace fontFace = fontFaceBridge.createFontFace
            (ctx, localFontFaceElement);
        SVGGlyphElementBridge glyphBridge
            = (SVGGlyphElementBridge)ctx.getBridge(localGlyphElement);

        aci.first();
        Paint fillPaint = (Paint)aci.getAttribute(TextAttribute.FOREGROUND);
        Paint strokePaint = (Paint)aci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE_PAINT);
        Stroke stroke = (Stroke)aci.getAttribute(
            GVTAttributedCharacterIterator.TextAttribute.STROKE);

        return glyphBridge.createGlyph(ctx, localGlyphElement, altGlyphElement,
                                      -1, fontSize, fontFace,
                                      fillPaint, strokePaint, stroke);
    }
}
