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
package com.zws.binlog.network;

import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface Packet {

    // https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_basic_packets.html#sect_protocol_basic_packets_sending_mt_16mb
    
    /**If the payload is larger than or equal to 224−1 bytes the length is set to 2^24−1 (ff ff ff)
     * and a additional packets are sent with the rest of the payload until the payload of a packet
     * is less than 224−1 bytes.Sending a payload of 16 777 215 (224−1) bytes looks like:
     * ff ff ff 00 ...
     * 00 00 00 01
     * 摘录来自: Oracle. “MySQL Internals Manual”。 iBooks.
     */
    
    int MAX_LENGTH = 16777215;
    
    void parse( ByteBuf msg);
}
