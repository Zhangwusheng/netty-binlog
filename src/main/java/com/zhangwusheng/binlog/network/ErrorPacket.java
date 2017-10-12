/*
 * Copyright 2013 Stanley Shyiko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhangwusheng.binlog.network;

import com.zhangwusheng.ByteUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 *
 * This packet signals that an error occurred. It contains a SQL state value if CLIENT_PROTOCOL_41 is enabled.
 * Payload
 *  Type	Name	Description
 *  int<1>	header	[ff] header of the ERR packet
 *  int<2>	error_code	error-code
 * if capabilities & CLIENT_PROTOCOL_41 {
 *  string[1]	sql_state_marker	# marker of the SQL State
 *  string[5]	sql_state	SQL State
 *  }
 *  string<EOF>	error_message	human readable error message
 *  Example
 *  17 00 00 01 ff 48 04 23    48 59 30 30 30 4e 6f 20
 *  74 61 62 6c 65 73 20 75    73 65 64
 *  .....H.#HY000Notables used”
 *  摘录来自: Oracle. “MySQL Internals Manual”。 iBooks.
 */
public class ErrorPacket implements Packet {

    private int errorCode;
    private String sqlState;
    private String errorMessage;

    public ErrorPacket ()  {
        /**
         * see mysql-5.7.17/libmysql/test_trace_plugin.cc
         569     if (ERR_PACKET(args.pkt))
         570     {
         571       const byte *pkt= args.pkt;
         572       unsigned int err_code= uint2korr(pkt+1);
         573       pkt+= 3;
         574
         575       if ('#' == *pkt)
         576       {
         577         LOG(("Server error %d (%.5s): %.*s",
         578              err_code, pkt+1, args.pkt_len - 9, pkt+6));
         579       }
         580       else
         581       {
         582         LOG(("Server error %d: %.*s",
         583              err_code, args.pkt_len - 3, pkt));
         584       }
         585     }
         *
         * */
    }
    
    
    public void parse ( ByteBuf byteBuf ) {
        //前面已经把0xFF读取走了
        this.errorCode = ByteUtil.readInteger ( byteBuf,2 );
        //第三个字节是#
        byte indicator = byteBuf.getByte ( 3 );
        if (indicator == '#') {
            byteBuf.skipBytes ( 1 );
            this.sqlState = ByteUtil.readString ( byteBuf,5 );
        }
        //把剩下的读出来
        this.errorMessage = ByteUtil.readString ( byteBuf,byteBuf.readableBytes () );
    
    }
    
    public int getErrorCode() {
        return errorCode;
    }

    public String getSqlState() {
        return sqlState;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
