/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.dom.svg;

import java.lang.ref.WeakReference;

import org.apache.batik.dom.AbstractDocument;

import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGClipPathElement;

/**
 * This class implements {@link org.w3c.dom.svg.SVGClipPathElement}.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class SVGOMClipPathElement
    extends    SVGClippingMaskingElement
    implements SVGClipPathElement {

    /**
     * The DefaultAttributeValueProducer for clipPathUnits.
     */
    protected final static DefaultAttributeValueProducer
        CLIP_PATH_UNITS_DEFAULT_VALUE_PRODUCER =
        new DefaultAttributeValueProducer() {
                public String getDefaultAttributeValue() {
                    return SVG_CLIP_PATH_CLIP_PATH_UNITS_DEFAULT_VALUE;
                }
            };

    /**
     * The clipPathUnits attribute reference.
     */
    protected transient WeakReference clipPathUnitsReference;

    /**
     * Creates a new SVGOMClipPathElement object.
     */
    protected SVGOMClipPathElement() {
    }

    /**
     * Creates a new SVGOMClipPathElement object.
     * @param prefix The namespace prefix.
     * @param owner The owner document.
     */
    public SVGOMClipPathElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getLocalName()}.
     */
    public String getLocalName() {
        return SVG_CLIP_PATH_TAG;
    }

    /**
     * <b>DOM</b>: Implements {@link
     * org.w3c.dom.svg.SVGGradientElement#getGradientUnits()}.
     */
    public SVGAnimatedEnumeration getClipPathUnits() {
        SVGAnimatedEnumeration result;
        if (clipPathUnitsReference == null ||
            (result = (SVGAnimatedEnumeration)clipPathUnitsReference.get()) == null) {
            result = new SVGOMAnimatedEnumeration(this, null,
                                                  SVG_CLIP_PATH_UNITS_ATTRIBUTE,
                                                  STRING_TO_SHORT_UNITS,
                                                  SHORT_TO_STRING_UNITS,
                                                CLIP_PATH_UNITS_DEFAULT_VALUE_PRODUCER);
            clipPathUnitsReference = new WeakReference(result);
        }
        return result;
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new SVGOMClipPathElement();
    }
}
