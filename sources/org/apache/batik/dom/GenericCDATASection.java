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

package org.apache.batik.dom;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * This class implements the {@link org.w3c.dom.CDATASection} interface.
 *
 * @author <a href="mailto:stephane@hillion.org">Stephane Hillion</a>
 * @version $Id$
 */
public class GenericCDATASection extends AbstractText implements CDATASection {
    /**
     * Is this element immutable?
     */
    protected boolean readonly;

    /**
     * Creates a new CDATASection object.
     */
    protected GenericCDATASection() {
    }

    /**
     * Creates a new CDATASection object.
     */
    public GenericCDATASection(String value, AbstractDocument owner) {
	ownerDocument = owner;
	setNodeValue(value);
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeName()}.
     * @return "#cdata-section".
     */
    public String getNodeName() {
	return "#cdata-section";
    }

    /**
     * <b>DOM</b>: Implements {@link org.w3c.dom.Node#getNodeType()}.
     * @return {@link org.w3c.dom.Node#CDATA_SECTION_NODE}
     */
    public short getNodeType() {
	return CDATA_SECTION_NODE;
    }

    /**
     * Tests whether this node is readonly.
     */
    public boolean isReadonly() {
	return readonly;
    }

    /**
     * Sets this node readonly attribute.
     */
    public void setReadonly(boolean v) {
	readonly = v;
    }

    /**
     * Creates a text node of the current type.
     */
    protected Text createTextNode(String text) {
	return getOwnerDocument().createCDATASection(text);
    }

    /**
     * Returns a new uninitialized instance of this object's class.
     */
    protected Node newNode() {
        return new GenericCDATASection();
    }
}
