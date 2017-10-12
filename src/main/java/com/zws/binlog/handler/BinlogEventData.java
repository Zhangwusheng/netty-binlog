package com.zws.binlog.handler;

import com.zws.binlog.event.EventHeaderV4;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class BinlogEventData {
    
    private EventHeaderV4 header;
    private byte[] data;
    
    public EventHeaderV4 getHeader ( ) {
        return header;
    }
    
    public void setHeader ( EventHeaderV4 header ) {
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
