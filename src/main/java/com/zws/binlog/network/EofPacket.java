package com.zws.binlog.network;

import com.zws.binlog.util.ByteUtil;
import io.netty.buffer.ByteBuf;

/**
 * Created by zhangwusheng on 17/10/3.
 */
public class EofPacket implements Packet {
    
    // In the MySQL client/server protocol, EOF and OK packets serve the same purpose,
    // to mark the end of a query execution result. Due to changes in MySQL 5.7 in the
    // OK packet (such as session state tracking), and to avoid repeating the changes
    // in the EOF packet, the EOF packet is deprecated as of MySQL 5.7.5.”
    //
    // int<1>	header	[fe] EOF header
    // if capabilities & CLIENT_PROTOCOL_41 {
    //  int<2>	warnings	number of warnings
    //  int<2>	status_flags	Status Flags
    // }
    //
    // 摘录来自: Oracle. “MySQL Internals Manual”。 iBooks.
    
    private int statusFlag=-1;
    private int numberOfWarning=-1;
    
    
    public void parse ( ByteBuf msg ) {
 
        if( msg.readableBytes () > 0){
            numberOfWarning = ByteUtil.readInteger ( msg ,2);
        }
    
        if( msg.readableBytes () > 0){
            statusFlag = ByteUtil.readInteger ( msg ,2);
        }
    
        //if capabilities & CLIENT_PROTOCOL_41
        
    }
    
    @Override
    public String toString ( ) {
        return "EofPacket{" +
                "statusFlag=" + statusFlag +
                ", numberOfWarning=" + numberOfWarning +
                '}';
    }
}
