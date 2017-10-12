package com.zws.binlog.handler;

import com.zws.binlog.command.AuthenticateCommand;
import com.zws.binlog.network.GreetingPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Created by zhangwusheng on 17/10/10.
 */
public class GreetingPacketHandler extends MessageToMessageDecoder {
    
    @Override
    protected void decode ( ChannelHandlerContext ctx, Object msg, List out ) throws Exception {
        if( msg instanceof GreetingPacket){
            GreetingPacket greetingPacket = (GreetingPacket) msg;
    
            String user = "repl";
            String password = "repl";
            AuthenticateCommand authenticateCommand =
                    new AuthenticateCommand (user,password
                            ,greetingPacket.getScramble ()
                            ,greetingPacket.getServerCollation ()  );
    
            ctx.channel ().writeAndFlush ( authenticateCommand.toByteBuf () );
            ctx.pipeline ().remove ( this );
        }
    }
}
