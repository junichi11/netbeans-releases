/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Jemmy library.
 * The Initial Developer of the Original Code is Alexandre Iline.
 * All Rights Reserved.
 * 
 * Contributor(s): Alexandre Iline.
 * 
 * $Id$ $Revision$ $Date$
 * 
 */

package org.netbeans.jemmy.util;

import java.awt.Color;

import java.awt.image.BufferedImage;

import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;

import org.netbeans.jemmy.JemmyException;

import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/** 
 * Allows to load PNG graphical file.
 * @author Alexandre Iline
 */
public class PNGDecoder extends Object {

    InputStream in;

    /** 
     * @param out output stream to read PNG image from.
     */    
    public PNGDecoder(InputStream in) {
        this.in = in;
    }

    byte read() throws IOException {
        byte b = (byte)in.read();
        return(b);
    }

    int readInt() throws IOException {
        byte b[] = read(4);
        return(((b[0]&0xff)<<24) +
               ((b[1]&0xff)<<16) +
               ((b[2]&0xff)<<8) +
               ((b[3]&0xff)));
    }

    byte[] read(int count) throws IOException {
        byte[] result = new byte[count];
        for(int i = 0; i < count; i++) {
            result[i] = read();
        }
        return(result);
    }

    boolean compare(byte[] b1, byte[] b2) {
        if(b1.length != b2.length) {
            return(false);
        }
        for(int i = 0; i < b1.length; i++) {
            if(b1[i] != b2[i]) {
                return(false);
            }
        }
        return(true);
    }

    void checkEquality(byte[] b1, byte[] b2) {
        if(!compare(b1, b2)) {
            throw(new JemmyException("Format error"));
        }
    }

    /**
     * Decodes image from an input stream passed into constructor.
     */
    public BufferedImage decode() throws IOException {

        byte[] id = read(12);
        checkEquality(id, new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13});

        byte[] ihdr = read(4);
        checkEquality(ihdr, "IHDR".getBytes());

        int width = readInt();
        int height = readInt();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        byte[] head = read(5);
        int mode;
        if(compare(head, new byte[]{1, 0, 0, 0, 0})) {
            mode = PNGEncoder.BW_MODE;
        } else if(compare(head, new byte[]{8, 0, 0, 0, 0})) {
            mode = PNGEncoder.GREYSCALE_MODE;
        } else if(compare(head, new byte[]{8, 2, 0, 0, 0})) {
            mode = PNGEncoder.COLOR_MODE;
        } else {
            throw(new JemmyException("Format error"));
        }

        readInt();//!!crc

        int size = readInt();

        byte[] idat = read(4);
        checkEquality(idat, "IDAT".getBytes());

        byte[] data = read(size);


        Inflater inflater = new Inflater();
        inflater.setInput(data, 0, size);

        int color;

        try {
            switch (mode) {
            case PNGEncoder.BW_MODE: 
                {
                    int bytes = (int)(width / 8);
                    if((width % 8) != 0) {
                        bytes++;
                    }
                    byte colorset;
                    byte[] row = new byte[bytes];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < bytes; x++) {
                            colorset = row[x];
                            for (int sh = 0; sh < 8; sh++) {
                                if(x * 8 + sh >= width) {
                                    break;
                                }
                                if((colorset & 0x80) == 0x80) {
                                    result.setRGB(x * 8 + sh, y, Color.white.getRGB());
                                } else {
                                    result.setRGB(x * 8 + sh, y, Color.black.getRGB());
                                }
                                colorset <<= 1;
                            }
                        }
                    }
                }
                break;
            case PNGEncoder.GREYSCALE_MODE: 
                {
                    byte[] row = new byte[width];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < width; x++) {
                            color = row[x];
                            result.setRGB(x, y, (color << 16) + (color << 8) + color);
                        }
                    }
                }
                break;
            case PNGEncoder.COLOR_MODE:
                {
                    byte[] row = new byte[width * 3];
                    for (int y = 0; y < height; y++) {
                        inflater.inflate(new byte[1]);
                        inflater.inflate(row);
                        for (int x = 0; x < width; x++) {
                            result.setRGB(x, y, 
                                          ((row[x * 3 + 0]&0xff) << 16) +
                                          ((row[x * 3 + 1]&0xff) << 8) +
                                          ((row[x * 3 + 2]&0xff)));
                        }
                    }
                }
            }
        } catch(DataFormatException e) {
            throw(new JemmyException("ZIP error", e));
        }

        readInt();//!!crc
        readInt();//0

        byte[] iend = read(4);
        checkEquality(iend, "IEND".getBytes());

        readInt();//!!crc
        in.close();

        return(result);
    }

    /**
     * Decodes image from file.
     */
    public static BufferedImage decode(String fileName) {
        try {
            return(new PNGDecoder(new FileInputStream(fileName)).decode());
        } catch(IOException e) {
            throw(new JemmyException("IOException during image reading", e));
        }
    }

}
