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

package org.apache.batik.gvt.text;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * GVTACIImpl
 *
 * Used to implement SVG &lt;tspan&gt; and &lt;text&gt;
 * attributes.  This implementation is designed for efficient support
 * of per-character attributes (i.e. single character attribute spans).
 * It supports an extended set of TextAttributes, via inner class
 * GVTAttributedCharacterIterator.TextAttributes.
 *
 * @author <a href="mailto:bill.haneman@ireland.sun.com">Bill Haneman</a>
 * @version $Id$
 */

public class GVTACIImpl
                implements GVTAttributedCharacterIterator {

    private String simpleString;
    private Set allAttributes;
    private ArrayList mapList;
    private static int START_RUN = 2;
    private static int END_RUN = 3;
    private static int MID_RUN = 1;
    private static int SINGLETON = 0;
    private int[] charInRun;
    private CharacterIterator iter = null;
    private int currentIndex = -1;

    /**
     * Constructs a new GVTAttributedCharacterIterator with no attributes
     *     and a null string.
     */
    public GVTACIImpl() {
        simpleString = "";
        buildAttributeTables();
    }

    /**
     * Constructs a GVTACIImpl whose contents are
     *     equivalent to those of aci.
     * This constructor creates a new copy of the source data in <tt>aci</tt>.
     */
    public GVTACIImpl(AttributedCharacterIterator aci) {
        buildAttributeTables(aci);
    }

    /**
     * Sets this iterator's contents to an unattributed copy of String s.
     */
    public void setString(String s) {
        simpleString = s;
        iter = new StringCharacterIterator(simpleString);
        buildAttributeTables();
    }

    /**
     * Assigns this iterator's contents to be equivalent to AttributedString s.
     */
    public void setString(AttributedString s) {
        iter = s.getIterator();
        buildAttributeTables((AttributedCharacterIterator) iter);
    }

    /**
     * Sets values of a per-character attribute associated with the
     *     content string.
     * Characters from <tt>beginIndex</tt> to <tt>endIndex</tt>
     *     (zero-offset) are assigned values for attribute key <tt>attr</tt>
     *     from the array <tt>attValues.</tt>
     * If the length of attValues is less than character span
     *     <tt>(endIndex-beginIndex)</tt> the last value is duplicated;
     *     if attValues is longer than the character span
     *     the extra values are ignored.
     * Note that if either beginIndex or endIndex are outside the bounds
     *     of the current character array they are clipped accordingly.
     */
    public void setAttributeArray
        (GVTAttributedCharacterIterator.TextAttribute attr,
         Object[] attValues, int beginIndex, int endIndex) {

        beginIndex = Math.max(beginIndex, 0);
        endIndex = Math.min(endIndex, simpleString.length());
        if (charInRun[beginIndex] == END_RUN) {
            if (charInRun[beginIndex - 1] == MID_RUN) {
                charInRun[beginIndex - 1] = END_RUN;
            } else {
                charInRun[beginIndex - 1] = SINGLETON;
            }
        }
        if (charInRun[endIndex + 1] == END_RUN) {
            charInRun[endIndex + 1] = SINGLETON;
        } else if (charInRun[endIndex + 1] == MID_RUN) {
            charInRun[endIndex + 1] = START_RUN;
        }
        for (int i = beginIndex; i <= endIndex; ++i) {
            charInRun[i] = SINGLETON;
            int n = Math.min(i, attValues.length - 1);
            ((Map) mapList.get(i)).put(attr, attValues[n]);
        }
    }

    //From java.text.AttributedCharacterIterator

    /**
     * Get the keys of all attributes defined on the iterator's text range.
     */
    public Set getAllAttributeKeys() {
        return allAttributes;
    }

    /**
     * Get the value of the named attribute for the current
     *     character.
     */
    public Object getAttribute(AttributedCharacterIterator.Attribute attribute)
    {
        return getAttributes().get(attribute);
    }

    /**
     * Returns a map with the attributes defined on the current
     * character.
     */
    public Map getAttributes() {
        return (Map) mapList.get(currentIndex);
    }

    /**
     * Get the index of the first character following the
     *     run with respect to all attributes containing the current
     *     character.
     */
    public int getRunLimit() {
        int  ndx = currentIndex;
        do {
            ++ndx;
        } while (charInRun[ndx] == MID_RUN);
        return ndx;
    }

    /**
     * Get the index of the first character following the
     *      run with respect to the given attribute containing the current
     *      character.
     */
    public int getRunLimit(AttributedCharacterIterator.Attribute attribute) {
        int ndx = currentIndex;
        Object  value = getAttributes().get(attribute);

        //to avoid null pointer, treat null value as special case:-(
        if (value == null) {
            do {
                 ++ndx;
            } while (((Map) mapList.get(ndx)).get(attribute) == null);
        } else {
            do {
                ++ndx;
            } while (value.equals(((Map) mapList.get(ndx)).get(attribute)));
        }
        return ndx;
    }


    /**
     * Get the index of the first character following the
     *     run with respect to the given attributes containing the current
     *     character.
     */
    public int getRunLimit(Set attributes) {
        int ndx = currentIndex;
        do {
            ++ndx;
        } while (attributes.equals(mapList.get(ndx)));
        return ndx;
    }

    /**
     * Get the index of the first character of the run with
     *    respect to all attributes containing the current character.
     */
    public int getRunStart() {
        int ndx = currentIndex;
        while (charInRun[ndx] == MID_RUN) --ndx;
        return ndx;
    }

    /**
     * Get the index of the first character of the run with
     *      respect to the given attribute containing the current character.
     * @param attribute The attribute for whose appearance the first offset
     *      is requested.
     */
    public int getRunStart(AttributedCharacterIterator.Attribute attribute) {
        int ndx = currentIndex - 1;
        Object value = getAttributes().get(attribute);

        //to avoid null pointer, treat null value as special case:-(
        try {
            if (value == null) {
                while (((Map) mapList.get(ndx - 1)).get(attribute) == null)
                    --ndx;
            } else {
                while (value.equals(
                        ((Map) mapList.get(ndx - 1)).get(attribute)) )
                    --ndx;
            }
        } catch(IndexOutOfBoundsException e) {
        }
        return ndx;
    }

    /**
     * Get the index of the first character of the run with
     *      respect to the given attributes containing the current character.
     * @param attributes the Set of attributes which begins at the returned
     *      index.
     */
    public int getRunStart(Set attributes) {
        int ndx = currentIndex;
        try {
            while (attributes.equals(mapList.get(ndx - 1))) --ndx;
        } catch(IndexOutOfBoundsException e) {
        }
        return ndx;
    }

    //From CharacterIterator

    /**
     * Create a copy of this iterator
     */
    public Object clone() {
        GVTAttributedCharacterIterator cloneACI =
                new GVTACIImpl(this);
        return cloneACI;
    }

    /**
     * Get the character at the current position (as returned
     *      by getIndex()).
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char current() {
        return iter.current();
    }

    /**
     * Sets the position to getBeginIndex().
     * @return the character at the start index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char first() {
        return iter.first();
    }

    /**
     * Get the start index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getBeginIndex() {
        return iter.getBeginIndex();
    }

    /**
     * Get the end index of the text.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getEndIndex() {
        return iter.getEndIndex();
    }

    /**
     * Get the current index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public int getIndex() {
        return iter.getIndex();
    }

    /**
     * Sets the position to getEndIndex()-1 (getEndIndex() if
     * the text is empty) and returns the character at that position.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char last() {
        return iter.last();
    }

    /**
     * Increments the iterator's index by one, returning the next character.
     * @return the character at the new index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char next() {
        return iter.next();
    }

    /**
     * Decrements the iterator's index by one and returns
     * the character at the new index.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char previous() {
        return iter.previous();
    }

    /**
     * Sets the position to the specified position in the text.
     * @param position The new (current) index into the text.
     * @return the character at new index <em>position</em>.
     * <br><b>Specified by:</b> java.text.CharacterIterator.
     */
    public char setIndex(int position) {
        return iter.setIndex(position);
    }

    //Private methods:

    private void buildAttributeTables() {
        allAttributes = new HashSet();
        mapList = new ArrayList(simpleString.length());
        charInRun = new int[simpleString.length()];
        for (int i = 0; i < charInRun.length; ++i) {
            charInRun[i] = SINGLETON;
            /*
             * XXX TODO: loosen assumption, initially each character has its own
             * attribute map.
             */
            mapList.set(i, new HashMap());
        }
    }

    private void buildAttributeTables(AttributedCharacterIterator aci) {
        allAttributes = aci.getAllAttributeKeys();
        int length = aci.getEndIndex() - aci.getBeginIndex();
        mapList = new ArrayList(length);
        charInRun = new int[length];
        char  c = aci.first();
        char chars[] = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = c;
            charInRun[i] = SINGLETON;
            /*
             * XXX TODO:loosen assumption, initially each character
             * has its own attribute map.
             */
            mapList.set(i, new HashMap(aci.getAttributes()));
            c = aci.next();
        }
        simpleString = new String(chars);
    }

    //Inner classes:

    /**
     * AttributeFilter which converts (extended) location attributes
     * SVGAttributedCharacterIterator.TextAttribute.X, TextAttribute.Y,
     * TextAttribute.ROTATE attributes to TextAttribute.TRANSFORM attributes.
     */
    public class TransformAttributeFilter implements
                     GVTAttributedCharacterIterator.AttributeFilter {

        /**
         * Return a new AttributedCharacterIterator instance
         * in which location attributes have been converted to
         * TextAttribute.TRANSFORM attributes.
         */
        public AttributedCharacterIterator
                     mutateAttributes(AttributedCharacterIterator aci) {
            //TODO:Implement this !!!
            return aci;
        }
    }
}
