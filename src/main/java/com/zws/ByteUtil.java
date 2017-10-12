package com.zws;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import org.slf4j.Logger;

import java.util.BitSet;

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
    public static String readLengthEncodedString(ByteBuf src)  {
        return readString(src,readPackedInteger(src));
    }
    
    /**
     * @see
     */
    public static int readPackedInteger(ByteBuf src)  {
        Number number = readVariableNumber(src);
//        if (number == null) {
//            throw new IOException("Unexpected NULL where int should have been");
//        }
//        if (number.longValue() > Integer.MAX_VALUE) {
//            throw new IOException("Stumbled upon long even though int expected");
//        }
        return number.intValue();
    }

    public static int bigEndianInteger(byte[] bytes, int offset, int length) {
        int result = 0;
        for (int i = offset; i < (offset + length); i++) {
            byte b = bytes[i];
            result = (result << 8) | (b >= 0 ? (int) b : (b + 256));
        }
        return result;
    }

    public static byte[] readSpecifiedLengthBytes(ByteBuf src, int length) {

        byte[] str = new byte[length];
        src.readBytes(str);
        return str;
    }

    public static BitSet readBitSet(ByteBuf buf, int length, boolean bigEndian)  {
        // according to MySQL internals the amount of storage required for N
        // columns is INT((N+7)/8) bytes
        byte[] bytes = readSpecifiedLengthBytes(buf, (length + 7) >> 3);
        bytes = bigEndian ? bytes : reverse(bytes);
        BitSet result = new BitSet();
        for (int i = 0; i < length; i++) {
            if ((bytes[i >> 3] & (1 << (i % 8))) != 0) {
                result.set(i);
            }
        }
        return result;
    }


    private static byte[] reverse(byte[] bytes) {
        for (int i = 0, length = bytes.length >> 1; i < length; i++) {
            int j = bytes.length - 1 - i;
            byte t = bytes[i];
            bytes[i] = bytes[j];
            bytes[j] = t;
        }
        return bytes;
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
    
    public static void prettyPrint(ByteBuf buf, Logger log){
        String s = ByteBufUtil.prettyHexDump ( buf );
        log.info ("\n"+ s );
    }
}
