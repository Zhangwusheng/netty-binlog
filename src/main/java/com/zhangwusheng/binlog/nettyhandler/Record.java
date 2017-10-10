package com.zhangwusheng.binlog.nettyhandler;

import com.zhangwusheng.binlog.network.ColumnDefinitionPacket;
import com.zhangwusheng.binlog.network.RowPacket;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class Record {
    
    private int columnCount;
    private ColumnDefinitionPacket[] columnDefinitionPackets;
    private RowPacket[] rowPackets;
    
    public Record( int columnCount, ColumnDefinitionPacket[] columns, RowPacket[] rows){
        this.columnCount = columnCount;
        this.columnDefinitionPackets = columns;
        this.rowPackets = rows;
    }
    
    public int getColumnCount ( ) {
        return columnCount;
    }
    
    public ColumnDefinitionPacket[] getColumnDefinitionPackets ( ) {
        return columnDefinitionPackets;
    }
    
    public RowPacket[] getRowPackets ( ) {
        return rowPackets;
    }
}
