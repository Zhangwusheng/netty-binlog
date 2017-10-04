package com.zhangwusheng;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhangwusheng on 17/10/1.
 */
public class ByteUtil {
    
    public static int verify(byte value) {
        if (value < 0) {
            return (int) (256 + value);
        }
        return value;
    }
    
    /**
     * Read int written in little-endian format.throws IOException
     */
    public static int readInteger( ByteBuf byteBuf ,int length)  {
        int result = 0;
        for (int i = 0; i < length; ++i) {
            result |= (verify(byteBuf.readByte ()) << (i << 3));
        }
        return result;
    }
    
    /**
     * Read fixed length string.
     */
    public static String readString(ByteBuf byteBuf ,int start,int length) {
        byte[] result = new byte[length];
        byteBuf.getBytes ( start,result );
        return new String(result);
    }
    
    public static String readString(ByteBuf byteBuf ,int length) {
        byte[] result = new byte[length];
        byteBuf.readBytes ( result );
        return new String(result);
    }
    
    public static int readUnsignedInt(ByteBuf src, int bits) {
        int result = 0;
        for (int i = 0; i < bits; ++i) {
            result |= (src.readUnsignedByte() << (i << 3));
        }
        return result;
    }
    
    public static short readUnsignedByte(ByteBuf src) {
        return src.readUnsignedByte();
    }
    
    public static long readUnsignedLong(ByteBuf src, int bits) {
        long result = 0;
        for (int i = 0; i < bits; ++i) {
            result |= (src.readUnsignedByte() << (i << 3));
        }
        return result;
    }
    
    public static String readZeroTerminatedString(ByteBuf src) {
        
        src.markReaderIndex();// 先标记
        int length = 0;
        while ('\0' != src.readByte()) {// 可见字符
            // 一直循环
            length++;
        }
        length++;// 这个长度包含\0
        // 找到了\0 的长度为length
        src.resetReaderIndex();// 恢复
        byte[] str = new byte[length];
        src.readBytes(str);
        // 之前有个bug,
        return new String(str, 0, length - 1);
    }
    
    /**
     * Read variable-length string. Preceding packed integer indicates the length of the string.
     */
    public static String readLengthEncodedString(ByteBuf src) throws IOException {
        return readString(src,readPackedInteger(src));
    }
    
    /**
     * @see
     */
    public static int readPackedInteger(ByteBuf src) throws IOException {
        Number number = readVariableNumber(src);
        if (number == null) {
            throw new IOException("Unexpected NULL where int should have been");
        }
        if (number.longValue() > Integer.MAX_VALUE) {
            throw new IOException("Stumbled upon long even though int expected");
        }
        return number.intValue();
    }
    
    // 下面的函数主要用来写
    public static byte[] writeByte(byte value, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            // 小端模式
            result[i] = (byte) (0x000000FF & (value >>> (i << 3)));
        }
        return result;
    }
    
    public static byte[] writeInt(int value, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            // 小端模式
            result[i] = (byte) (0x000000FF & (value >>> (i << 3)));
        }
        return result;
    }
    
    public static byte[] writeLong(long value, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            // 小端模式
            result[i] = (byte) (0x000000FF & (value >>> (i << 3)));
        }
        return result;
    }
    
    public static byte[] writeString(String value) {// 最后要以\0结尾
        value += "\0";
        return value.getBytes();
    }
    
    // 借鉴了mysql-binlog的代码
    /**
     * see mysql/sql/password.c scramble(...)
     */
    
    public static int availableWithChecksumLength(ByteBuf src, int checksumLength) {
        
        return src.readableBytes() - checksumLength;
    }
    
    /**
     * Format (first-byte-based):<br/>
     * 0-250 - The first byte is the number (in the range 0-250). No additional
     * bytes are used.<br/>
     * 251 - SQL NULL value<br/>
     * 252 - Two more bytes are used. The number is in the range 251-0xffff.
     * <br/>
     * 253 - Three more bytes are used. The number is in the range
     * 0xffff-0xffffff.<br/>
     * 254 - Eight more bytes are used. The number is in the range
     * 0xffffff-0xffffffffffffffff.
     */
    public static Number readVariableNumber(ByteBuf value) {
        // 读取变长的整数
        int b = readUnsignedByte(value);
        if (b < 251) {
            return b;
        } else if (b == 251) {
            return null;
        } else if (b == 252) {
            return readUnsignedLong(value, 2);
        } else if (b == 253) {
            return readUnsignedLong(value, 3);
        } else if (b == 254) {
            return readUnsignedLong(value, 8);
        }
        return null;
    }
}
