package com.zhangwusheng.binlog.network;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangwusheng on 17/10/4.
 */
public class ResultSetRowPacket implements Packet{
    
    public void parse ( ByteBuf msg ) {
    
//        List<ResultSetRowPacket> resultSet = new LinkedList<ResultSetRowPacket> ();
//        byte[] statementResult = channel.read();
//        if (statementResult[0] == (byte) 0xFF /* error */) {
//            byte[] bytes = Arrays.copyOfRange(statementResult, 1, statementResult.length);
//            ErrorPacket errorPacket = new ErrorPacket(bytes);
//            throw new ServerException(errorPacket.getErrorMessage(), errorPacket.getErrorCode(),
//                    errorPacket.getSqlState());
//        }
//        while ((channel.read())[0] != (byte) 0xFE /* eof */) { /* skip */ }
//        for (byte[] bytes; (bytes = channel.read())[0] != (byte) 0xFE /* eof */; ) {
//            resultSet.add(new ResultSetRowPacket(bytes));
//        }
//        return resultSet.toArray(new ResultSetRowPacket[resultSet.size()]);
//
    }
}
