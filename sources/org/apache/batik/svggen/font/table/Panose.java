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

package org.apache.batik.svggen.font.table;

/**
 * @version $Id$
 * @author <a href="mailto:david@steadystate.co.uk">David Schweinsberg</a>
 */
public class Panose {

  byte bFamilyType = 0;
  byte bSerifStyle = 0;
  byte bWeight = 0;
  byte bProportion = 0;
  byte bContrast = 0;
  byte bStrokeVariation = 0;
  byte bArmStyle = 0;
  byte bLetterform = 0;
  byte bMidline = 0;
  byte bXHeight = 0;

  /** Creates new Panose */
  public Panose(byte[] panose) {
    bFamilyType = panose[0];
    bSerifStyle = panose[1];
    bWeight = panose[2];
    bProportion = panose[3];
    bContrast = panose[4];
    bStrokeVariation = panose[5];
    bArmStyle = panose[6];
    bLetterform = panose[7];
    bMidline = panose[8];
    bXHeight = panose[9];
  }

  public byte getFamilyType() {
    return bFamilyType;
  }
  
  public byte getSerifStyle() {
    return bSerifStyle;
  }
  
  public byte getWeight() {
    return bWeight;
  }

  public byte getProportion() {
    return bProportion;
  }
  
  public byte getContrast() {
    return bContrast;
  }
  
  public byte getStrokeVariation() {
    return bStrokeVariation;
  }
  
  public byte getArmStyle() {
    return bArmStyle;
  }
  
  public byte getLetterForm() {
    return bLetterform;
  }
  
  public byte getMidline() {
    return bMidline;
  }
  
  public byte getXHeight() {
    return bXHeight;
  }
  
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(String.valueOf(bFamilyType)).append(" ")
      .append(String.valueOf(bSerifStyle)).append(" ")
      .append(String.valueOf(bWeight)).append(" ")
      .append(String.valueOf(bProportion)).append(" ")
      .append(String.valueOf(bContrast)).append(" ")
      .append(String.valueOf(bStrokeVariation)).append(" ")
      .append(String.valueOf(bArmStyle)).append(" ")
      .append(String.valueOf(bLetterform)).append(" ")
      .append(String.valueOf(bMidline)).append(" ")
      .append(String.valueOf(bXHeight));
    return sb.toString();
  }
}
