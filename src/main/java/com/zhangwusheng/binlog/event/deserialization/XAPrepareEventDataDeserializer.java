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
import com.zhangwusheng.binlog.event.data.XAPrepareEventData;
import io.netty.buffer.ByteBuf;

/**
 * https://github.com/mysql/mysql-server/blob/5.7/libbinlogevents/src/control_events.cpp#L590
 * <p>
 * onePhase : boolean, 1byte
 * formatID : int, 4byte
 * gtridLength : int, 4byte
 * bqualLength : int, 4byte
 * data : String, gtrid + bqual, (gtridLength + bqualLength)byte
 * <p>
 * @author <a href="https://github.com/stevenczp">Steven Cheng</a>
 */
public class XAPrepareEventDataDeserializer implements EventDataDeserializer<XAPrepareEventData> {
//    @Override
    public XAPrepareEventData deserialize(ByteBuf inputStream)  {
        XAPrepareEventData xaPrepareEventData = new XAPrepareEventData();
        byte one = inputStream.readByte();
//        xaPrepareEventData.setOnePhase(inputStream.read() == 0x00 ? false : true);
        xaPrepareEventData.setOnePhase(one == 0x00 ? false : true);

//        xaPrepareEventData.setFormatID(inputStream.readInteger(4));
//        xaPrepareEventData.setGtridLength(inputStream.readInteger(4));
//        xaPrepareEventData.setBqualLength(inputStream.readInteger(4));

        xaPrepareEventData.setFormatID(ByteUtil.readInteger(inputStream,4));
        xaPrepareEventData.setGtridLength(ByteUtil.readInteger(inputStream,4));
        xaPrepareEventData.setBqualLength(ByteUtil.readInteger(inputStream,4));


//        xaPrepareEventData.setData(inputStream.read(
//            xaPrepareEventData.getGtridLength() + xaPrepareEventData.getBqualLength()));

        xaPrepareEventData.setData(ByteUtil.readSpecifiedLengthBytes(inputStream,
                xaPrepareEventData.getGtridLength() + xaPrepareEventData.getBqualLength()));

        return xaPrepareEventData;
    }
}
