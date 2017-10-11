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

//import com.github.shyiko.mysql.binlog.event.RotateEventData;
//import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.event.data.RotateEventData;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class RotateEventDataDeserializer implements EventDataDeserializer<RotateEventData > {

//    @Override
    public RotateEventData deserialize(ByteBuf inputStream)  {
        RotateEventData eventData = new RotateEventData();
        eventData.setBinlogPosition( ByteUtil.readUnsignedLong ( inputStream,8 ));
        //减掉4个字节的checksum
        eventData.setBinlogFilename ( ByteUtil.readString ( inputStream,inputStream.readableBytes ()-4 ) );
//        eventData.setBinlogFilename(inputStream.readString(inputStream.available()));
        return eventData;
    }
}
