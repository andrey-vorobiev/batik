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

package org.apache.batik.gvt.font;

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import java.util.HashMap;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

/**
 * A font family class for AWT fonts.
 *
 * @author <a href="mailto:bella.robinson@cmis.csiro.au">Bella Robinson</a>
 * @version $Id$
 */
public class AWTFontFamily implements GVTFontFamily {

    protected GVTFontFace fontFace;
    protected Font   font;

    /**
     * Constructs an AWTFontFamily with the specified familyName.
     *
     * @param familyName The name of the font family.
     */
    public AWTFontFamily(GVTFontFace fontFace) {
        this.fontFace = fontFace;
    }

    /**
     * Constructs an AWTFontFamily with the specified familyName.
     *
     * @param familyName The name of the font family.
     */
    public AWTFontFamily(String familyName) {
        this(new GVTFontFace(familyName));
    }

    /**
     * Constructs an AWTFontFamily with the specified familyName.
     *
     * @param familyName The name of the font family.
     */
    public AWTFontFamily(GVTFontFace fontFace, Font font) {
        this.fontFace = fontFace;
        this.font     = font;
    }

    /**
     * Returns the font family name.
     *
     * @return The family name.
     */
    public String getFamilyName() {
        return fontFace.getFamilyName();
    }

    /**
     * Returns the font-face information for this font family.
     */
    public GVTFontFace getFontFace() {
        return fontFace;
    }

    /**
     * Derives a GVTFont object of the correct size.
     *
     * @param size The required size of the derived font.
     * @param aci  The character iterator that will be rendered using
     *             the derived font.  
     */
    public GVTFont deriveFont(float size, AttributedCharacterIterator aci) {
        if (font != null)
            return new AWTGVTFont(font, size);

        HashMap fontAttributes = new HashMap(aci.getAttributes());
        fontAttributes.put(TextAttribute.SIZE, new Float(size));
        fontAttributes.put(TextAttribute.FAMILY, fontFace.getFamilyName());
        fontAttributes.remove(GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_DELIMITER);
        return new AWTGVTFont(fontAttributes);
    }

}
