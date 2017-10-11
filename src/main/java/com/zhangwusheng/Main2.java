package com.zhangwusheng;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.zhangwusheng.binlog.GtidSet;
import com.zhangwusheng.binlog.command.DumpBinaryLogGitdCommand;
import com.zhangwusheng.binlog.handler.MysqlProtoclHeaderHandler;
import com.zhangwusheng.binlog.nettyhandler.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zws on 9/29/17.
 */
public class Main2 {


    private Logger log = LoggerFactory.getLogger ( Main2.class );

    public void test()
    {

//        log.info("this is mtst log");
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
                            channelPipeline.addLast ( "GreetingProtocolHandler",new GreetingProtocolHandler () );
                            channelPipeline.addLast ( "GreetingPacketHandler",new GreetingPacketHandler () );
                            channelPipeline.addLast ( "AuthProtocolHandler",new AuthProtocolHandler () );
                            channelPipeline.addLast ( "AuthOKPacketHandler",new AuthOKPacketHandler () );
                            channelPipeline.addLast ( "ShowMasterStatusProtocolHandler",new ShowMasterStatusProtocolHandler () );
                            channelPipeline.addLast ( "ShowMasterStatusRecordHandler",new ShowMasterStatusRecordHandler () );
                            channelPipeline.addLast ( "FetchBinlogChecksumProtocolHandler",new FetchBinlogChecksumProtocolHandler () );
                            channelPipeline.addLast ( "FetchBinlogCheckRecordHandler",new FetchBinlogCheckRecordHandler () );
                            channelPipeline.addLast ( "SetMasterBinlogChecksumProtocolHandler",new SetMasterBinlogChecksumProtocolHandler () );
                            channelPipeline.addLast ( "SetMasterBinlogChecksumRecordHandler",new SetMasterBinlogChecksumRecordHandler () );
                            channelPipeline.addLast ( "BinlogEventProtocolHandler",new BinlogEventProtocolHandler () );
                            channelPipeline.addLast ( "BinlogEventRecordHandler",new BinlogEventRecordHandler () );
                        }
                    });
            
            bootstrap.attr ( dbUser,"repl" );
            bootstrap.attr ( dbPassword,"repl" );

//            ChannelFuture channelFuture = bootstrap.connect("192.168.1.105",3333).sync();
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
        
//        StringBuilder sb = new StringBuilder (  );
//        sb.append ( "zhangwusheng" );
//        sb.setLength ( sb.length ()-1 );
//        System.out.println (sb.toString () );
//        System.exit ( 0 );
        
//        ByteBuf byteBuf = Unpooled.buffer ( 10 );
//        GtidSet gtidSet = new GtidSet ( "584416c8-a84b-11e7-b641-74e50bc69d0a:1-2" );
//        String uuid = "584416c8-a84b-11e7-b641-74e50bc69d0a";
//        uuid = uuid.replace ( "-","" );
//        byte[] bb = DumpBinaryLogGitdCommand.hexToByteArray (uuid  );
//        byteBuf.writeBytes ( bb );
//        String b2 = ByteBufUtil.prettyHexDump ( byteBuf );
//        System.out.println (b2 );
//
//        System.out.println (gtidSet.toString () );
//        System.exit ( 0 );
        
       
//        byteBuf.writeBytes ( "Zhangwusheng".getBytes () );
//        ByteBuf byteBuf2 = Unpooled.buffer ( 10 );
//        byteBuf2.writeBytes ( "Chenlingling".getBytes () );
//        byteBuf.writeBytes(byteBuf2);
//
//        System.out.println(byteBuf.readableBytes());
//        byte bbb = byteBuf.readByte ();
//        byte aaa = byteBuf.getByte ( 1 );
//
//        System.out.println ( (char)bbb );
//        System.out.println ((char)aaa );
//        System.exit ( 1 );
        
        
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
        
        
        System.out.println(Main2.class.getName());
        
        new Main2 ().test ();
        
    }
}
