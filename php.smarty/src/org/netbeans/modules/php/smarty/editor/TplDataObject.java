/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.php.smarty.editor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.html.lexer.HTMLTokenId;
import org.netbeans.api.lexer.Token;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.api.queries.FileEncodingQuery;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.spi.queries.FileEncodingQueryImplementation;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.loaders.SaveAsCapable;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.UserCancelException;
import org.openide.windows.TopComponent;

/**
 * Most code of this class was get from HtmlDataObject - especially encoding support.
 *
 * @author Martin Fousek <marfous@netbeans.org>
 */
@MIMEResolver.ExtensionRegistration(
    displayName="#TPLResolver",
    extension="tpl",
    mimeType="text/x-tpl",
    position=373
)
public class TplDataObject extends MultiDataObject implements CookieSet.Factory {

    private transient TplEditorSupport tplEditorSupport;

    /**
     * constants used when finding tpl (html) document content type
     */
    private static final String CHARSET_DECL = "CHARSET="; //NOI18N

    /**
     * Constant where is placed standard TPL file icon.
     */
    private static final String ICON_LOCATION = "org/netbeans/modules/php/smarty/resources/tpl-icon.png"; //NOI18N

    private static final Logger LOG = Logger.getLogger(TplDataObject.class.getName());

    public static final String DEFAULT_CHARSET_NAME = Charset.defaultCharset().name();

    public TplDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet set = getCookieSet();
        set.add(TplEditorSupport.class, this);
        set.assign(SaveAsCapable.class, new SaveAsCapable() {

            @Override
            public void saveAs( FileObject folder, String fileName ) throws IOException {
                TplEditorSupport es = getCookie( TplEditorSupport.class );
                try {
                    es.updateEncoding();
                    es.saveAs( folder, fileName );
                } catch (UserCancelException e) {
                    //ignore, just not save anything
                }
            }
        });

        set.assign(FileEncodingQueryImplementation.class, new FileEncodingQueryImpl());
    }

    @MultiViewElement.Registration(
            displayName="#CTL_SourceTabCaption",
            iconBase=ICON_LOCATION,
            persistenceType=TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID="smarty.template",
            mimeType=TplDataLoader.MIME_TYPE,
            position=1
    )
    @Messages("CTL_SourceTabCaption=&Source")
    public static MultiViewEditorElement createMultiViewEditorElement(Lookup context) {
        return new MultiViewEditorElement(context);
    }

    @Override
    protected Node createNodeDelegate() {
        DataNode dn = new DataNode(this, Children.LEAF, getLookup());
        dn.setIconBaseWithExtension(ICON_LOCATION); // NOI18N
        return dn;
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

        /** Creates new Cookie */
    public <T extends Cookie> T createCookie(Class<T> klass) {
        if (klass.isAssignableFrom(TplEditorSupport.class)) {
            return klass.cast(getTplEditorSupport());
        } else {
            return null;
        }
    }

    private synchronized TplEditorSupport getTplEditorSupport() {
        if (tplEditorSupport == null) {
            tplEditorSupport = new TplEditorSupport(this);
        }
        return tplEditorSupport;
    }

    // Package accessibility for TplEditorSupport:
    CookieSet getCookieSet0() {
        return getCookieSet();
    }

    /** Checks the file for UTF-16 marks and calls findEncoding with properly loaded document content then. */
    String getFileEncoding() {
        InputStream is = null;
        try {
            FileObject pf = getPrimaryFile();
            if(!pf.isValid()) {
                return null;
            }
            is = pf.getInputStream();
            return getFileEncoding(is);
        } catch (IOException ex) {
            LOG.log(Level.WARNING, null, ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ex) {
                LOG.log(Level.WARNING, null, ex);
            }
        }
        return null;
    }

    private String getFileEncoding(final InputStream in) throws IOException {
        //detect encoding from input stream
        String encoding = null;
            byte[] arr = new byte[4096];
        int len = in.read(arr);
            len = (len >= 0) ? len : 0;
            //check UTF-16 mark
            if (len > 1) {
                int mark = (arr[0]&0xff)*0x100+(arr[1]&0xff);
                if (mark == 0xfeff) {
                    encoding = "UTF-16"; //NOI18N
                } else if(mark == 0xfffe) {
                    encoding = "UTF-16LE"; //NOI18N
                }
            }
            //try to read the file using some encodings
            String[] encodings = new String[]{encoding != null ? encoding : DEFAULT_CHARSET_NAME, "UTF-16LE", "UTF-16BE"}; //NOI18N
            int i = 0;
            do {
                encoding = findEncoding(makeString(arr, 0, len, encodings[i++]));
            } while (encoding == null && i < encodings.length);

        if (encoding != null) {
            encoding = encoding.trim();
        }
        return encoding;
    }

    private String makeString(byte[] arr, int offset, int len, String encoding) throws UnsupportedEncodingException {
        return new String(arr, 0, len, encoding).toUpperCase();
    }


    /** Tries to guess the mime type from given input stream. Tries to find
     *   <em>&lt;meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"&gt;</em>
     * @param txt the string to search in (should be in upper case)
     * @return the encoding or null if no has been found
     */
    static String findEncoding(String txt) {
        int[] offsets = findEncodingOffsets(txt);
        if (offsets.length == 3) {
            String encoding = txt.substring(offsets[0] + offsets[1], offsets[0] + offsets[2]);
            return encoding;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static int[] findEncodingOffsets(String txt) {
        int[] rslt = new int[0];
        TokenHierarchy hi = TokenHierarchy.create(txt, HTMLTokenId.language());
        TokenSequence ts = hi.tokenSequence();
        ts.moveStart();
        while(ts.moveNext()) {
            Token token = ts.token();
            if(token.id() == HTMLTokenId.VALUE) {
                String tokenImage = token.text().toString();
                int charsetOffset = tokenImage.indexOf(CHARSET_DECL);
                charsetOffset = charsetOffset == -1 ? tokenImage.indexOf(CHARSET_DECL.toLowerCase()) : charsetOffset;

                int charsetEndOffset = charsetOffset + CHARSET_DECL.length();
                if (charsetOffset != -1){
                    int endOffset = tokenImage.indexOf('"', charsetEndOffset);

                    if (endOffset == -1){
                        endOffset = tokenImage.indexOf('\'', charsetEndOffset);
                    }

                    if (endOffset == -1){
                        endOffset = tokenImage.indexOf(';', charsetEndOffset);
                    }

                    if (endOffset == -1){
                        return rslt;
                    }

                    rslt =  new int[]{token.offset(hi), charsetEndOffset, endOffset};
                }
            }
        }
        return rslt;
    }

    private class FileEncodingQueryImpl extends FileEncodingQueryImplementation {

        private volatile Charset cachedEncoding;
        private final AtomicBoolean listeningOnContentChange = new AtomicBoolean();
        private final ThreadLocal<Boolean> callingFEQ = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return false;
            }
        };

        @Override
        public Charset getEncoding(FileObject file) {
            assert file != null;
            if(callingFEQ.get()) {
                //we are calling to the FEQ from within this method so
                //we must not return anything to prevent cycling
                return null;
            }

            Charset encoding = cachedEncoding;
            if (encoding != null) {
                LOG.log(Level.FINEST, "TplDataObject.getFileEncoding cached {0}", new Object[] {encoding});   //NOI18N
                return encoding;
            } else {
                //get the encoding from the FEQ excluding this FEQ implementation
                //so the proxy charset can default to appropriate encoding
                callingFEQ.set(true);
                try {
                    Charset charset = FileEncodingQuery.getEncoding(file);
                    return new ProxyCharset(charset);
                } finally {
                    callingFEQ.set(false);
                }
            }
        }

        private Charset cache (final Charset encoding) {

            if (!listeningOnContentChange.getAndSet(true)) {
                final FileObject primaryFile = getPrimaryFile();
                primaryFile.addFileChangeListener(FileUtil.weakFileChangeListener(new FileChangeAdapter(){
                    @Override
                    public void fileChanged(FileEvent fe) {
                        cachedEncoding = null;
                    }
                },primaryFile));
            }
            cachedEncoding = encoding;
            LOG.log(Level.FINEST, "TplDataObject.getFileEncoding noncached {0}", new Object[] {encoding});   //NOI18N
            return encoding;
        }


        private class ProxyCharset extends Charset {

            public ProxyCharset (Charset charset) {
                super (charset.name(), new String[0]);         //NOI18N
            }

            public boolean contains(Charset c) {
                return false;
            }

            public CharsetDecoder newDecoder() {
                return new HtmlDecoder (this);
            }

            public CharsetEncoder newEncoder() {
                return new HtmlEncoder (this);
            }
        }

        private class HtmlEncoder extends CharsetEncoder {

            private CharBuffer buffer = CharBuffer.allocate(4*1024);
            private CharBuffer remainder;
            private CharsetEncoder encoder;

            public HtmlEncoder (Charset cs) {
                super(cs, 1,2);
            }


            protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
                if (buffer == null) {
                    assert encoder != null;
                    if (remainder!=null) {
                        CoderResult result = encoder.encode(remainder,out,false);
                        if (!remainder.hasRemaining()) {
                            remainder = null;
                        }
                    }
                    CoderResult result = encoder.encode(in, out, false);
                    return result;
                }
               if (buffer.remaining() == 0 || (buffer.position() > 0 && in.limit() == 0)) {
                   return handleHead (in,out);
               }
               else if (buffer.remaining() < in.remaining()) {
                   int limit = in.limit();
                   in.limit(in.position()+buffer.remaining());
                   buffer.put(in);
                   in.limit(limit);
                   return handleHead (in, out);
               }
               else {
                   buffer.put(in);
                   return CoderResult.UNDERFLOW;
               }
            }

            private CoderResult handleHead (CharBuffer in, ByteBuffer out) {
                String encoding = null;
                try {
                    encoding = getEncoding ();
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                }
                if (encoding == null) {
                    buffer = null;
                    throwUnknownEncoding();
                    return null;
                }
                else {
                    Charset c;
                    try {
                        c = cache(Charset.forName(encoding));
                    } catch (UnsupportedCharsetException e) {
                        buffer = null;
                        throwUnknownEncoding();
                        return null;
                    } catch (IllegalCharsetNameException e) {
                        buffer = null;
                        throwUnknownEncoding();
                        return null;
                    }
                    encoder = c.newEncoder();
                    return flushHead(in, out);
                }
            }

            private CoderResult flushHead (CharBuffer in , ByteBuffer out) {
                buffer.flip();
                CoderResult r = encoder.encode(buffer,out, in==null);
                if (r.isOverflow()) {
                    remainder = buffer;
                    buffer = null;
                    return r;
                }
                else {
                    buffer = null;
                    if (in == null) {
                        return r;
                    }
                    return encoder.encode(in, out, false);
                }
            }

            private String getEncoding () throws IOException {
                String text = buffer.asReadOnlyBuffer().flip().toString();
                InputStream in = new ByteArrayInputStream(text.getBytes(DEFAULT_CHARSET_NAME));
                try {
                    return getFileEncoding(in);
                } finally {
                    in.close();
                }
            }

            @Override
            protected CoderResult implFlush(ByteBuffer out) {
                CoderResult res;
                if (buffer != null) {
                    res = handleHead(null, out);
                    return res;
                }
                else if (remainder != null) {
                    encoder.encode(remainder, out, true);
                }
                else {
                    CharBuffer empty = (CharBuffer) CharBuffer.allocate(0).flip();
                    encoder.encode(empty, out, true);
                }
                res = encoder.flush(out);
                return res;
            }

            @Override
            protected void implReset() {
                if (encoder != null) {
                    encoder.reset();
                }
            }
        }

        private class HtmlDecoder extends CharsetDecoder {

            private ByteBuffer buffer = ByteBuffer.allocate(4*1024);
            private ByteBuffer remainder;
            private CharsetDecoder decoder;

            public HtmlDecoder (Charset cs) {
                super (cs,1,2);
            }

            protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
                if (buffer == null) {
                    assert decoder != null;
                    if (remainder!=null) {
                        ByteBuffer tmp = ByteBuffer.allocate(remainder.remaining() + in.remaining());
                        tmp.put(remainder);
                        tmp.put(in);
                        tmp.flip();
                        CoderResult result = decoder.decode(tmp,out,false);
                        if (tmp.hasRemaining()) {
                            remainder = tmp;
                        }
                        else {
                            remainder = null;
                        }
                        return result;
                    }
                    else {
                        return decoder.decode(in, out, false);
                    }
               }
               if (buffer.remaining() == 0) {
                   return handleHead (in,out);
               }
               else if (buffer.remaining() < in.remaining()) {
                   int limit = in.limit();
                   in.limit(in.position()+buffer.remaining());
                   buffer.put(in);
                   in.limit(limit);
                   return handleHead (in, out);
               }
               else {
                   buffer.put(in);
                   return CoderResult.UNDERFLOW;
               }
            }

            private CoderResult handleHead (ByteBuffer in, CharBuffer out) {
                String encoding = null;
                try {
                    encoding = getEncoding ();
                } catch (IOException ioe) {
                    Exceptions.printStackTrace(ioe);
                }
                if (encoding == null) {
                    buffer = null;
                    throwUnknownEncoding();
                    return null;
                }
                else {
                    Charset c;
                    try {
                        c = cache(Charset.forName(encoding));
                    } catch (UnsupportedCharsetException e) {
                        buffer = null;
                        throwUnknownEncoding();
                        return null;
                    } catch (IllegalCharsetNameException e) {
                        buffer = null;
                        throwUnknownEncoding();
                        return null;
                    }
                    decoder = c.newDecoder();
                    return flushHead(in, out);
                }
            }

            private CoderResult flushHead (ByteBuffer in , CharBuffer out) {
                buffer.flip();
                CoderResult r = decoder.decode(buffer,out, in==null);
                if (r.isOverflow()) {
                    remainder = buffer;
                    buffer = null;
                    return r;
                }
                else {
                    buffer = null;
                    if (in == null) {
                        return r;
                    }
                    return decoder.decode(in, out, false);
                }
            }

            private String getEncoding () throws IOException {
                byte[] arr = buffer.array();
                ByteArrayInputStream in = new ByteArrayInputStream (arr);
                try {
                    return getFileEncoding(in);
                }
                finally {
                    in.close();
                }
            }

            @Override
            protected CoderResult implFlush(CharBuffer out) {
                CoderResult res;
                if (buffer != null) {
                    res = handleHead(null, out);
                    return res;
                }
                else if (remainder != null) {
                    decoder.decode(remainder, out, true);
                }
                else {
                    ByteBuffer empty = (ByteBuffer) ByteBuffer.allocate(0).flip();
                    decoder.decode(empty, out, true);
                }
                res = decoder.flush(out);
                return res;
            }

            @Override
            protected void implReset() {
                if (decoder != null) {
                    decoder.reset();
                }
            }
        }
    }
}
