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

package org.apache.batik.css.dom;

import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;

/**
 * This class implements the {@link org.w3c.dom.css.CSSValue},
 * {@link org.w3c.dom.css.CSSPrimitiveValue},
 * {@link org.w3c.dom.css.CSSValueList} interfaces.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class CSSOMValue
    implements CSSPrimitiveValue,
               CSSValueList,
               Counter,
               Rect,
               RGBColor {
    
    /**
     * The associated value.
     */
    protected ValueProvider valueProvider;

    /**
     * The modifications handler.
     */
    protected ModificationHandler handler;

    /**
     * The left component, if this value is a Rect.
     */
    protected LeftComponent leftComponent;

    /**
     * The right component, if this value is a Rect.
     */
    protected RightComponent rightComponent;

    /**
     * The bottom component, if this value is a Rect.
     */
    protected BottomComponent bottomComponent;

    /**
     * The top component, if this value is a Rect.
     */
    protected TopComponent topComponent;

    /**
     * The red component, if this value is a RGBColor.
     */
    protected RedComponent redComponent;

    /**
     * The green component, if this value is a RGBColor.
     */
    protected GreenComponent greenComponent;

    /**
     * The blue component, if this value is a RGBColor.
     */
    protected BlueComponent blueComponent;

    /**
     * The list items.
     */
    protected CSSValue[] items;

    /**
     * Creates a new CSSOMValue.
     */
    public CSSOMValue(ValueProvider vp) {
        valueProvider = vp;
    }

    /**
     * Sets the modification handler of this value.
     */
    public void setModificationHandler(ModificationHandler h) {
        handler = h;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValue#getCssText()}.
     */
    public String getCssText() {
        return valueProvider.getValue().getCssText();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSValue#setCssText(String)}.
     */
    public void setCssText(String cssText) throws DOMException {
	if (handler == null) {
            throw new DOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
	} else {
            handler.textChanged(cssText);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSValue#getCssValueType()}.
     */
    public short getCssValueType() {
        return valueProvider.getValue().getCssValueType();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getPrimitiveType()}.
     */
    public short getPrimitiveType() {
        return valueProvider.getValue().getPrimitiveType();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
     */
    public void setFloatValue(short unitType, float floatValue)
        throws DOMException {
	if (handler == null) {
            throw new DOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
	} else {
            handler.floatValueChanged(unitType, floatValue);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getFloatValue(short)}.
     */
    public float getFloatValue(short unitType) throws DOMException {
        return convertFloatValue(unitType, valueProvider.getValue());
    }

    /**
     * Converts the actual float value to the given unit type.
     */
    public static float convertFloatValue(short unitType, Value value) {
	switch (unitType) {
	case CSSPrimitiveValue.CSS_NUMBER:
	case CSSPrimitiveValue.CSS_PERCENTAGE:
	case CSSPrimitiveValue.CSS_EMS:
	case CSSPrimitiveValue.CSS_EXS:
	case CSSPrimitiveValue.CSS_DIMENSION:
	case CSSPrimitiveValue.CSS_PX:
	    if (value.getPrimitiveType() == unitType) {
		return value.getFloatValue();
	    }
	    break;
	case CSSPrimitiveValue.CSS_CM:
	    return toCentimeters(value);
	case CSSPrimitiveValue.CSS_MM:
	    return toMillimeters(value);
	case CSSPrimitiveValue.CSS_IN:
	    return toInches(value);
	case CSSPrimitiveValue.CSS_PT:
	    return toPoints(value);
	case CSSPrimitiveValue.CSS_PC:
	    return toPicas(value);
	case CSSPrimitiveValue.CSS_DEG:
	    return toDegrees(value);
	case CSSPrimitiveValue.CSS_RAD:
	    return toRadians(value);
	case CSSPrimitiveValue.CSS_GRAD:
	    return toGradians(value);
	case CSSPrimitiveValue.CSS_MS:
	    return toMilliseconds(value);
	case CSSPrimitiveValue.CSS_S:
	    return toSeconds(value);
	case CSSPrimitiveValue.CSS_HZ:
	    return toHertz(value);
	case CSSPrimitiveValue.CSS_KHZ:
	    return tokHertz(value);
	}
        throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
    }

    /**
     * Converts the current value into centimeters.
     */
    protected static float toCentimeters(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_CM:
	    return value.getFloatValue();
	case CSSPrimitiveValue.CSS_MM:
	    return (float)(value.getFloatValue() / 10);
	case CSSPrimitiveValue.CSS_IN:
	    return (float)(value.getFloatValue() * 2.54);
	case CSSPrimitiveValue.CSS_PT:
	    return (float)(value.getFloatValue() * 2.54 / 72);
	case CSSPrimitiveValue.CSS_PC:
	    return (float)(value.getFloatValue() * 2.54 / 6);
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into inches.
     */
    protected static float toInches(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_CM:
	    return (float)(value.getFloatValue() / 2.54);
	case CSSPrimitiveValue.CSS_MM:
	    return (float)(value.getFloatValue() / 25.4);
	case CSSPrimitiveValue.CSS_IN:
	    return value.getFloatValue();
	case CSSPrimitiveValue.CSS_PT:
	    return (float)(value.getFloatValue() / 72);
	case CSSPrimitiveValue.CSS_PC:
	    return (float) (value.getFloatValue() / 6);
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into millimeters.
     */
    protected static float toMillimeters(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_CM:
	    return (float)(value.getFloatValue() * 10);
	case CSSPrimitiveValue.CSS_MM:
	    return value.getFloatValue();
	case CSSPrimitiveValue.CSS_IN:
	    return (float)(value.getFloatValue() * 25.4);
	case CSSPrimitiveValue.CSS_PT:
	    return (float)(value.getFloatValue() * 25.4 / 72);
	case CSSPrimitiveValue.CSS_PC:
	    return (float)(value.getFloatValue() * 25.4 / 6);
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into points.
     */
    protected static float toPoints(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_CM:
	    return (float)(value.getFloatValue() * 72 / 2.54);
	case CSSPrimitiveValue.CSS_MM:
	    return (float)(value.getFloatValue() * 72 / 25.4);
	case CSSPrimitiveValue.CSS_IN:
	    return (float)(value.getFloatValue() * 72);
	case CSSPrimitiveValue.CSS_PT:
	    return value.getFloatValue();
	case CSSPrimitiveValue.CSS_PC:
	    return (float)(value.getFloatValue() * 12);
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into picas.
     */
    protected static float toPicas(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_CM:
	    return (float)(value.getFloatValue() * 6 / 2.54);
	case CSSPrimitiveValue.CSS_MM:
	    return (float)(value.getFloatValue() * 6 / 25.4);
	case CSSPrimitiveValue.CSS_IN:
	    return (float)(value.getFloatValue() * 6);
	case CSSPrimitiveValue.CSS_PT:
	    return (float)(value.getFloatValue() / 12);
	case CSSPrimitiveValue.CSS_PC:
	    return value.getFloatValue();
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into degrees.
     */
    protected static float toDegrees(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_DEG:
	    return value.getFloatValue();
	case CSSPrimitiveValue.CSS_RAD:
	    return (float)(value.getFloatValue() * 180 / Math.PI);
	case CSSPrimitiveValue.CSS_GRAD:
	    return (float)(value.getFloatValue() * 9 / 5);
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into radians.
     */
    protected static float toRadians(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_DEG:
	    return (float)(value.getFloatValue() * 5 / 9);
	case CSSPrimitiveValue.CSS_RAD:
	    return value.getFloatValue();
	case CSSPrimitiveValue.CSS_GRAD:
	    return (float)(value.getFloatValue() * 100 / Math.PI);
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into gradians.
     */
    protected static float toGradians(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_DEG:
	    return (float)(value.getFloatValue() * Math.PI / 180);
	case CSSPrimitiveValue.CSS_RAD:
	    return (float)(value.getFloatValue() * Math.PI / 100);
	case CSSPrimitiveValue.CSS_GRAD:
	    return value.getFloatValue();
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into milliseconds.
     */
    protected static float toMilliseconds(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_MS:
	    return value.getFloatValue();
	case CSSPrimitiveValue.CSS_S:
	    return (float)(value.getFloatValue() * 1000);
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }
	
    /**
     * Converts the current value into seconds.
     */
    protected static float toSeconds(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_MS:
	    return (float)(value.getFloatValue() / 1000);
	case CSSPrimitiveValue.CSS_S:
	    return value.getFloatValue();
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }
	
    /**
     * Converts the current value into Hertz.
     */
    protected static float toHertz(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_HZ:
	    return value.getFloatValue();
	case CSSPrimitiveValue.CSS_KHZ:
	    return (float)(value.getFloatValue() / 1000);
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

    /**
     * Converts the current value into kHertz.
     */
    protected static float tokHertz(Value value) {
	switch (value.getPrimitiveType()) {
	case CSSPrimitiveValue.CSS_HZ:
	    return (float)(value.getFloatValue() * 1000);
	case CSSPrimitiveValue.CSS_KHZ:
	    return value.getFloatValue();
	default:
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
	}
    }

   /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#setStringValue(short,String)}.
     */
    public void setStringValue(short stringType, String stringValue)
        throws DOMException {
	if (handler == null) {
            throw new DOMException
                (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
	} else {
            handler.stringValueChanged(stringType, stringValue);
	}
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getStringValue()}.
     */
    public String getStringValue() throws DOMException {
        return valueProvider.getValue().getStringValue();
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getCounterValue()}.
     */
    public Counter getCounterValue() throws DOMException {
        return this;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getRectValue()}.
     */
    public Rect getRectValue() throws DOMException {
        return this;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.css.CSSPrimitiveValue#getRGBColorValue()}.
     */
    public RGBColor getRGBColorValue() throws DOMException {
        return this;
    }

    // CSSValueList ///////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValueList#getLength()}.
     */
    public int getLength() {
        return valueProvider.getValue().getLength();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.CSSValueList#item(int)}.
     */
    public CSSValue item(int index) {
        int len = valueProvider.getValue().getLength();
        if (index < 0 || index >= len) {
            return null;
        }
        if (items == null) {
            items = new CSSValue[valueProvider.getValue().getLength()];
        } else if (items.length < len) {
            CSSValue[] nitems = new CSSValue[len];
            for (int i = 0; i < items.length; i++) {
                nitems[i] = items[i];
            }
            items = nitems;
        }
        CSSValue result = items[index];
        if (result == null) {
            items[index] = result = new ListComponent(index);
        }
        return result;
    }

    // Counter /////////////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Counter#getIdentifier()}.
     */
    public String getIdentifier() {
        return valueProvider.getValue().getIdentifier();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Counter#getListStyle()}.
     */
    public String getListStyle() {
        return valueProvider.getValue().getListStyle();
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Counter#getSeparator()}.
     */
    public String getSeparator() {
        return valueProvider.getValue().getSeparator();
    }

    // Rect ///////////////////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Rect#getTop()}.
     */
    public CSSPrimitiveValue getTop() {
        valueProvider.getValue().getTop();
        if (topComponent == null) {
            topComponent = new TopComponent();
        }
        return topComponent;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Rect#getRight()}.
     */
    public CSSPrimitiveValue getRight() { 
        valueProvider.getValue().getRight();
        if (rightComponent == null) {
            rightComponent = new RightComponent();
        }
        return rightComponent;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Rect#getBottom()}.
     */
    public CSSPrimitiveValue getBottom() {
        valueProvider.getValue().getBottom();
        if (bottomComponent == null) {
            bottomComponent = new BottomComponent();
        }
        return bottomComponent;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.Rect#getLeft()}.
     */
    public CSSPrimitiveValue getLeft() {
        valueProvider.getValue().getLeft();
        if (leftComponent == null) {
            leftComponent = new LeftComponent();
        }
        return leftComponent;
    }

    // RGBColor ///////////////////////////////////////////////////

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.RGBColor#getRed()}.
     */
    public CSSPrimitiveValue getRed() {
        valueProvider.getValue().getRed();
        if (redComponent == null) {
            redComponent = new RedComponent();
        }
        return redComponent;
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.RGBColor#getGreen()}.
     */
    public CSSPrimitiveValue getGreen() {
        valueProvider.getValue().getGreen();
        if (greenComponent == null) {
            greenComponent = new GreenComponent();
        }
        return greenComponent;
    }


    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.css.RGBColor#getBlue()}.
     */
    public CSSPrimitiveValue getBlue() {
        valueProvider.getValue().getBlue();
        if (blueComponent == null) {
            blueComponent = new BlueComponent();
        }
        return blueComponent;
    }

    /**
     * To provides the actual value.
     */
    public interface ValueProvider {

        /**
         * Returns the current value associated with this object.
         */
        Value getValue();
    }

    /**
     * To manage the modifications on a CSS value.
     */
    public interface ModificationHandler {

        /**
         * Called when the value text has changed.
         */
        void textChanged(String text) throws DOMException;

        /**
         * Called when the float value has changed.
         */
        void floatValueChanged(short unit, float value) throws DOMException;

        /**
         * Called when the string value has changed.
         */
        void stringValueChanged(short type, String value) throws DOMException;

        /**
         * Called when the left value text has changed.
         */
        void leftTextChanged(String text) throws DOMException;

        /**
         * Called when the left float value has changed.
         */
        void leftFloatValueChanged(short unit, float value)
            throws DOMException;

        /**
         * Called when the top value text has changed.
         */
        void topTextChanged(String text) throws DOMException;

        /**
         * Called when the top float value has changed.
         */
        void topFloatValueChanged(short unit, float value)
            throws DOMException;

        /**
         * Called when the right value text has changed.
         */
        void rightTextChanged(String text) throws DOMException;

        /**
         * Called when the right float value has changed.
         */
        void rightFloatValueChanged(short unit, float value)
            throws DOMException;

        /**
         * Called when the bottom value text has changed.
         */
        void bottomTextChanged(String text) throws DOMException;

        /**
         * Called when the bottom float value has changed.
         */
        void bottomFloatValueChanged(short unit, float value)
            throws DOMException;

        /**
         * Called when the red value text has changed.
         */
        void redTextChanged(String text) throws DOMException;

        /**
         * Called when the red float value has changed.
         */
        void redFloatValueChanged(short unit, float value)
            throws DOMException;

        /**
         * Called when the green value text has changed.
         */
        void greenTextChanged(String text) throws DOMException;

        /**
         * Called when the green float value has changed.
         */
        void greenFloatValueChanged(short unit, float value)
            throws DOMException;

        /**
         * Called when the blue value text has changed.
         */
        void blueTextChanged(String text) throws DOMException;

        /**
         * Called when the blue float value has changed.
         */
        void blueFloatValueChanged(short unit, float value)
            throws DOMException;

        /**
         * Called when the list value text has changed.
         */
        void listTextChanged(int idx, String text) throws DOMException;

        /**
         * Called when the list float value has changed.
         */
        void listFloatValueChanged(int idx, short unit, float value)
            throws DOMException;

        /**
         * Called when the list string value has changed.
         */
        void listStringValueChanged(int idx, short unit, String value)
            throws DOMException;

    }

    /**
     * This class provides an abstract implementation of a ModificationHandler.
     */
    public abstract class AbstractModificationHandler
        implements ModificationHandler {

        /**
         * Returns the associated value.
         */
        protected abstract Value getValue();

        /**
         * Called when the float value has changed.
         */
        public void floatValueChanged(short unit, float value)
            throws DOMException {
            textChanged(FloatValue.getCssText(unit, value));
        }

        /**
         * Called when the string value has changed.
         */
        public void stringValueChanged(short type, String value)
            throws DOMException {
            textChanged(StringValue.getCssText(type, value));
        }

        /**
         * Called when the left value text has changed.
         */
        public void leftTextChanged(String text) throws DOMException {
            text = "rect(" +
                getValue().getTop().getCssText() + ", " +
                getValue().getRight().getCssText() + ", " +
                getValue().getBottom().getCssText() + ", " +
                text + ")";
            textChanged(text);
        }

        /**
         * Called when the left float value has changed.
         */
        public void leftFloatValueChanged(short unit, float value)
            throws DOMException {
            String text = "rect(" +
                getValue().getTop().getCssText() + ", " +
                getValue().getRight().getCssText() + ", " +
                getValue().getBottom().getCssText() + ", " +
                FloatValue.getCssText(unit, value) + ")";
            textChanged(text);
        }

        /**
         * Called when the top value text has changed.
         */
        public void topTextChanged(String text) throws DOMException {
            text = "rect(" +
                text + ", " +
                getValue().getRight().getCssText() + ", " +
                getValue().getBottom().getCssText() + ", " +
                getValue().getLeft().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the top float value has changed.
         */
        public void topFloatValueChanged(short unit, float value)
            throws DOMException {
            String text = "rect(" +
                FloatValue.getCssText(unit, value) + ", " +
                getValue().getRight().getCssText() + ", " +
                getValue().getBottom().getCssText() + ", " +
                getValue().getLeft().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the right value text has changed.
         */
        public void rightTextChanged(String text) throws DOMException {
            text = "rect(" +
                getValue().getTop().getCssText() + ", " +
                text + ", " +
                getValue().getBottom().getCssText() + ", " +
                getValue().getLeft().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the right float value has changed.
         */
        public void rightFloatValueChanged(short unit, float value)
            throws DOMException {
            String text = "rect(" +
                getValue().getTop().getCssText() + ", " +
                FloatValue.getCssText(unit, value) + ", " +
                getValue().getBottom().getCssText() + ", " +
                getValue().getLeft().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the bottom value text has changed.
         */
        public void bottomTextChanged(String text) throws DOMException {
            text = "rect(" +
                getValue().getTop().getCssText() + ", " +
                getValue().getRight().getCssText() + ", " +
                text + ", " +
                getValue().getLeft().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the bottom float value has changed.
         */
        public void bottomFloatValueChanged(short unit, float value)
            throws DOMException {
            String text = "rect(" +
                getValue().getTop().getCssText() + ", " +
                getValue().getRight().getCssText() + ", " +
                FloatValue.getCssText(unit, value) + ", " +
                getValue().getLeft().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the red value text has changed.
         */
        public void redTextChanged(String text) throws DOMException {
            text = "rgb(" +
                text + ", " +
                getValue().getGreen().getCssText() + ", " +
                getValue().getBlue().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the red float value has changed.
         */
        public void redFloatValueChanged(short unit, float value)
            throws DOMException {
            String text = "rgb(" +
                FloatValue.getCssText(unit, value) + ", " +
                getValue().getGreen().getCssText() + ", " +
                getValue().getBlue().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the green value text has changed.
         */
        public void greenTextChanged(String text) throws DOMException {
            text = "rgb(" +
                getValue().getRed().getCssText() + ", " +
                text + ", " +
                getValue().getBlue().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the green float value has changed.
         */
        public void greenFloatValueChanged(short unit, float value)
            throws DOMException {
            String text = "rgb(" +
                getValue().getRed().getCssText() + ", " +
                FloatValue.getCssText(unit, value) + ", " +
                getValue().getBlue().getCssText() + ")";
            textChanged(text);
        }

        /**
         * Called when the blue value text has changed.
         */
        public void blueTextChanged(String text) throws DOMException {
            text = "rgb(" +
                getValue().getRed().getCssText() + ", " +
                getValue().getGreen().getCssText() + ", " +
                text + ")";
            textChanged(text);
        }

        /**
         * Called when the blue float value has changed.
         */
        public void blueFloatValueChanged(short unit, float value)
            throws DOMException {
            String text = "rgb(" +
                getValue().getRed().getCssText() + ", " +
                getValue().getGreen().getCssText() + ", " +
                FloatValue.getCssText(unit, value) + ")";
            textChanged(text);
        }

        /**
         * Called when the list value text has changed.
         */
        public void listTextChanged(int idx, String text) throws DOMException {
            ListValue lv = (ListValue)getValue();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < idx; i++) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(text);
            int len = lv.getLength();
            for (int i = idx + 1; i < len; i++) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            text = sb.toString();
            textChanged(text);
        }

        /**
         * Called when the list float value has changed.
         */
        public void listFloatValueChanged(int idx, short unit, float value)
            throws DOMException {
            ListValue lv = (ListValue)getValue();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < idx; i++) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(FloatValue.getCssText(unit, value));
            int len = lv.getLength();
            for (int i = idx + 1; i < len; i++) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            textChanged(sb.toString());
        }

        /**
         * Called when the list string value has changed.
         */
        public void listStringValueChanged(int idx, short unit, String value)
            throws DOMException {
            ListValue lv = (ListValue)getValue();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < idx; i++) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(StringValue.getCssText(unit, value));
            int len = lv.getLength();
            for (int i = idx + 1; i < len; i++) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            textChanged(sb.toString());
        }
    }

    /**
     * To store a component.
     */
    protected abstract class AbstractComponent implements CSSPrimitiveValue {

        /**
         * The returns the actual value of this component.
         */
        protected abstract Value getValue();

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#getCssText()}.
         */
        public String getCssText() {
            return getValue().getCssText();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#getCssValueType()}.
         */
        public short getCssValueType() {
            return getValue().getCssValueType();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#getPrimitiveType()}.
         */
        public short getPrimitiveType() {
            return getValue().getPrimitiveType();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#getFloatValue(short)}.
         */
        public float getFloatValue(short unitType) throws DOMException {
            return convertFloatValue(unitType, getValue());
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#getStringValue()}.
         */
        public String getStringValue() throws DOMException {
            return valueProvider.getValue().getStringValue();
        }
        
        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#getCounterValue()}.
         */
        public Counter getCounterValue() throws DOMException {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
        }
        
        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#getRectValue()}.
         */
        public Rect getRectValue() throws DOMException {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#getRGBColorValue()}.
         */
        public RGBColor getRGBColorValue() throws DOMException {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
        }

        // CSSValueList ///////////////////////////////////////////////////////

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValueList#getLength()}.
         */
        public int getLength() {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
        }
        
        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValueList#item(int)}.
         */
        public CSSValue item(int index) {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
        }
    }

    /**
     * To store a Float component.
     */
    protected abstract class FloatComponent extends AbstractComponent {

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setStringValue(short,String)}.
         */
        public void setStringValue(short stringType, String stringValue)
            throws DOMException {
            throw new DOMException(DOMException.INVALID_ACCESS_ERR, "");
        }
    }

    /**
     * To represents a left component.
     */
    protected class LeftComponent extends FloatComponent {
        
        /**
         * The returns the actual value of this component.
         */
        protected Value getValue() {
            return valueProvider.getValue().getLeft();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#setCssText(String)}.
         */
        public void setCssText(String cssText) throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.leftTextChanged(cssText);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
         */
        public void setFloatValue(short unitType, float floatValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.leftFloatValueChanged(unitType, floatValue);
            }
        }

    }

    /**
     * To represents a top component.
     */
    protected class TopComponent extends FloatComponent {
        
        /**
         * The returns the actual value of this component.
         */
        protected Value getValue() {
            return valueProvider.getValue().getTop();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#setCssText(String)}.
         */
        public void setCssText(String cssText) throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.topTextChanged(cssText);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
         */
        public void setFloatValue(short unitType, float floatValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.topFloatValueChanged(unitType, floatValue);
            }
        }

    }

    /**
     * To represents a right component.
     */
    protected class RightComponent extends FloatComponent {
        
        /**
         * The returns the actual value of this component.
         */
        protected Value getValue() {
            return valueProvider.getValue().getRight();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#setCssText(String)}.
         */
        public void setCssText(String cssText) throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.rightTextChanged(cssText);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
         */
        public void setFloatValue(short unitType, float floatValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.rightFloatValueChanged(unitType, floatValue);
            }
        }

    }


    /**
     * To represents a bottom component.
     */
    protected class BottomComponent extends FloatComponent {
        
        /**
         * The returns the actual value of this component.
         */
        protected Value getValue() {
            return valueProvider.getValue().getBottom();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#setCssText(String)}.
         */
        public void setCssText(String cssText) throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.bottomTextChanged(cssText);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
         */
        public void setFloatValue(short unitType, float floatValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.bottomFloatValueChanged(unitType, floatValue);
            }
        }

    }


    /**
     * To represents a red component.
     */
    protected class RedComponent extends FloatComponent {
        
        /**
         * The returns the actual value of this component.
         */
        protected Value getValue() {
            return valueProvider.getValue().getRed();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#setCssText(String)}.
         */
        public void setCssText(String cssText) throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.redTextChanged(cssText);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
         */
        public void setFloatValue(short unitType, float floatValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.redFloatValueChanged(unitType, floatValue);
            }
        }

    }


    /**
     * To represents a green component.
     */
    protected class GreenComponent extends FloatComponent {
        
        /**
         * The returns the actual value of this component.
         */
        protected Value getValue() {
            return valueProvider.getValue().getGreen();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#setCssText(String)}.
         */
        public void setCssText(String cssText) throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.greenTextChanged(cssText);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
         */
        public void setFloatValue(short unitType, float floatValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.greenFloatValueChanged(unitType, floatValue);
            }
        }

    }

    /**
     * To represents a blue component.
     */
    protected class BlueComponent extends FloatComponent {
        
        /**
         * The returns the actual value of this component.
         */
        protected Value getValue() {
            return valueProvider.getValue().getBlue();
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#setCssText(String)}.
         */
        public void setCssText(String cssText) throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.blueTextChanged(cssText);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
         */
        public void setFloatValue(short unitType, float floatValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.blueFloatValueChanged(unitType, floatValue);
            }
        }

    }

    /**
     * To represents a List component.
     */
    protected class ListComponent extends AbstractComponent {
        
        /**
         * The index of this component.
         */
        protected int index;

        /**
         * Creates a new ListComponent.
         */
        public ListComponent(int idx) {
            index = idx;
        }

        /**
         * The returns the actual value of this component.
         */
        protected Value getValue() {
            if (index >= valueProvider.getValue().getLength()) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            }
            return valueProvider.getValue().item(index);
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSValue#setCssText(String)}.
         */
        public void setCssText(String cssText) throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.listTextChanged(index, cssText);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setFloatValue(short,float)}.
         */
        public void setFloatValue(short unitType, float floatValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.listFloatValueChanged(index, unitType, floatValue);
            }
        }

        /**
         * <b>DOM</b>: Implements {@link
         * org.w3c.dom.css.CSSPrimitiveValue#setStringValue(short,String)}.
         */
        public void setStringValue(short stringType, String stringValue)
            throws DOMException {
            if (handler == null) {
                throw new DOMException
                    (DOMException.NO_MODIFICATION_ALLOWED_ERR, "");
            } else {
                getValue();
                handler.listStringValueChanged(index, stringType, stringValue);
            }
        }
    }

}
