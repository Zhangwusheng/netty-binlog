package com.zws.binlog.network;

import com.zws.binlog.define.CharacterSets;
import com.zws.binlog.util.ByteUtil;
import io.netty.buffer.ByteBuf;

/**
 * Created by zhangwusheng on 17/10/6.
 */
public class ColumnDefinitionPacket implements Packet {

    //可以用wireshark，对包进行解析。wireshark支持mysql协议
    //    Catalog: def
    //    Database: information_schema
    //    Table: tables
    //    Original table: TABLES
    //    Name: TABLE_CATALOG
    //    Original name: TABLE_CATALOG
    //    Charset number: utf8 COLLATE utf8_general_ci (33)
    //    Length: 1536
    //    Type: FIELD_TYPE_VAR_STRING (253)
    //    Flags: 0x0001
    //    Decimals: 0

    private String catalog;
    private String database;
    private String table;
    private String originalTable;
    private String name;
    private String originalName;
    private byte nextLength;//Always 0x0c

    //SELECT id, collation_name FROM information_schema.collations ORDER BY id;
    //这里是ID，可以从上面语句找到对应关系，变成字符串
    private int characterSet;// 2 bytes;

    private int maxColumnLength;//4
    private int columnType;//1
    private int flags;
    private int decimals;


    private boolean isEof = false;
    //    decimals (1) -- max shown decimal digits
    //    0x00 for integers and static strings
    //    0x1f for dynamic strings, double, float
    //    0x00 to 0x51 for decimals”
    //    decimals and column_length can be used for text-output formatting
    //    摘录来自: Oracle. “MySQL Internals Manual”。 iBooks.

    public void parse ( ByteBuf msg ) {
        msg.markReaderIndex ();
        if( msg.readByte ( ) == (byte)0xFE ){
            isEof = true;
            msg.skipBytes ( 4 );
            //这里一共是5个字节，已经读取了一个FE，跳过4个
            return;
        }else{
            msg.resetReaderIndex ();
        }

        catalog = ByteUtil.readLengthEncodedString ( msg );
        database = ByteUtil.readLengthEncodedString ( msg );
        table = ByteUtil.readLengthEncodedString ( msg );
        originalTable = ByteUtil.readLengthEncodedString ( msg );
        name = ByteUtil.readLengthEncodedString ( msg );
        originalName = ByteUtil.readLengthEncodedString ( msg );
        msg.skipBytes ( 1 );
        nextLength = 0x0c;

        characterSet = ByteUtil.readInteger ( msg,2 );
        maxColumnLength = ByteUtil.readInteger ( msg,4 );
        columnType = ByteUtil.readInteger ( msg,1 );
        flags = ByteUtil.readInteger ( msg,2 );
        decimals = ByteUtil.readInteger ( msg,1 );
        msg.skipBytes ( 2 );//Filler 00 00
    }

    @Override
    public String toString ( ) {
        if ( isEof ) {
            return "ColumnDefinitionPacket{EOF}";
        } else {
            return "ColumnDefinitionPacket{" +
                    "catalog='" + catalog + '\'' +
                    ", database='" + database + '\'' +
                    ", table='" + table + '\'' +
                    ", originalTable='" + originalTable + '\'' +
                    ", name='" + name + '\'' +
                    ", originalName='" + originalName + '\'' +
                    ", nextLength=" + nextLength +
                    ", characterSet=" + CharacterSets.getCharacterSetStringById ( characterSet ) +
                    ", maxColumnLength=" + maxColumnLength +
                    ", columnType=" + columnType +
                    ", flags=" + flags +
                    ", decimals=" + decimals +
                    '}';
        }
    }
}
