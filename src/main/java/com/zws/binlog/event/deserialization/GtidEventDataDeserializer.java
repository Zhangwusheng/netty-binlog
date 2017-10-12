/*
 * Copyright 2013 Patrick Prasse
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
package com.zws.binlog.event.deserialization;

import com.zws.ByteUtil;
import com.zws.binlog.event.data.GtidEventData;
import io.netty.buffer.ByteBuf;

/**
 * @author <a href="mailto:pprasse@actindo.de">Patrick Prasse</a>
 */
public class GtidEventDataDeserializer implements EventDataDeserializer<GtidEventData > {
    
    public GtidEventData deserialize ( ByteBuf msg )  {
    
                GtidEventData eventData = new GtidEventData();
        byte flags = (byte) ByteUtil.readInteger ( msg,1 );
        byte[] sid = new byte[16];
        msg.readBytes ( sid );
        long gno = ByteUtil.readUnsignedLong ( msg,8 );
        eventData.setFlags ( flags );
        String gtid = byteArrayToHex(sid, 0, 4) + "-" +
            byteArrayToHex(sid, 4, 2) + "-" +
            byteArrayToHex(sid, 6, 2) + "-" +
            byteArrayToHex(sid, 8, 2) + "-" +
            byteArrayToHex(sid, 10, 6) + ":" +
            String.format("%d", gno);
        eventData.setGtid (  gtid );

        msg.skipBytes(CHECKSUM_LENGTH);
        return eventData;
        
    }
    
    //    @Override
//    public GtidEventData deserialize(ByteArrayInputStream inputStream) throws IOException {
//        GtidEventData eventData = new GtidEventData();
//        byte flags = (byte) inputStream.readInteger(1);
//        byte[] sid = inputStream.read(16);
//        long gno = inputStream.readLong(8);
//        eventData.setFlags(flags);
//        eventData.setGtid(byteArrayToHex(sid, 0, 4) + "-" +
//            byteArrayToHex(sid, 4, 2) + "-" +
//            byteArrayToHex(sid, 6, 2) + "-" +
//            byteArrayToHex(sid, 8, 2) + "-" +
//            byteArrayToHex(sid, 10, 6) + ":" +
//            String.format("%d", gno)
//        );
//        return eventData;
//    }

    private String byteArrayToHex(byte[] a, int offset, int len) {
        StringBuilder sb = new StringBuilder();
        for (int idx = offset; idx < (offset + len) && idx < a.length; idx++) {
            sb.append(String.format("%02x", a[idx] & 0xff));
        }
        return sb.toString();
    }

}
