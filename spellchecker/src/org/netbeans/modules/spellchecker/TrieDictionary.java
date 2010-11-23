/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
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
package org.netbeans.modules.spellchecker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.netbeans.modules.spellchecker.spi.dictionary.Dictionary;
import org.netbeans.modules.spellchecker.spi.dictionary.ValidityType;
import org.openide.util.CharSequences;

/**
 *
 * @author Jan Lahoda
 */
public class TrieDictionary implements Dictionary {

    private byte[] array;
    private ByteBuffer buffer;

    TrieDictionary(byte[] array) {
        this.array = array;
        this.buffer = null;
    }

    private TrieDictionary(File data) throws IOException {
        this.array = null;

        FileInputStream ins = new FileInputStream(data);
        FileChannel channel = ins.getChannel();
        
        try {
            this.buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
        } finally {
            channel.close();
            ins.close();
        }
    }

    public ValidityType validateWord(CharSequence word) {
        String wordString = word.toString();
        ValidityType type = validateWordImpl(wordString.toLowerCase());

        if (type != ValidityType.VALID) {
            ValidityType curr = validateWordImpl(wordString);

            if (type == ValidityType.PREFIX_OF_VALID) {
                if (curr == ValidityType.VALID) {
                    type = curr;
                }
            } else {
                type = curr;
            }
        }

        return type;
    }

    private ValidityType validateWordImpl(CharSequence word) {
        int node = findNode(word, 0, 4);

        if (node == (-1))
            return ValidityType.INVALID;

        if (readByte(node) == 0x01) {
            return ValidityType.VALID;
        }

        return ValidityType.PREFIX_OF_VALID;
    }

    public List<String> findValidWordsForPrefix(CharSequence word) {
        List<String> result = new ArrayList<String>();
        int node = findNode(word, 0, 4);
        
        if (node == (-1))
            return Collections.emptyList();
        
        return findValidWordsForPrefix(new StringBuffer(word), node, result);
    }

    public List<String> findProposals(CharSequence pattern) {
        List<String> result = new ArrayList<String>();
        
        return findProposals(pattern, 2, 4, new StringBuffer(), result);
    }
    
    private List<String> findProposals(CharSequence pattern, int maxDistance, int node, StringBuffer word, List<String> result) {
        int entries = readInt(node + 1);
        
        for (int currentEntry = 0; currentEntry < entries; currentEntry++) {
            char ac = readChar(node + 5 + currentEntry * 6);
            
            word.append(ac);
            
            int distance = distance(pattern, word);
            int targetNode = node + readInt(node + 5 + currentEntry * 6 + 2);
            
            if (distance < maxDistance) {
                if (readByte(targetNode) == 0x01) {
                    result.add(word.toString());
                }
            }
                
            if ((distance - (pattern.length() - word.length())) < maxDistance) {
                findProposals(pattern, maxDistance, targetNode, word, result);
            }
            
            word.deleteCharAt(word.length() - 1);
        }
        
        return result;
    }

    private List<String> findValidWordsForPrefix(StringBuffer foundSoFar, int node, List<String> result) {
        int entries = readInt(node + 1);
        
        for (int currentEntry = 0; currentEntry < entries; currentEntry++) {
            char ac = readChar(node + 5 + currentEntry * 6);
            
            foundSoFar.append(ac);
            
            int targetNode = node + readInt(node + 5 + currentEntry * 6 + 2);
            
            if (readByte(targetNode) == 0x01) {
                result.add(foundSoFar.toString());
            }
                
            findValidWordsForPrefix(foundSoFar, targetNode, result);
            
            foundSoFar.deleteCharAt(foundSoFar.length() - 1);
        }
        
        return result;
    }
    
    private int findNode(CharSequence word, int currentCharOffset, int currentNode) {
        if (word.length() <= currentCharOffset)
            return currentNode;

        char c = word.charAt(currentCharOffset);
        int entries = readInt(currentNode + 1);

        for (int currentEntry = 0; currentEntry < entries; currentEntry++) {
            char ac = readChar(currentNode + 5 + currentEntry * 6);

            if (ac == c) {
                int newNodeOffset = readInt(currentNode + 5 + currentEntry * 6 + 2);

                int newNode = currentNode + newNodeOffset;

                return findNode(word, currentCharOffset + 1, newNode);
            }
        }

        return -1;
    }

    private static final int CURRENT_TRIE_DICTIONARY_VERSION = 1;

    private static final File CACHE_DIR = new File(new File(new File(System.getProperty("netbeans.user"), "var"), "cache"), "dict");

    public static Dictionary getDictionary(String suffix, List<URL> sources) throws IOException {
        File trie = new File(CACHE_DIR, "dictionary" + suffix + ".trie" + CURRENT_TRIE_DICTIONARY_VERSION);
        
        return getDictionary(trie, sources);
    }

    static Dictionary getDictionary(File trie, List<URL> sources) throws IOException {
        if (!trie.exists()) {
            trie.getParentFile().mkdirs();

            boolean done = false;

            try {
                ByteArray array = new ByteArray(trie);

                constructTrie(array, sources);
                array.close();
                done = true;
            } finally {
                if (!done) {
                    trie.delete();
                }
            }
        }

        return new TrieDictionary(trie);
    }

    private static int toUnsigned(byte b) {
        if (b < 0) {
            return 256 + b;
        }

        return b;
    }

    private int readInt(int pos) {
        return (toUnsigned(readByte(pos + 0)) << 24) + (toUnsigned(readByte(pos + 1)) << 16) + (toUnsigned(readByte(pos + 2)) << 8) + toUnsigned(readByte(pos + 3));
    }

    private char readChar(int pos) {
        return (char) ((toUnsigned(readByte(pos + 0)) << 8) + toUnsigned(readByte(pos + 1)));
    }

    private byte readByte(int pos) {
        if (buffer != null) {
            return buffer.get(pos);
        } else {
            return array[pos];
        }
    }

    private static boolean compareChars(char c1, char c2) {
        return c1 == c2 || Character.toLowerCase(c1) == Character.toLowerCase(c2);
    }
    
    private static int distance(CharSequence pattern, CharSequence word) {
        int[] old = new int[pattern.length() + 1];
        int[] current = new int[pattern.length() + 1];
        int[] oldLength = new int[pattern.length() + 1];
        int[] length = new int[pattern.length() + 1];
        
        for (int cntr = 0; cntr < old.length; cntr++) {
            old[cntr] = pattern.length() + 1;//cntr;
            oldLength[cntr] = (-1);
        }
        
        current[0] = old[0] = oldLength[0] = length[0] = 0;
        
        int currentIndex = 0;
        
        while (currentIndex < word.length()) {
            for (int cntr = 0; cntr < pattern.length(); cntr++) {
                int insert = old[cntr + 1] + 1;
                int delete = current[cntr] + 1;
                int replace = old[cntr] + (compareChars(pattern.charAt(cntr), word.charAt(currentIndex)) ? 0 : 1);
                
                if (insert < delete) {
                    if (insert < replace) {
                        current[cntr + 1] = insert;
                        length[cntr + 1] = oldLength[cntr + 1] + 1;
                    } else {
                        current[cntr + 1] = replace;
                        length[cntr + 1] = oldLength[cntr] + 1;
                    }
                } else {
                    if (delete < replace) {
                        current[cntr + 1] = delete;
                        length[cntr + 1] = length[cntr];
                    } else {
                        current[cntr + 1] = replace;
                        length[cntr + 1] = oldLength[cntr] + 1;
                    }
                }
            }
            
            currentIndex++;
            
            int[] temp = old;
            
            old = current;
            current = temp;
            
            temp = oldLength;
            
            oldLength = length;
            length = temp;
        }
        
        return old[pattern.length()];
    }
    
    private static void constructTrie(ByteArray array, List<URL> sources) throws IOException {
        SortedSet<CharSequence> data = new TreeSet<CharSequence>();

        for (URL u : sources) {

            BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream(), "UTF-8"));
            
            try {
                String line;
                
                while ((line = in.readLine()) != null) {
                    data.add(CharSequences.create(line));
                }
            } finally {
                //TODO: wrap in try - catch:
                in.close();
            }
        }
        
        constructTrieData(array, data);
    }
    
    private static void constructTrieData(ByteArray array, SortedSet<? extends CharSequence> data) throws IOException {
        array.put(0, CURRENT_TRIE_DICTIONARY_VERSION);
        encodeOneLayer(array, 4, 0, data);
    }

    private static int encodeOneLayer(ByteArray array, int currentPointer, int currentChar, SortedSet<? extends CharSequence> data) throws IOException {
        Map<Character, SortedSet<CharSequence>> char2Words = new TreeMap<Character, SortedSet<CharSequence>>();
        boolean representsFullWord = !data.isEmpty() && data.first().length() <= currentChar;
        Iterator<? extends CharSequence> dataIt = data.iterator();

        if (representsFullWord) {
            dataIt.next();
        }

        while (dataIt.hasNext()) {
            CharSequence word = dataIt.next();
            char c = word.charAt(currentChar);
            SortedSet<CharSequence> words = char2Words.get(c);

            if (words == null) {
                char2Words.put(c, words = new TreeSet<CharSequence>());
            }

            words.add(word);
        }

        int entries = char2Words.size();

        //write flags:
        byte flags = 0x00;

        if (representsFullWord) {
            flags = 0x01;
        }

        array.put(currentPointer, flags);
        array.put(currentPointer + 1, entries);

        int currentEntry = 0;
        int childPointer = currentPointer + 5 + entries * 6;

        for (Entry<Character, SortedSet<CharSequence>> e : char2Words.entrySet()) {
            array.put(currentPointer + 5 + currentEntry * 6, e.getKey());
            array.put(currentPointer + 5 + currentEntry * 6 + 2, childPointer - currentPointer);

            childPointer = encodeOneLayer(array, childPointer, currentChar + 1, e.getValue());

            currentEntry++;
        }

        return childPointer;
    }

    private static class ByteArray {

        private final RandomAccessFile out;

        public ByteArray(File out) throws FileNotFoundException {
            this.out = new RandomAccessFile(out, "rw");
        }

        public void put(int pos, char what) throws IOException {
            out.seek(pos);
            out.writeChar(what);
        }

        public void put(int pos, byte what) throws IOException {
            out.seek(pos);
            out.writeByte(what);
        }

        public void put(int pos, int what) throws IOException {
            out.seek(pos);
            out.writeInt(what);
        }

        public void close() throws IOException {
            out.close();
        }
    }
}
