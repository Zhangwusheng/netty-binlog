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

//import com.github.shyiko.mysql.binlog.event.TableMapEventData;
//import com.github.shyiko.mysql.binlog.io.ByteArrayInputStream;

import com.zhangwusheng.ByteUtil;
import com.zhangwusheng.binlog.event.data.TableMapEventData;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.BitSet;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class TableMapEventDataDeserializer implements EventDataDeserializer<TableMapEventData> {

//    @Override
    public TableMapEventData deserialize(ByteBuf inputStream)  {
        TableMapEventData eventData = new TableMapEventData();
        long tableId = ByteUtil.readUnsignedLong(inputStream,6);
        eventData.setTableId(tableId);
//        eventData.setTableId(inputStream.readLong(6));
        inputStream.skipBytes(3); // 2 bytes reserved for future use + 1 for the length of database name
        String database = ByteUtil.readZeroTerminatedString(inputStream);
        eventData.setDatabase(database);

//        eventData.setDatabase(inputStream.readZeroTerminatedString());
        inputStream.skipBytes(1); // table name
        String tableName =ByteUtil.readZeroTerminatedString(inputStream);
//        eventData.setTable(inputStream.readZeroTerminatedString());
        eventData.setTable(tableName);
//        int numberOfColumns = inputStream.readPackedInteger();
        int numberOfColumns = ByteUtil.readPackedInteger(inputStream);

//        byte[] bytesOfColumnsTypes = new byte[numberOfColumns];
//        inputStream.readBytes(bytesOfColumnsTypes);

        byte[] bytesOfColumnsTypes = ByteUtil.readSpecifiedLengthBytes(inputStream,numberOfColumns);

                eventData.setColumnTypes(bytesOfColumnsTypes);
//        inputStream.readPackedInteger(); // metadata length
        int metadataLength =ByteUtil.readPackedInteger(inputStream);
        eventData.setMetadataLength(metadataLength);

        eventData.setColumnMetadata(readMetadata(inputStream, eventData.getColumnTypes()));
        BitSet bitSet = ByteUtil.readBitSet(inputStream,numberOfColumns,true);
//        eventData.setColumnNullability(inputStream.readBitSet(numberOfColumns, true));


        inputStream.skipBytes(CHECKSUM_LENGTH);
        return eventData;
    }

    private int[] readMetadata(ByteBuf inputStream, byte[] columnTypes)  {
        int[] metadata = new int[columnTypes.length];
        for (int i = 0; i < columnTypes.length; i++) {
            switch(ColumnType.byCode(columnTypes[i] & 0xFF)) {
                case FLOAT:
                case DOUBLE:
                case BLOB:
                case JSON:
                case GEOMETRY:
//                    metadata[i] = inputStream.readInteger(1);
                    metadata[i] = ByteUtil.readInteger(inputStream,1);
                    break;
                case BIT:
                case VARCHAR:
                case NEWDECIMAL:
//                    metadata[i] = inputStream.readInteger(2);
                    metadata[i] = ByteUtil.readInteger(inputStream,1);
                    break;
                case SET:
                case ENUM:
                case STRING:
                    byte[] bytesT1 = new byte[2];
                    inputStream.readBytes(bytesT1);
//                    metadata[i] = bigEndianInteger(inputStream.read(2), 0, 2);
                    metadata[i] = ByteUtil.bigEndianInteger(bytesT1, 0, 2);
                    break;
                case TIME_V2:
                case DATETIME_V2:
                case TIMESTAMP_V2:
//                    metadata[i] = inputStream.readInteger(1); // fsp (@see {@link ColumnType})
                    metadata[i] = ByteUtil.readInteger(inputStream,1);
                    break;
                default:
                    metadata[i] = 0;
            }
        }
        return metadata;
    }



}
