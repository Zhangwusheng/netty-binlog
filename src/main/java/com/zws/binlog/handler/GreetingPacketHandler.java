package com.zws.binlog.handler;

import com.zws.binlog.command.NettyAuthenticateCommand;
import com.zws.binlog.network.GreetingPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class GreetingPacketHandler extends MessageToMessageDecoder {
    
    private Logger log = LoggerFactory.getLogger ( GreetingPacketHandler.class );
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        if( msg instanceof GreetingPacket){
            GreetingPacket greetingPacket = (GreetingPacket) msg;
//            log.info ( "Greeting:"+greetingPacket.toString () );
    
            String user = "repl";
            String password = "repl";
            NettyAuthenticateCommand authenticateCommand =
                    new NettyAuthenticateCommand (user,password
                            ,greetingPacket.getScramble ()
                            ,greetingPacket.getServerCollation ()  );
    
            ctx.channel ().writeAndFlush ( authenticateCommand.toByteBuf () );
            ctx.pipeline ().remove ( this );
        }
    }
}
