package com.zws.binlog.network;

import com.zws.ByteUtil;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangwusheng on 17/10/6.
 */
public class RowPacket implements Packet {
    
    
    private String[] values;
    
    public void parse ( ByteBuf msg ) {
         List<String> values = new LinkedList<String> ();
        while (msg.readableBytes () > 0) {
            values.add( ByteUtil.readLengthEncodedString ( msg ));
        }
        this.values = values.toArray(new String[values.size()]);
    }
    
    
    public String[] getValues() {
        return values;
    }
    
    public String getValue(int index) {
        return values[index];
    }
    
    public int size() {
        return values.length;
    }
    
    @Override
    public String toString ( ) {
        return "RowPacket{" +
                "values=" + Arrays.toString ( values ) +
                '}';
    }
}
