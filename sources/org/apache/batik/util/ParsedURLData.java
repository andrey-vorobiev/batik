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

package org.apache.batik.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Holds the data for more URL's
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$ 
 */
public class ParsedURLData {
    
    String HTTP_USER_AGENT_HEADER      = "User-Agent";

    String HTTP_ACCEPT_HEADER          = "Accept";
    String HTTP_ACCEPT_LANGUAGE_HEADER = "Accept-Language";
    String HTTP_ACCEPT_ENCODING_HEADER = "Accept-Encoding";

    protected static List acceptedEncodings = new LinkedList();
    static {
        acceptedEncodings.add("gzip");
    }

    /**
     * GZIP header magic number bytes, like found in a gzipped
     * files, which are encoded in Intel format (ie. little indian).
     */
    public final static byte GZIP_MAGIC[] = {(byte)0x1f, (byte)0x8b};

    /**
     * This is a utility function others can call that checks if
     * is is a GZIP stream if so it returns a GZIPInputStream that
     * will decode the contents, otherwise it returns (or a
     * buffered version of is) untouched.
     * @param is Stream that may potentially be a GZIP stream.
     */
    public static InputStream checkGZIP(InputStream is) 
        throws IOException {

            if (!is.markSupported())
                is = new BufferedInputStream(is);
            byte data[] = new byte[2];
            try {
                is.mark(2);
                is.read(data);
                is.reset();
            } catch (Throwable t) {
                is.reset();
                return is;
            }
        if ((data[0] == GZIP_MAGIC[0]) &&
            (data[1] == GZIP_MAGIC[1]))
            return new GZIPInputStream(is);
        return is;
    }

    /**
     * Since the Data instance is 'hidden' in the ParsedURL
     * instance we make all our methods public.  This makes it
     * easy for the various Protocol Handlers to update an
     * instance as parsing proceeds.
     */
    public String protocol        = null;
    public String host            = null;
    public int    port            = -1;
    public String path            = null;
    public String ref             = null;
    public String contentType     = null;
    public String contentEncoding = null;

    public InputStream stream     = null;
    public boolean     hasBeenOpened  = false;

    /**
     * Void constructor
     */
    public ParsedURLData() {
    }

    /**
     * Build from an existing URL.
     */
    public ParsedURLData(URL url) {
        protocol = url.getProtocol();
        if ((protocol != null) && (protocol.length() == 0)) 
            protocol = null;

        host = url.getHost();
        if ((host != null) && (host.length() == 0)) 
            host = null;

        port     = url.getPort();

        path     = url.getFile();
        if ((path != null) && (path.length() == 0)) 
            path = null;

        ref      = url.getRef();
        if ((ref != null) && (ref.length() == 0))  
            ref = null;
    }

    /**
     * Attempts to build a normal java.net.URL instance from this
     * URL.
     */
    protected URL buildURL() throws MalformedURLException {

        // System.out.println("File: " + file);
        // if (ref != null)
        //     file += "#" + ref;
        // System.err.println("Building: " + protocol + " - " + 
        //                     host + " - " + path);

        if ((protocol != null) && (host != null)) {
            String file = "";
            if (path != null) 
                file = path;
            if (port == -1)
                return new URL(protocol, host, file);

            return new URL(protocol, host, port, file);
        }

        // System.err.println("toString: " + toString());
        return new URL(toString());
    }

    /**
     * Implement Object.hashCode.
     */
    public int hashCode() {
        int hc = port;
        if (protocol != null) 
            hc ^= protocol.hashCode();
        if (host != null)
            hc ^= host.hashCode();

        // For some URLS path and ref can get fairly long
        // and the most unique part is towards the end
        // so we grab that part for HC purposes
        if (path != null) {
            int len = path.length();
            if (len > 20)
                hc ^= path.substring(len-20).hashCode();
            else
                hc ^= path.hashCode();
        }
        if (ref != null) {
            int len = ref.length();
            if (len > 20)
                hc ^= ref.substring(len-20).hashCode();
            else
                hc ^= ref.hashCode();
        }

        return hc;
    }

    /**
     * Implement Object.equals for ParsedURLData.
     */
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (! (obj instanceof ParsedURLData)) 
            return false;

        ParsedURLData ud = (ParsedURLData)obj;
        if (ud.port != port)
            return false;
            
        if (ud.protocol==null) {
            if (protocol != null)
                return false;
        } else if (protocol == null)
            return false;
        else if (!ud.protocol.equals(protocol))
            return false;

        if (ud.host==null) {
            if (host   !=null)
                return false;
        } else if (host == null)
            return false;
        else if (!ud.host.equals(host))
            return false;

        if (ud.ref==null) {
            if (ref   !=null)
                return false;
        } else if (ref == null)
            return false;
        else if (!ud.ref.equals(ref))
            return false;

        if (ud.path==null) {
            if (path   !=null)
                return false;
        } else if (path == null)
            return false;
        else if (!ud.path.equals(path))
            return false;

        return true;
    }

    /**
     * Returns the content type if available.  This is only available
     * for some protocols.
     */
    public String getContentType(String userAgent) {
        if (contentType != null)
            return contentType;

        if (!hasBeenOpened) {
            try {
                openStreamInternal(userAgent, null,  null);
            } catch (IOException ioe) { /* nothing */ }
        }

        return contentType;
    }

    /**
     * Returns the content encoding if available.  This is only available
     * for some protocols.
     */
    public String getContentEncoding(String userAgent) {
        if (contentEncoding != null)
            return contentEncoding;

        if (!hasBeenOpened) {
            try {
                openStreamInternal(userAgent, null,  null);
            } catch (IOException ioe) { /* nothing */ }
        }

        return contentEncoding;
    }

    /**
     * Returns true if the URL looks well formed and complete.
     * This does not garuntee that the stream can be opened but
     * is a good indication that things aren't totally messed up.
     */
    public boolean complete() {
        try {
            URL url = buildURL();
        } catch (MalformedURLException mue) {
            return false;
        }
        return true;
    }

    /**
     * Open the stream and check for common compression types.  If
     * the stream is found to be compressed with a standard
     * compression type it is automatically decompressed.
     * @param userAgent The user agent opening the stream (may be null).
     * @param mimeTypes The expected mime types of the content 
     *        in the returned InputStream (mapped to Http accept
     *        header among other possability).  The elements of
     *        the iterator must be strings (may be null)
     */
    public InputStream openStream(String userAgent, Iterator mimeTypes) 
        throws IOException {
        InputStream raw = openStreamInternal(userAgent, mimeTypes, 
                                             acceptedEncodings.iterator());
        if (raw == null)
            return null;
        stream = null;
                
        return checkGZIP(raw);
    }

    /**
     * Open the stream and returns it.  No checks are made to see
     * if the stream is compressed or encoded in any way.
     * @param userAgent The user agent opening the stream (may be null).
     * @param mimeTypes The expected mime types of the content 
     *        in the returned InputStream (mapped to Http accept
     *        header among other possability).  The elements of
     *        the iterator must be strings (may be null)
     */
    public InputStream openStreamRaw(String userAgent, Iterator mimeTypes) 
        throws IOException {
        
        InputStream ret = openStreamInternal(userAgent, mimeTypes, null);
        stream = null;
        return ret;
    }

    protected InputStream openStreamInternal(String userAgent,
                                             Iterator mimeTypes,
                                             Iterator encodingTypes) 
        throws IOException {
        if (stream != null)
            return stream;
        
        hasBeenOpened = true;

        URL url = null;
        try {
            url = buildURL();
        } catch (MalformedURLException mue) {
            throw new IOException
                ("Unable to make sense of URL for connection");
        }

        if (url == null)
            return null;

        URLConnection urlC = url.openConnection();
        if (urlC instanceof HttpURLConnection) {
            if (userAgent != null)
                urlC.setRequestProperty(HTTP_USER_AGENT_HEADER, userAgent);

            if (mimeTypes != null) {
                String acceptHeader = "";
                while (mimeTypes.hasNext()) {
                    acceptHeader += mimeTypes.next();
                    if (mimeTypes.hasNext())
                        acceptHeader += ",";
                }
                urlC.setRequestProperty(HTTP_ACCEPT_HEADER, acceptHeader);
            }

            if (encodingTypes != null) {
                String encodingHeader = "";
                while (encodingTypes.hasNext()) {
                    encodingHeader += encodingTypes.next();
                    if (encodingTypes.hasNext())
                        encodingHeader += ",";
                }
                urlC.setRequestProperty(HTTP_ACCEPT_ENCODING_HEADER, 
                                        encodingHeader);
            }

            contentType     = urlC.getContentType();
            contentEncoding = urlC.getContentEncoding();
        }

        return (stream = urlC.getInputStream());
    }

    /**
     * Returns the URL up to and include the port number on
     * the host.  Does not include the path or fragment pieces.
     */
    public String getPortStr() {
        String portStr ="";
        if (protocol != null)
            portStr += protocol + ":";

        if ((host != null) || (port != -1)) {
            portStr += "//";
            if (host != null) portStr += host;
            if (port != -1)   portStr += ":" + port;
        }

        return portStr;
    }

    protected boolean sameFile(ParsedURLData other) {
        if (this == other) return true;

        // Check if the rest of the two PURLs matche other than
        // the 'ref'
        if ((port      == other.port) &&
            ((path     == other.path) 
             || ((path!=null) && path.equals(other.path))) &&
            ((host     == other.host) 
             || ((host!=null) && host.equals(other.host))) &&
            ((protocol == other.protocol) 
             || ((protocol!=null) && protocol.equals(other.protocol))))
            return true;

        return false;
    }


    /**
     * Return a string representation of the data.
     */
    public String toString() {
        String ret = getPortStr();
        if (path != null)
            ret += path;

        if (ref != null) 
            ret += "#" + ref;

        return ret;
    }
}

