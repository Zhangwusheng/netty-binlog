package com.zhangwusheng.binlog.event.deserialization;

import com.zhangwusheng.binlog.event.*;
import com.zhangwusheng.binlog.event.data.NullEventData;
import io.netty.buffer.ByteBuf;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Created by zhangwusheng on 17/10/11.
 */
public class EventDeserializer {
    private EventHeaderV4Deserializer eventHeaderDeserializer= new EventHeaderV4Deserializer ();;
    private final Map<EventType, EventDataDeserializer> eventDataDeserializers= new IdentityHashMap<EventType, EventDataDeserializer> ();;
    private NullEventDataDeserializer nullEventDataDeserializer = new NullEventDataDeserializer ();
    
    public EventDeserializer(){
        
        registerDefaultEventDataDeserializers();
    }
    
    EventHeaderV4 eventHeader;
    EventData eventData;
    
    public Event buildEvent(){
        Event event = new Event ( eventHeader,eventData  );
        return event;
    }
    public EventDeserializer decodeEventData(ByteBuf in ){
//        if( eventHeader.getEventType () == EventType.GTID){
//            EventDataDeserializer dataDeserializer = eventDataDeserializers.get ( eventHeader.getEventType () );
//            EventData eventData = dataDeserializer.deserialize ( in );
//        }
    
        EventDataDeserializer dataDeserializer =getDataDeserializer(eventHeader.getEventType ());
        eventData = dataDeserializer.deserialize ( in );
        return this;
    }
    
    
    private EventDataDeserializer getDataDeserializer(EventType type){
        if( eventDataDeserializers.containsKey ( type )){
            return eventDataDeserializers.get ( type );
        }
        return nullEventDataDeserializer;
    }
    
    public EventDeserializer decodeHeader( ByteBuf inputStream) {
    
        eventHeader = eventHeaderDeserializer.deserialize(inputStream);

        return this;
//        if (inputStream.peek() == -1) {
//            return null;
//        }
//        EventHeader eventHeader = eventHeaderDeserializer.deserialize(inputStream);
//        EventDataDeserializer eventDataDeserializer = getEventDataDeserializer(eventHeader.getEventType());
//        if (eventHeader.getEventType() == EventType.TABLE_MAP && tableMapEventDataDeserializer != null) {
//            eventDataDeserializer = tableMapEventDataDeserializer;
//        }
//        EventData eventData = deserializeEventData(inputStream, eventHeader, eventDataDeserializer);
//        if (eventHeader.getEventType() == EventType.TABLE_MAP) {
//            TableMapEventData tableMapEvent;
//            if (eventData instanceof EventDataWrapper) {
//                EventDataWrapper eventDataWrapper = (EventDataWrapper) eventData;
//                tableMapEvent = (TableMapEventData) eventDataWrapper.getInternal();
//                if (tableMapEventDataDeserializer != null) {
//                    eventData = eventDataWrapper.getExternal();
//                }
//            } else {
//                tableMapEvent = (TableMapEventData) eventData;
//            }
//            tableMapEventByTableId.put(tableMapEvent.getTableId(), tableMapEvent);
//        }
//        return new Event(eventHeader, eventData);
    }
    
    
    private void registerDefaultEventDataDeserializers() {
//        eventDataDeserializers.put(EventType.FORMAT_DESCRIPTION,
//                new FormatDescriptionEventDataDeserializer());
//        eventDataDeserializers.put(EventType.ROTATE,
//                new RotateEventDataDeserializer());
//        eventDataDeserializers.put(EventType.INTVAR,
//                new IntVarEventDataDeserializer());
//        eventDataDeserializers.put(EventType.QUERY,
//                new QueryEventDataDeserializer());
//        eventDataDeserializers.put(EventType.TABLE_MAP,
//                new TableMapEventDataDeserializer());
//        eventDataDeserializers.put(EventType.XID,
//                new XidEventDataDeserializer());
//        eventDataDeserializers.put(EventType.WRITE_ROWS,
//                new WriteRowsEventDataDeserializer(tableMapEventByTableId));
//        eventDataDeserializers.put(EventType.UPDATE_ROWS,
//                new UpdateRowsEventDataDeserializer(tableMapEventByTableId));
//        eventDataDeserializers.put(EventType.DELETE_ROWS,
//                new DeleteRowsEventDataDeserializer(tableMapEventByTableId));
//        eventDataDeserializers.put(EventType.EXT_WRITE_ROWS,
//                new WriteRowsEventDataDeserializer(tableMapEventByTableId).
//                        setMayContainExtraInformation(true));
//        eventDataDeserializers.put(EventType.EXT_UPDATE_ROWS,
//                new UpdateRowsEventDataDeserializer(tableMapEventByTableId).
//                        setMayContainExtraInformation(true));
//        eventDataDeserializers.put(EventType.EXT_DELETE_ROWS,
//                new DeleteRowsEventDataDeserializer(tableMapEventByTableId).
//                        setMayContainExtraInformation(true));
//        eventDataDeserializers.put(EventType.ROWS_QUERY,
//                new RowsQueryEventDataDeserializer());
        eventDataDeserializers.put(EventType.GTID,
                new GtidEventDataDeserializer());
    }
}
