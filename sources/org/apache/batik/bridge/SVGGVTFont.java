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

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.batik.css.engine.SVGCSSEngine;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.font.Kern;
import org.apache.batik.gvt.font.KerningTable;
import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

/**
 * Represents an SVG font.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public final class SVGGVTFont implements GVTFont, SVGConstants {

    private float fontSize;
    private GVTFontFace fontFace;
    private String[] glyphUnicodes;
    private String[] glyphNames;
    private String[] glyphLangs;
    private String[] glyphOrientations;
    private String[] glyphForms;
    private Element[] glyphElements;
    private Element[] hkernElements;
    private Element[] vkernElements;
    private BridgeContext ctx;
    private Element textElement;
    private Element missingGlyphElement;
    private KerningTable hKerningTable;
    private KerningTable vKerningTable;
    private String language;
    private String orientation;

    private GVTLineMetrics lineMetrics=null;

    /**
     * Constructs a new SVGGVTFont of the specified size.
     *
     * @param fontSize The size of the font to create.
     * @param fontFace The font face that describes the font.
     * @param glyphUnicodes An array containing the unicode values for
     * all the glyphs this font can display.
     * @param glyphNames An array containing the names of all the
     * glyphs this font can display.
     * @param ctx The bridge context.
     * @param glyphElements An array containing the children glyph
     * elements of the SVG font.
     * @param missingGlyphElement The missing glyph element for this
     * font.
     * @param hkernElements An array containing all hkern elements for
     * this font.
     * @param vkernElements An array containing all vkern elements for
     * this font.
     * @param textElement The text element that contains the text to
     * be rendered using this font.
     */
    public SVGGVTFont(float fontSize,
                      GVTFontFace fontFace,
                      String[] glyphUnicodes,
                      String[] glyphNames,
                      String[] glyphLangs,
                      String[] glyphOrientations,
                      String[] glyphForms,
                      BridgeContext ctx,
                      Element[] glyphElements,
                      Element missingGlyphElement,
                      Element[] hkernElements,
                      Element[] vkernElements,
                      Element textElement) {
        this.fontFace = fontFace;
        this.fontSize = fontSize;
        this.glyphUnicodes = glyphUnicodes;
        this.glyphNames = glyphNames;
        this.glyphLangs = glyphLangs;
        this.glyphOrientations = glyphOrientations;
        this.glyphForms = glyphForms;
        this.ctx = ctx;
        this.glyphElements = glyphElements;
        this.missingGlyphElement = missingGlyphElement;
        this.hkernElements = hkernElements;
        this.vkernElements = vkernElements;
        this.textElement = textElement;

        this.language = XMLSupport.getXMLLang(textElement);

        Value v = CSSUtilities.getComputedStyle
            (textElement, SVGCSSEngine.WRITING_MODE_INDEX);
        if (v.getStringValue().startsWith(CSS_TB_VALUE)) {
            // top to bottom, so set orientation to "v"
            this.orientation = SVG_V_VALUE;
        } else {
            this.orientation = SVG_H_VALUE;
        }

        createKerningTables();
    }


    /**
     * Creates the kerning tables for this font. Two tables are created,
     * horizontal and vertical. If there are not children vkern or hkern
     * elements these tables will be empty.
     */
    private void createKerningTables() {

        Kern[] hEntries = new Kern[hkernElements.length];
        for (int i = 0; i < hkernElements.length; i++) {
            Element hkernElement = hkernElements[i];
            SVGHKernElementBridge hkernBridge =
                (SVGHKernElementBridge)ctx.getBridge(hkernElement);
            Kern hkern = hkernBridge.createKern(ctx, hkernElement, this);
            hEntries[i] = hkern;
        }
        hKerningTable = new KerningTable(hEntries);

        Kern[] vEntries = new Kern[vkernElements.length];
        for (int i = 0; i < vkernElements.length; i++) {
            Element vkernElement = vkernElements[i];
            SVGVKernElementBridge vkernBridge =
                (SVGVKernElementBridge)ctx.getBridge(vkernElement);
            Kern vkern = vkernBridge.createKern(ctx, vkernElement, this);
            vEntries[i] = vkern;
        }
        vKerningTable = new KerningTable(vEntries);

    }

    /**
     * Returns the horizontal kerning value for the specified glyph pair.
     * This will be zero if there is no explicit horizontal kerning value
     * for this particular glyph pair.
     *
     * @param glyphCode1 The id of the first glyph.
     * @param glyphCode2 The id of the second glyph.
     *
     * @return The horizontal kerning value.
     */
    public float getHKern(int glyphCode1, int glyphCode2) {
        if (glyphCode1 < 0 || glyphCode1 >= glyphUnicodes.length
            || glyphCode2 < 0 || glyphCode2 >= glyphUnicodes.length) {
            return 0f;
        }
        return hKerningTable.getKerningValue(glyphCode1, glyphCode2,
                glyphUnicodes[glyphCode1], glyphUnicodes[glyphCode2]);
    }

    /**
     * Returns the vertical kerning value for the specified glyph pair.
     * This will be zero if there is no explicit vertical kerning value for
     * for this particular glyph pair.
     *
     * @param glyphCode1 The id of the first glyph.
     * @param glyphCode2 The id of the second glyph.
     *
     * @return The vertical kerning value.
     */
    public float getVKern(int glyphCode1, int glyphCode2) {
        if (glyphCode1 < 0 || glyphCode1 >= glyphUnicodes.length
            || glyphCode2 < 0 || glyphCode2 >= glyphUnicodes.length) {
            return 0f;
        }
        return vKerningTable.getKerningValue(glyphCode1, glyphCode2,
                glyphUnicodes[glyphCode1], glyphUnicodes[glyphCode2]);
    }

    /**
     * Returns an array of glyph codes (unique ids) of the glyphs with the
     * specified name (there may be more than one).
     *
     * @param name The name of the glyph.
     *
     * @return An array of matching glyph codes. This may be empty.
     */
    public int[] getGlyphCodesForName(String name) {
        Vector glyphCodes = new Vector();
        for (int i = 0; i < glyphNames.length; i++) {
            if (glyphNames[i] != null && glyphNames[i].equals(name)) {
                glyphCodes.add(new Integer(i));
            }
        }
        int[] glyphCodeArray = new int[glyphCodes.size()];
        for (int i = 0; i < glyphCodes.size(); i++) {
            glyphCodeArray[i] = ((Integer)glyphCodes.elementAt(i)).intValue();
        }
        return glyphCodeArray;
    }

    /**
     * Returns an array of glyph codes (unique ids) of the glyphs with the
     * specified unicode value (there may be more than one).
     *
     * @param unicode The unicode value of the glyph.
     *
     * @return An array of matching glyph codes. This may be empty.
     */
    public int[] getGlyphCodesForUnicode(String unicode) {
        Vector glyphCodes = new Vector();
        for (int i = 0; i < glyphUnicodes.length; i++) {
            if (glyphUnicodes[i] != null && glyphUnicodes[i].equals(unicode)) {
                glyphCodes.add(new Integer(i));
            }
        }
        int[] glyphCodeArray = new int[glyphCodes.size()];
        for (int i = 0; i < glyphCodes.size(); i++) {
            glyphCodeArray[i] = ((Integer)glyphCodes.elementAt(i)).intValue();
        }
        return glyphCodeArray;
    }

    /**
     * Returns true if the glyph language matches the language of the
     * text node to be rendered by this font.  This will be the case
     * if one of the languages in glyphLang matches exactly with the
     * xml:lang attibute of the text node, or if the xml:lang
     * attribute exactly equals a prefix of one glyph languages.
     *
     * @param glyphLang A comma separated list of languages that are associated
     * with a glyph.
     *
     * @return Whether or not the glyph language matches the language of the
     * text node.
     */
    private boolean languageMatches(String glyphLang) {
        if (glyphLang == null || glyphLang.length() == 0) {
            return true;  // will match all languages
        }
        StringTokenizer st = new StringTokenizer(glyphLang, ",");
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (s.equals(language)
               || (s.startsWith(language) && s.length() > language.length()
                   && s.charAt(language.length()) == '-')) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the glyph orientation matches the orientation of the
     * text node to be rendered by this font.
     *
     * @param glyphOrientation The glyph orientation attribute value. Will be
     * "h", "v" or empty.
     *
     * @return Whether or not the glyph orientation matches the text to be
     * rendered by this font object.
     */
    private boolean orientationMatches(String glyphOrientation) {
        if (glyphOrientation == null || glyphOrientation.length() == 0) {
            return true;
        }
        return glyphOrientation.equals(orientation);
    }


    /**
     * Returns true if the glyph form matches that of the current character in
     * the aci.
     *
     * @param gyphUnicode The unicode value of the glyph.
     * @param glyphForm The arabic-form glyph attribute.
     * @param aci The aci containing the character to check.
     * @param currentIndex The index of the character to check.
     */
    private boolean formMatches(String glyphUnicode,
                                String glyphForm,
                                AttributedCharacterIterator aci,
                                int currentIndex) {
        if (aci == null || glyphForm == null || glyphForm.length() == 0) {
            // there aren't any attributes attached to the text
            // or the glyph doesn't have an arabic form
            return true;
        }

        char c = aci.setIndex(currentIndex);
        Integer form = (Integer)aci.getAttribute
            (GVTAttributedCharacterIterator.TextAttribute.ARABIC_FORM);

        if (form == null || form.equals
            (GVTAttributedCharacterIterator.TextAttribute.ARABIC_NONE)) {
            // the glyph has an arabic form and the current character
            // form is "none" so don't match
            return false;
        }

        // see if c is the start of an arabic ligature
        if (glyphUnicode.length() > 1) {

            boolean matched = true;
            for (int j = 1; j < glyphUnicode.length(); j++) {
                c = aci.next();
                if (glyphUnicode.charAt(j) != c) {
                    matched = false;
                    break;
                }
            }

            // reset the aci
            aci.setIndex(currentIndex);

            if (matched) {

                // ligature matches, now check that the arabic forms are ok
                char lastChar =
                    aci.setIndex(currentIndex + glyphUnicode.length() - 1);
                Integer lastForm = (Integer)aci.getAttribute(
                    GVTAttributedCharacterIterator.TextAttribute.ARABIC_FORM);

                // reset the aci again
                aci.setIndex(currentIndex);

                if (form != null && lastForm != null) {
                    if (form.equals(GVTAttributedCharacterIterator.
                                    TextAttribute.ARABIC_TERMINAL) &&
                        lastForm.equals(GVTAttributedCharacterIterator.
                                        TextAttribute.ARABIC_INITIAL)) {
                        // return true if the glyph form is isolated
                        return glyphForm.equals
                            (SVGConstants.SVG_ISOLATED_VALUE);

                    } else if (form.equals(GVTAttributedCharacterIterator.
                                           TextAttribute.ARABIC_TERMINAL)) {
                        // return true if the glyph form is terminal
                        return glyphForm.equals
                            (SVGConstants.SVG_TERMINAL_VALUE);

                    } else if (form.equals(GVTAttributedCharacterIterator.
                                           TextAttribute.ARABIC_MEDIAL) &&
                               lastForm.equals(GVTAttributedCharacterIterator.
                                               TextAttribute.ARABIC_MEDIAL)) {
                        // return true if the glyph form is medial
                        return glyphForm.equals(SVGConstants.SVG_MEDIAL_VALUE);
                    }
                    // should test for other combos as well here
                }
            }
        }

        if (form.equals(GVTAttributedCharacterIterator.
                        TextAttribute.ARABIC_ISOLATED)) {
            return glyphForm.equals(SVGConstants.SVG_ISOLATED_VALUE);
        }
        if (form.equals(GVTAttributedCharacterIterator.
                        TextAttribute.ARABIC_TERMINAL)) {
            return glyphForm.equals(SVGConstants.SVG_TERMINAL_VALUE);
        }
        if (form.equals(GVTAttributedCharacterIterator.
                        TextAttribute.ARABIC_INITIAL)) {
            return glyphForm.equals(SVGConstants.SVG_INITIAL_VALUE);
        }
        if (form.equals(GVTAttributedCharacterIterator.
                        TextAttribute.ARABIC_MEDIAL)) {
            return glyphForm.equals(SVGConstants.SVG_MEDIAL_VALUE);
        }
        return false;
    }

    /**
     * Indicates whether or not the specified glyph can be displayed by this
     * font.
     *
     * @param name The name of the glyph to check.
     *
     * @return True if the glyph can be displayed.
     */
    public boolean canDisplayGivenName(String name) {
        for (int i = 0; i < glyphNames.length; i++) {
            if (glyphNames[i] != null && glyphNames[i].equals(name)
                && languageMatches(glyphLangs[i])
                && orientationMatches(glyphOrientations[i])) {
                return true;
            }
        }
        return false;
    }


    /**
     * Indicates whether or not the specified character can be
     * displayed by this font.
     *
     * @param c The character to check.
     *
     * @param True if the character can be displayed.
     */
    public boolean canDisplay(char c) {
        for (int i = 0; i < glyphUnicodes.length; i++) {
            if (glyphUnicodes[i].indexOf(c) != -1
                && languageMatches(glyphLangs[i])
                && orientationMatches(glyphOrientations[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether this Font can display the characters in the
     * specified character array starting at start and ending at limit.
     *
     * @param text An array containing the characters to check.
     * @param start The index of the first character to check.
     * @param limit The index of the last character to check.
     *
     * @return The index of the first character it can't display or -1 if
     * it can display the whole string.
     */
    public int canDisplayUpTo(char[] text, int start, int limit) {
        StringCharacterIterator sci =
            new StringCharacterIterator(new String(text));
        return canDisplayUpTo(sci, start, limit);
    }

    /**
     * Checks whether this Font can display the characters in the
     * specified character iterator starting at start and ending at limit.
     *
     * @param iter The iterator containing the characters to check.
     * @param start The index of the first character to check.
     * @param limit The index of the last character to check.
     *
     * @return The index of the first character it can't display or -1 if
     * it can display the whole string.
     */
    public int canDisplayUpTo(CharacterIterator iter, int start, int limit) {

        AttributedCharacterIterator aci = null;
        if (iter instanceof AttributedCharacterIterator) {
            aci = (AttributedCharacterIterator)iter;
        }

        char c = iter.setIndex(start);
        int currentIndex = start;

        while (c != iter.DONE && currentIndex < limit) {

            boolean foundMatchingGlyph = false;

            for (int i = 0; i < glyphUnicodes.length; i++) {
                if (glyphUnicodes[i].indexOf(c) == 0
                    && languageMatches(glyphLangs[i])
                    && orientationMatches(glyphOrientations[i])
                    && formMatches(glyphUnicodes[i], glyphForms[i], 
                                   aci, currentIndex)) {  
                    // found a possible match

                    if (glyphUnicodes[i].length() == 1)  { // not a ligature
                        foundMatchingGlyph = true;
                        break;

                    } else {
                        // glyphCodes[i] is a ligature so try and
                        // match the rest of the glyphCode chars
                        boolean matched = true;
                        for (int j = 1; j < glyphUnicodes[i].length(); j++) {
                            c = iter.next();
                            if (glyphUnicodes[i].charAt(j) != c) {
                                matched = false;
                                break;
                            }
                        }
                        if (matched) { // found a matching ligature!
                            foundMatchingGlyph = true;
                            break;

                        } else {
                            // did not match ligature, keep looking
                            // for another glyph
                            c = iter.setIndex(currentIndex);
                        }
                    }
                }
            }
            if (!foundMatchingGlyph) {
                return currentIndex;
            }
            c = iter.next();
            currentIndex = iter.getIndex();
        }
        return -1;
    }

    /**
     * Checks whether or not this font can display the characters in the
     * specified String.
     *
     * @param str The string containing the characters to check.
     *
     * @return The index of the first character it can't display or -1 if
     * it can display the whole string.
     */
    public int canDisplayUpTo(String str) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return canDisplayUpTo(sci, 0, str.length());
    }

    /**
     * Returns a new GVTGlyphVector object for the specified array of
     * characters.
     *
     * @param frc The current font render context.
     * @param chars The array of chars that the glyph vector will represent.
     *
     * @return The new glyph vector.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            char[] chars) {
         StringCharacterIterator sci =
             new StringCharacterIterator(new String(chars));
         return createGlyphVector(frc, sci);
    }

    /**
     * Returns a new GVTGlyphVector object for the characters in the
     * specified character iterator.
     *
     * @param frc The current font render context.
     * @param ci The character iterator that the glyph vector will represent.
     *
     * @return The new glyph vector.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            CharacterIterator ci) {

        AttributedCharacterIterator aci = null;
        if (ci instanceof AttributedCharacterIterator) {
            aci = (AttributedCharacterIterator)ci;
        }

        Vector glyphs = new Vector();
        char c = ci.first();
        while (c != ci.DONE) {
            boolean foundMatchingGlyph = false;
            for (int i = 0; i < glyphUnicodes.length; i++) {
                if (glyphUnicodes[i].indexOf(c) == 0 &&
                    languageMatches(glyphLangs[i]) &&
                    orientationMatches(glyphOrientations[i]) &&
                    formMatches(glyphUnicodes[i], glyphForms[i], aci,
                                ci.getIndex())) {  // found a possible match

                    if (glyphUnicodes[i].length() == 1)  { // not a ligature
                        Element glyphElement = glyphElements[i];
                        SVGGlyphElementBridge glyphBridge =
                            (SVGGlyphElementBridge)ctx.getBridge
                                (glyphElement);
                        Glyph glyph;
                        if (aci != null) {
                            aci.setIndex(ci.getIndex());
                            Paint fillPaint = (Paint)aci.getAttribute
                                (TextAttribute.FOREGROUND);
                            Paint strokePaint = (Paint)aci.getAttribute
                                (GVTAttributedCharacterIterator.
                                 TextAttribute.STROKE_PAINT);
                            Stroke stroke = (Stroke)aci.getAttribute
                                (GVTAttributedCharacterIterator.
                                 TextAttribute.STROKE);
                            glyph = glyphBridge.createGlyph
                                (ctx, glyphElement, textElement, i, fontSize,
                                 fontFace, fillPaint, strokePaint, stroke);
                        } else {
                             glyph = glyphBridge.createGlyph
                                 (ctx, glyphElement, textElement, i, fontSize,
                                  fontFace, null, null, null);
                        }
                        glyphs.add(glyph);
                        foundMatchingGlyph = true;
                        break;

                    } else {
                        // glyphCodes[i] is a ligature so try and
                        // match the rest of the glyphCode chars
                        int current = ci.getIndex();
                        boolean matched = true;
                        for (int j = 1; j < glyphUnicodes[i].length(); j++) {
                            c = ci.next();
                            if (glyphUnicodes[i].charAt(j) != c) {
                                matched = false;
                                break;
                            }
                        }
                        if (matched) { // found a matching ligature!

                            Element glyphElement = glyphElements[i];
                            SVGGlyphElementBridge glyphBridge
                                = (SVGGlyphElementBridge)ctx.getBridge
                                (glyphElement);
                            Glyph glyph;
                            if (aci != null) {
                                aci.setIndex(ci.getIndex());
                                Paint fillPaint = (Paint)aci.getAttribute
                                    (TextAttribute.FOREGROUND);
                                Paint strokePaint = (Paint)aci.getAttribute
                                    (GVTAttributedCharacterIterator.
                                     TextAttribute.STROKE_PAINT);
                                Stroke stroke = (Stroke)aci.getAttribute
                                    (GVTAttributedCharacterIterator.
                                     TextAttribute.STROKE);
                                glyph = glyphBridge.createGlyph
                                    (ctx, glyphElement, textElement, i,
                                     fontSize, fontFace, fillPaint,
                                     strokePaint, stroke);
                            } else {
                                glyph = glyphBridge.createGlyph
                                    (ctx, glyphElement, textElement, i,
                                     fontSize, fontFace, null, null, null);
                            }
                            glyphs.add(glyph);
                            foundMatchingGlyph = true;
                            break;

                        } else {
                            // did not match ligature, keep looking
                            // for another glyph
                            c = ci.setIndex(current);
                        }
                    }
                }
            }
            if (!foundMatchingGlyph) {
                // add the missing glyph
                SVGGlyphElementBridge glyphBridge =
                    (SVGGlyphElementBridge)ctx.getBridge(missingGlyphElement);
                Glyph glyph;
                if (aci != null) {
                    aci.setIndex(ci.getIndex());
                    Paint fillPaint = (Paint)aci.getAttribute
                        (TextAttribute.FOREGROUND);
                    Paint strokePaint = (Paint)aci.getAttribute
                        (GVTAttributedCharacterIterator.
                         TextAttribute.STROKE_PAINT);
                    Stroke stroke = (Stroke)aci.getAttribute
                        (GVTAttributedCharacterIterator.TextAttribute.STROKE);
                    glyph = glyphBridge.createGlyph
                        (ctx, missingGlyphElement, textElement, -1, fontSize,
                         fontFace, fillPaint, strokePaint, stroke);
                } else {
                    glyph = glyphBridge.createGlyph
                        (ctx, missingGlyphElement, textElement, -1, fontSize,
                         fontFace, null, null, null);
                }
                glyphs.add(glyph);

            }
            c = ci.next();
        }

        // turn the vector of glyphs into an array;
        int numGlyphs = glyphs.size();
        Glyph[] glyphArray = new Glyph[numGlyphs];
        for (int i =0; i < numGlyphs; i++) {
            glyphArray[i] = (Glyph)glyphs.get(i);
        }
        // return a new SVGGVTGlyphVector
        return new SVGGVTGlyphVector(this, glyphArray, frc);
    }

    /**
     * Returns a new GVTGlyphVector object for the glyphs in the
     * the glyph code array.
     *
     * @param frc The current font render context.
     * @param glyphCodes An array containin the ids of the glyphs that
     * the glyph vector will represent.
     *
     * @return The new glyph vector.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            int[] glyphCodes,
                                            CharacterIterator ci) {
        // costruct a string from the glyphCodes
        String str = "";
        for (int i = 0; i < glyphCodes.length; i++) {
            str += glyphUnicodes[glyphCodes[i]];
        }
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return createGlyphVector(frc, sci);
    }

    /**
     * Returns a new GVTGlyphVector object for the specified String.
     *
     * @param frc The current font render context.
     * @param str The string that the glyph vector will represent.
     *
     * @return The new glyph vector.
     */
    public GVTGlyphVector createGlyphVector(FontRenderContext frc,
                                            String str) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return createGlyphVector(frc, sci);
    }

    /**
     * Creates a new GVTFont object by replicating this font object and
     * applying a new size to it.
     *
     * @param size The size of the new font.
     *
     * @param return The new font object.
     */
    public GVTFont deriveFont(float size) {
        return new SVGGVTFont(size, fontFace, glyphUnicodes, glyphNames,
                              glyphLangs, glyphOrientations, glyphForms, ctx,
                              glyphElements, missingGlyphElement,
                              hkernElements, vkernElements, textElement);
    }

    /**
     * Returns the line metrics for the specified text.
     *
     * @param chars The character array containing the text.
     * @param beginIndex The index of the first character.
     * @param limit The limit of characters.
     * @param frc The current font render context.
     *
     * @return The new GVTLineMetrics object.
     */
    public GVTLineMetrics getLineMetrics(char[] chars, int beginIndex,
                                         int limit,
                                         FontRenderContext frc) {
        StringCharacterIterator sci =
            new StringCharacterIterator(new String(chars));
        return getLineMetrics(sci, beginIndex, limit, frc);
    }

    /**
     * Returns the line metrics for the specified text.
     *
     * @param ci The character iterator containing the text.
     * @param beginIndex The index of the first character.
     * @param limit The limit of characters.
     * @param frc The current font render context.
     *
     * @return The new GVTLineMetrics object.
     */
    public GVTLineMetrics getLineMetrics(CharacterIterator ci, int beginIndex,
                                         int limit, FontRenderContext frc) {
        if (lineMetrics != null) 
            return lineMetrics;

        float fontHeight = fontFace.getUnitsPerEm();
        float scale = fontSize/fontHeight;

        float ascent = fontFace.getAscent() * scale;
        float descent = fontFace.getDescent() * scale;

        float[] baselineOffsets = new float[3];
        baselineOffsets[Font.ROMAN_BASELINE]   = 0;
        baselineOffsets[Font.CENTER_BASELINE]  = (ascent+descent)/2-ascent;
        baselineOffsets[Font.HANGING_BASELINE] = -ascent;

        float stOffset    = fontFace.getStrikethroughPosition() * -scale;
        float stThickness = fontFace.getStrikethroughThickness() * scale;
        float ulOffset    = fontFace.getUnderlinePosition() * scale;
        float ulThickness = fontFace.getUnderlineThickness() * scale;
        float olOffset    = fontFace.getOverlinePosition() * -scale;
        float olThickness = fontFace.getOverlineThickness() * scale;


        lineMetrics = new GVTLineMetrics
            (ascent, Font.ROMAN_BASELINE, baselineOffsets, descent, 
             fontHeight, fontHeight, limit-beginIndex, 
             stOffset, stThickness, 
             ulOffset, ulThickness, 
             olOffset, olThickness);
        return lineMetrics;
    }

    /**
     * Returns the line metrics for the specified text.
     *
     * @param str The string containing the text.
     * @param frc The current font render context.
     *
     * @return The new GVTLineMetrics object.
     */
    public GVTLineMetrics getLineMetrics(String str, FontRenderContext frc) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return getLineMetrics(sci, 0, str.length(), frc);
    }

    /**
     * Returns the line metrics for the specified text.
     *
     * @param str The string containing the text.
     * @param beginIndex The index of the first character.
     * @param limit The limit of characters.
     * @param frc The current font render context.
     *
     * @return The new GVTLineMetrics object.
     */
    public GVTLineMetrics getLineMetrics(String str, int beginIndex, int limit,
                                         FontRenderContext frc) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return getLineMetrics(sci, beginIndex, limit, frc);
    }

    /**
     * Returns the size of this font.
     *
     * @return The font size.
     */
    public float getSize() {
        return fontSize;
    }

    /**
     * Returns a string representation of this font.
     * This is for debugging purposes only.
     *
     * @return A string representation of this font.
     */
    public String toString() {
        return fontFace.getFamilyName() + " " + fontFace.getFontWeight() + " "
              + fontFace.getFontStyle();
    }
}
