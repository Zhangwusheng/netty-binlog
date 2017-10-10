package com.zhangwusheng.binlog.nettyhandler;

import com.zhangwusheng.binlog.event.EventHeader;

import java.util.Arrays;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class BinlogEventData {
    
    private EventHeader header;
    private byte[] data;
    
    public EventHeader getHeader ( ) {
        return header;
    }
    
    public void setHeader ( EventHeader header ) {
        this.header = header;
    }
    
    @Override
    public String toString ( ) {
        return "BinlogEventData{" +
                "type=" + header.toString () +
//                ", data=" + Arrays.toString ( data ) +
                '}';
    }
}
