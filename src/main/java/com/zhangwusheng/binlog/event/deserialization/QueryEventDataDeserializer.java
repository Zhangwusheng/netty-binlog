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
package com.zhangwusheng.binlog.event.deserialization;

//import com.github.shyiko.mysql.binlog.event.QueryEventData;
//import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;
import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.event.data.QueryEventData;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class QueryEventDataDeserializer implements EventDataDeserializer<QueryEventData> {

//    @Override
    public QueryEventData deserialize( ByteBuf inputStream)  {
        QueryEventData eventData = new QueryEventData();
        
        eventData.setThreadId( ByteUtil.readInteger ( inputStream,4 ));
        eventData.setExecutionTime(ByteUtil.readInteger ( inputStream,4 ));
        inputStream.skipBytes (1); // length of the name of the database
        eventData.setErrorCode(ByteUtil.readInteger ( inputStream,2 ));
        inputStream.skipBytes (ByteUtil.readInteger ( inputStream,2 )); // status variables block
        eventData.setDatabase(ByteUtil.readZeroTerminatedString ( inputStream ));
//        eventData.setSql(inputStream.readString(inputStream.available()));
        eventData.setSql ( ByteUtil.readString ( inputStream,inputStream.readableBytes () -4) );
        //CheckSum
        inputStream.skipBytes(CHECKSUM_LENGTH);
        return eventData;
    }
}
