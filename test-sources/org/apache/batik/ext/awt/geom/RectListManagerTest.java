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

package org.apache.batik.ext.awt.geom;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.util.Base64Test;
import org.apache.batik.ext.awt.geom.RectListManager;

import java.awt.Rectangle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.PrintStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;



/**
 * This test validates that the text selection API's work properly.
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class RectListManagerTest extends AbstractTest {

    /**
     * Directory for reference files
     */
    public static final String REFERENCE_DIR
        = "test-references/org/apache/batik/ext/awt/geom/";

    public static final String VARIATION_DIR
        = "variation/";

    public static final String CANDIDATE_DIR
        = "candidate/";


    /**
     * Error when unable to load requested rects file
     * {0} = file
     * {1} = exception
     */
    public static final String ERROR_READING_RECTS
        = "RectListManagerTest.error.reading.rects";

    /**
     * Error when unable to read/open ref URL
     * {0} = URL
     * {1} = exception stack trace.
     */
    public static final String ERROR_CANNOT_READ_REF_URL
        = "RectListManagerTest.error.cannot.read.ref.url";

    /**
     * Result didn't match reference result.
     * {0} = first byte of mismatch
     */
    public static final String ERROR_WRONG_RESULT
        = "RectListManagerTest.error.wrong.result";

    /**
     * No Reference or Variation file to compaire with.
     * {0} = reference url
     */
    public static final String ERROR_NO_REFERENCE
        = "RectListManagerTest.error.no.reference";


    public static final String ENTRY_KEY_ERROR_DESCRIPTION
        = "RectListManagerTest.entry.key.error.description";

    protected URL    rects = null;
    protected URL    ref   = null;
    protected URL    var   = null;
    protected File   can   = null;

    /**
     * Constructor. ref is ignored if action == ROUND.
     * @param rects  The rects file to load
     * @param ref    The reference file.
     */
    public RectListManagerTest(String rects, String ref) {
        this.rects = resolveURL(REFERENCE_DIR+rects);
        this.ref   = resolveURL(REFERENCE_DIR+ref);
        this.var   = resolveURL(REFERENCE_DIR+VARIATION_DIR+ref);
        this.can   = new File(REFERENCE_DIR+CANDIDATE_DIR+ref);
    }

    /**
     * Resolves the input string as follows.
     * + First, the string is interpreted as a file description.
     *   If the file exists, then the file name is turned into
     *   a URL.
     * + Otherwise, the string is supposed to be a URL. If it
     *   is an invalid URL, an IllegalArgumentException is thrown.
     */
    protected URL resolveURL(String url){
        // Is url a file?
        File f = (new File(url)).getAbsoluteFile();
        if(f.getParentFile().exists()){
            try{
                return f.toURL();
            }catch(MalformedURLException e){
                throw new IllegalArgumentException();
            }
        }
        
        // url is not a file. It must be a regular URL...
        try{
            return new URL(url);
        }catch(MalformedURLException e){
            throw new IllegalArgumentException(url);
        }
    }

    /**
     * Returns this Test's name
     */
    public String getName() {
        return rects.toString();
    }


    static final String RECT_PREF         ="rect";
    static final String RLM_PREF          ="rectlistmanger";
    static final String MERGE_PREF        ="merge";
    static final String ADD_PREF          ="add";
    static final String SUBTRACT_PREF     ="subtract";
    static final String CONTAINS_ALL_PREF ="containsall";
    static final String REMOVE_ALL_PREF   ="removeall";
    static final String RETAIN_ALL_PREF   ="retainall";
    static final String PRINT_PREF        ="print";

    /**
     * This method will only throw exceptions if some aspect
     * of the test's internal operation fails.
     */
    public TestReport runImpl() throws Exception {
        DefaultTestReport report = new DefaultTestReport(this);

        int lineNo=0;
        try {
            BufferedReader reader;
            reader = new BufferedReader
                (new InputStreamReader(rects.openStream()));

            // Now write a canidate reference/variation file...
            if (can.exists())
                can.delete();

            FileOutputStream fos = new FileOutputStream(can);
            PrintStream ps = new PrintStream(fos);

            Map rlms = new HashMap();
            RectListManager currRLM = null;
            String          currID  = null;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                line = line.toLowerCase();
                StringTokenizer st = new StringTokenizer(line);

                // Check blank line...
                if (!st.hasMoreTokens()) continue;

                // Get first token
                String pref = st.nextToken();

                // Check for comment.
                if (pref.startsWith("#")) continue;

                if        (RECT_PREF.equals(pref)) {
                    if (st.countTokens() != 4) continue;
                    if (currRLM == null) continue;

                    int x = Integer.parseInt(st.nextToken());
                    int y = Integer.parseInt(st.nextToken());
                    int w = Integer.parseInt(st.nextToken());
                    int h = Integer.parseInt(st.nextToken());
                    currRLM.add(new Rectangle(x, y, w, h));
                } 
                else if (RLM_PREF.equals(pref)) {
                    String id = st.nextToken();
                    Object o = rlms.get(id);
                    if (o == null) {
                        o = new RectListManager();
                        rlms.put(id, o);
                    }
                    currRLM = (RectListManager)o;
                    currID  = id;
                } 
                else if (MERGE_PREF.equals(pref)) {
                    if (currRLM == null) continue;
                    int overhead     = Integer.parseInt(st.nextToken());
                    int lineOverhead = Integer.parseInt(st.nextToken());
                    currRLM.mergeRects(overhead, lineOverhead);
                } 
                else if (ADD_PREF.equals(pref)) {
                    if (currRLM == null) continue;
                    String id = st.nextToken();
                    Object o = rlms.get(id);
                    if (o == null) continue;
                    currRLM.add((RectListManager)o);
                } 
                else if (SUBTRACT_PREF.equals(pref)) {
                    if (currRLM == null) continue;
                    String id = st.nextToken();
                    Object o = rlms.get(id);
                    if (o == null) continue;
                    int overhead = Integer.parseInt(st.nextToken());
                    int lineOverhead = Integer.parseInt(st.nextToken());
                    currRLM.subtract((RectListManager)o, 
                                     overhead, lineOverhead);
                }
                else if (CONTAINS_ALL_PREF.equals(pref)) {
                    if (currRLM == null) continue;
                    String id = st.nextToken();
                    Object o = rlms.get(id);
                    if (o == null) continue;
                    RectListManager rlm = (RectListManager)o;
                    ps.println("ID: " + currID + " Sz: " + currRLM.size());
                    
                    if (currRLM.containsAll(rlm)) {
                        ps.println("  Contains all: " + id + 
                                   " Sz: " + rlm.size());
                    } else {
                        ps.println("  Does not contain all: " + id + 
                                   " Sz: " + rlm.size());
                    }
                    ps.println();
                }                
                else if (REMOVE_ALL_PREF.equals(pref)) {
                    if (currRLM == null) continue;
                    String id = st.nextToken();
                    Object o = rlms.get(id);
                    if (o == null) continue;
                    currRLM.removeAll((RectListManager)o);
                }                
                else if (RETAIN_ALL_PREF.equals(pref)) {
                    if (currRLM == null) continue;
                    String id = st.nextToken();
                    Object o = rlms.get(id);
                    if (o == null) continue;
                    currRLM.retainAll((RectListManager)o);
                }                
                else if (PRINT_PREF.equals(pref)) {
                    if (currRLM == null) continue;

                    Iterator i = currRLM.iterator();
                    ps.println("ID: " + currID + " Sz: " + currRLM.size());
                    while (i.hasNext()) {
                        ps.println("  " + i.next());
                    }
                    ps.println();
                }
            }

            ps.close();
            fos.close();
        } catch(Exception e) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            report.setErrorCode(ERROR_READING_RECTS);
            report.setDescription(new TestReport.Entry[] {
                new TestReport.Entry
                    (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                     Messages.formatMessage
                     (ERROR_READING_RECTS,
                      new String[]{""+lineNo, rects.toString(), 
                                   trace.toString()}))
                    });
            report.setPassed(false);
            return report;
        }

        boolean usingVar = false;
        boolean usingRef = false;
        InputStream refIS = null;
        try {
            refIS = var.openStream();
        } catch(Exception e) { 
            try {
                refIS = ref.openStream();
            } catch(Exception ex) {
                StringWriter trace = new StringWriter();
                e.printStackTrace(new PrintWriter(trace));
                report.setErrorCode(ERROR_CANNOT_READ_REF_URL);
                report.setDescription
                    (new TestReport.Entry[] {
                        new TestReport.Entry
                            (Messages.formatMessage
                             (ENTRY_KEY_ERROR_DESCRIPTION, null),
                             Messages.formatMessage
                             (ERROR_CANNOT_READ_REF_URL,
                              new String[]{ref.toString(), trace.toString()}))
                            });
                report.setPassed(false);
            }
        }

        int mismatch = -2;
        if (refIS != null) {
            InputStream canIS = new FileInputStream(can);
            Checker check = new Checker(canIS, refIS);
            check.start();
            mismatch = check.getMismatch();

        }

        if (mismatch == -1) {
          report.setPassed(true);
          can.delete();
          return report;
        }

        if (mismatch == -2) {
            report.setErrorCode(ERROR_NO_REFERENCE);
            report.setDescription(new TestReport.Entry[] {
                new TestReport.Entry
                    (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                     Messages.formatMessage(ERROR_NO_REFERENCE, 
                                            new String[]{ref.toString()}))
                    });
        } else {
            report.setErrorCode(ERROR_WRONG_RESULT);
            report.setDescription(new TestReport.Entry[] {
                new TestReport.Entry
                    (Messages.formatMessage(ENTRY_KEY_ERROR_DESCRIPTION, null),
                     Messages.formatMessage(ERROR_WRONG_RESULT, 
                                            new String[]{""+mismatch}))
                    });
        }
        report.setPassed(false);

        return report;
    }

    public static class Checker extends Thread {
        int mismatch = -2;
        InputStream is1, is2;
        public Checker(InputStream is1, InputStream is2) {
            this.is1 = is1;
            this.is2 = is2;
        }
        public int getMismatch() {
            while (true) {
                try {
                    this.join();
                    break;
                } catch (InterruptedException ie) { }
            }

            return mismatch;
        }
        public void run() {
            mismatch = Base64Test.compareStreams (is1, is2, true);
        }
    }
}

