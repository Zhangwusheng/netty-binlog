package com.zhangwusheng;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.zhangwusheng.binlog.AuthenticateResultHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.LoggerFactory;

/**
 * Created by zws on 9/29/17.
 */
public class Main {
    
    
    
    public void test()
    {

    
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    
    
        Bootstrap bootstrap = new Bootstrap();
        AttributeKey<String> dbUser =  AttributeKey.valueOf ("db.user");
        AttributeKey<String> dbPassword =  AttributeKey.valueOf ("db.password");
        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_RCVBUF,4*1024)
                    .option(ChannelOption.SO_SNDBUF,4*1024)
                    .handler( new ChannelInitializer<SocketChannel>(){
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast ( "MysqlProtoclHeaderHandler",new MysqlProtoclHeaderHandler () );
                            channelPipeline.addLast ( "GreetingPacketResultHandler",new GreetingPacketResultHandler () );
                            channelPipeline.addLast ( "AuthenticateResultHandler",new AuthenticateResultHandler () );
//                            channelPipeline.addLast ( "GreetingPacketResultHandler",new GreetingPacketResultHandler () );
                        }
                    });
        
            bootstrap.attr ( dbUser,"root111" );
            bootstrap.attr ( dbPassword,"password1111" );
            
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1",3306).sync();
            //channelFuture.isDone ();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception ex){
        
        }
        finally {
            eventLoopGroup.shutdownGracefully();
        
        }
    }
    public static void main(String[] args) {
    
        ByteBuf byteBuf = Unpooled.buffer ( 10 );
        byteBuf.writeBytes ( "Zhangwusheng".getBytes () );
    
        System.out.println ( byteBuf.readableBytes () );
        
        byteBuf.skipBytes ( 1 );
        byte[] test = new byte[5];
        byteBuf.readBytes (test );
        
        String s = new String ( test );
        System.out.println ( s );
    
        System.out.println ( byteBuf.readableBytes () );
//        System.exit ( 0 ); ;
        
    
         String CDC_HOME_PROPERTY = "cdc.home.dir";
         String CDCHome = System.getProperty ( CDC_HOME_PROPERTY, System.getenv ( "CDC_HOME" ) );
    
        LoggerContext lc = ( LoggerContext ) LoggerFactory.getILoggerFactory ( );
        JoranConfigurator configurator = new JoranConfigurator ( );
        configurator.setContext ( lc );
        lc.reset ( );
        try {
            configurator.doConfigure ( CDCHome + "/conf/logback.xml" );
        }
        catch ( JoranException e ) {
            e.printStackTrace ( );
            System.exit ( 1 );
        }
    
    
        System.out.println(Main.class.getName());
        
    new Main().test ();
        
    }
}
