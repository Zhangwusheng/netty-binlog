package com.zws.binlog.network;

import com.zws.binlog.util.ByteUtil;
import io.netty.buffer.ByteBuf;

/**
 * Created by zhangwusheng on 17/10/3.
 */
public class OKPacket implements Packet {
    
    private int resultCode=-1;
    private int affectedRows=-1;
    private int lastInsertId=-1;
    private int statusFlag=-1;
    private int numberOfWarning=-1;
    
    //14.1.3.1 OK_Packet
    //这里实际上是个OK_Packet
    //07 00 00 02 00 00 00 02 00 00 00
    //头三个字节是长度，第四个字节是序号;后面7个字节第一个自己是00表示OK
    
    //“OK with CLIENT_PROTOCOL_41. 0 affected rows, last-insert-id was 0,
    // AUTOCOMMIT enabled, 0 warnings. No further info.”
    //摘录来自: Oracle. “MySQL Internals Manual”。 iBooks.
    
    //    “These rules distinguish whether the packet represents OK or EOF:
    //
    //    OK: header = 0 and length of packet > 7
    //
    //    EOF: header = 0xfe and length of packet < 9
    //
    //    摘录来自: Oracle. “MySQL Internals Manual”。 iBooks.
    
    
    public void parse ( ByteBuf msg ) {
        resultCode = ByteUtil.readInteger ( msg,1 );
        
        if( msg.readableBytes () > 0){
            affectedRows = ByteUtil.readVariableNumber ( msg ).intValue ();
        }
    
        if( msg.readableBytes () > 0){
            lastInsertId = ByteUtil.readVariableNumber ( msg ).intValue ();
        }
        if( msg.readableBytes () > 0){
            statusFlag = ByteUtil.readInteger ( msg ,2);
        }
    
        if( msg.readableBytes () > 0){
            numberOfWarning = ByteUtil.readInteger ( msg ,2);
        }
        //if capabilities & CLIENT_PROTOCOL_41
        
    }
    
    @Override
    public String toString ( ) {
        return "OKPacket{" +
                "resultCode=" + resultCode +
                ", affectedRows=" + affectedRows +
                ", lastInsertId=" + lastInsertId +
                ", statusFlag=" + statusFlag +
                ", numberOfWarning=" + numberOfWarning +
                "}";
    }
}
