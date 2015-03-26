package com.processing.networking;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * User: YAPI
 * Date: 3/26/15
 * Time: 1:24 PM
 */

/**
 * Converter - class for conventing String to bytes for sending over network, and convention bytes to String.
 * result byte array can be with different length then String.
 * Convention String to bytes contains next steps:
 * 1) convent String to array of bytes according default charSet
 * 2) adding 10 bytes before result array - with size of this array
 * <p/>
 * Convention bytes to String contains next steps:
 * 1) read 10 bytes in the begin of array, prepare size, cut them
 * 2) process bytes according size from "10 bytes", converting to string with default charset
 */
public class Converter {
    public static final Charset defaultCharset = StandardCharsets.UTF_8;
    public static final int lengthOfSizePrefix = 10;

    private Converter() {
    }

    public static byte[] writeString(String message) {
        ByteBuffer byteBuffer = defaultCharset.encode(message);
        byte[] bytes = new byte[byteBuffer.limit()];
        byteBuffer.get(bytes,0,bytes.length);
        byte[] mess = new byte[bytes.length + 10];
        byte[] size = intToBytes(bytes.length);
        for (int i = 0; i < 10; i++) {
            mess[i] = size[i];
        }
        for (int i = 10; i < bytes.length + 10; i++) {
            mess[i] = bytes[i-10];
        }
        return mess;
    }

    public static String readString(byte[] messageWithSizePrefix) {
        byte[] size = new byte[10];
        for (int i = 0; i < 10; i++) {
            size[i] = messageWithSizePrefix[i];
        }
        int messageSize =bytesToInteger(size);
        byte[] message = Arrays.copyOfRange(messageWithSizePrefix, 10, 10 +(int)messageSize);
        ByteBuffer byteBuffer=ByteBuffer.allocate(messageSize);
        byteBuffer.put(message);
        byteBuffer.flip();
        CharBuffer charBuffer=defaultCharset.decode(byteBuffer);
        return charBuffer.toString();
    }

    public static byte[] intToBytes(int x) {
        ByteBuffer buffer = ByteBuffer.allocate(lengthOfSizePrefix);
        buffer.putInt(x);
        return buffer.array();
    }

    public static int bytesToInteger(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(lengthOfSizePrefix);
        buffer.put(bytes, 0, lengthOfSizePrefix);
        buffer.flip();//need flip
        return buffer.getInt();
    }
}
