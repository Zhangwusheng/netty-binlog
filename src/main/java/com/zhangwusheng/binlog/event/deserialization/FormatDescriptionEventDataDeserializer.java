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

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.event.data.FormatDescriptionEventData;
import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class FormatDescriptionEventDataDeserializer implements EventDataDeserializer<FormatDescriptionEventData > {

//    @Override
    public FormatDescriptionEventData deserialize(ByteBuf inputStream)  {
        FormatDescriptionEventData eventData = new FormatDescriptionEventData();
        eventData.setBinlogVersion( ByteUtil.readInteger ( inputStream,2 ));
        eventData.setServerVersion(ByteUtil.readString ( inputStream,50 ).trim());
        inputStream.skipBytes (4); // redundant, present in a header
        eventData.setHeaderLength(ByteUtil.readInteger ( inputStream,1 ));

        inputStream.skipBytes(CHECKSUM_LENGTH);
        // lengths for all event types
        return eventData;
    }
}
