/*
 * Copyright 2015 Stanley Shyiko
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

//import com.github.shyiko.mysql.binlog.event.IntVarEventData;
//import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;
import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.event.data.IntVarEventData;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class IntVarEventDataDeserializer implements EventDataDeserializer<IntVarEventData> {


    public IntVarEventData deserialize(ByteBuf inputStream)  {
        IntVarEventData event = new IntVarEventData();
        event.setType(ByteUtil.readInteger(inputStream,1) );
        event.setValue(ByteUtil.readUnsignedLong(inputStream,8) );
        //checksum
        inputStream.skipBytes(CHECKSUM_LENGTH);
        return event;
    }
}
