package com.zhangwusheng.binlog.handler;

/**
 * Protocol::Handshake
 * Initial Handshake Packet
 * When the client connects to the server the server sends a handshake packet to the client.
 * Depending on the server version and configuration options different variants of
 * the initial packet are sent.
 *
 * To permit the server to add support for newer protocols, the first byte defines the
 * protocol version.Since 3.21.0 the Protocol::HandshakeV10 is sent, while it was still
 * supporting Protocol::HandshakeV9 with a compile time option.
 *
 * Payload
 * 1              protocol_version
 * ...
 * Protocol::HandshakeV10
 * Initial Handshake Packet - protocol version 10
 *
 * Payload
 * 1              [0a] protocol version
 * string[NUL]    server version
 * 4              connection id
 * string[8]      auth-plugin-data-part-1
 * 1              [00] filler
 * 2              capability flags (lower 2 bytes)
 * if more data in the packet:
 * 1              character set
 * 2              status flags
 * 2              capability flags (upper 2 bytes)
 * if capabilities & CLIENT_PLUGIN_AUTH {
 * 1              length of auth-plugin-data
 * } else {
 * 1              [00]
 * }
 * string[10]     reserved (all [00])
 * if capabilities & CLIENT_SECURE_CONNECTION {
 * string[$len]   auth-plugin-data-part-2 ($len=MAX(13, length of auth-plugin-data - 8))
 * if capabilities & CLIENT_PLUGIN_AUTH {
 * string[NUL]    auth-plugin name
 * }
 *
 * Fields各个字段的具体含义，上面的才是具体的格式，这里只是解释含义。
 * protocol_version (1) -- 0x0a protocol_version
 * server_version (string.NUL) -- human-readable server version
 * connection_id (4) -- connection id
 * auth_plugin_data_part_1 (string.fix_len) -- [len=8] first 8 bytes of the auth-plugin data
 * filler_1 (1) -- 0x00
 * capability_flag_1 (2) -- lower 2 bytes of the Protocol::CapabilityFlags (optional)
 * character_set (1) -- default server character-set, only the lower 8-bits Protocol::CharacterSet (optional)
 * status_flags (2) -- Protocol::StatusFlags (optional)
 * capability_flags_2 (2) -- upper 2 bytes of the Protocol::CapabilityFlags
 * auth_plugin_data_len (1) -- length of the combined auth_plugin_data, if auth_plugin_data_len is > 0
 * auth_plugin_name (string.NUL) -- name of the auth_method that the auth_plugin_data belongs to
 *
 * Note
 * Due to Bug#59453 the auth-plugin-name is missing the terminating NUL-char in versions prior to 5.5.10 and 5.6.2.
 * Returns
 * Protocol::HandshakeResponse from the client
 * Implemented By
 * send_server_handshake_packet()
 *
 * Example
 * 36 00 00 00 0a 35 2e 35    2e 32 2d 6d 32 00 0b 00    6....5.5.2-m2...
 * 00 00 64 76 48 40 49 2d    43 4a 00 ff f7 08 02 00    ..dvH@I-CJ......
 * 00 00 00 00 00 00 00 00    00 00 00 00 00 2a 34 64    .............*4d
 * 7c 63 5a 77 6b 34 5e 5d    3a 00                      |cZwk4^]:.
 *
 * If CLIENT_PLUGIN_AUTH is set the server sends the name of the Authentication Method that the auth_plugin_data belongs to:
 * 50 00 00 00 0a 35 2e 36    2e 34 2d 6d 37 2d 6c 6f    P....5.6.4-m7-lo[…]”
 * 00 ff    g.V...RB3vz&Gr..
 * ff 08 02 00 0f c0 15 00    00 00 00 00 00 00 00 00    ................
 * 00 2b 79 44 26 2f 5a 5a    33 30 35 5a 47 00 6d 79    .+yD&/ZZ305ZG.my
 * 73 71 6c 5f 6e 61 74 69    76 65 5f 70 61 73 73 77    sql_native_passw
 * 6f 72 64 00                                           ord
 *
 * Note
 * The auth-plugin-data is the concatenation of strings auth-plugin-data-part-1 and auth-plugin-data-part-2.
 * Note
 * Only the fields up to the filler after the auth_plugin_data_part_1 are required, all other fields are optional
 *
 *
 * Protocol::HandshakeV9:
 * Initial Handshake Packet - Protocol Version 9
 * Payload
 * 1              [09] protocol_version
 * string[NUL]    server_version
 * 4              connection_id
 * string[NUL]    scramble
 *
 * Fields
 * protocol_version (1) -- 0x09 protocol_version
 * server_version (string.NUL) -- human-readable server version
 * connection_id (4) -- connection id
 * auth_plugin_data (string.NUL) -- auth plugin data for Authentication::Old
 *
 * Returns
 * Protocol::HandshakeResponse320”
 *
 
 mysql-5.7.17/sql/auth/sql_authentication.cc:527 send_server_handshake_packet的实现：
 sends a server handshake initialization packet, the very first packet
 after the connection was established
 
 Packet format:
 
 Bytes       Content
 -----       ----
 1           protocol version (always 10)
 n           server version string, \0-terminated
 4           thread id
 8           first 8 bytes of the plugin provided data (scramble)
 1           \0 byte, terminating the first part of a scramble
 2           server capabilities (two lower bytes)
 1           server character set
 2           server status
 *2           server capabilities (two upper bytes)
 *1           length of the scramble
 *10          reserved, always 0 上述3列是被下面代码skip的13个
 n           rest of the plugin provided data (at least 12 bytes)
 1           \0 byte, terminating the second part of a scramble
 
 @retval 0 ok
 @retval 1 error
 */

import com.zhangwusheng.HandlerUtil;
import com.zhangwusheng.binlog.command.NettyAuthenticateCommand;
import com.zhangwusheng.binlog.network.GreetingPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GreetingPacketResultHandler extends SimpleChannelInboundHandler<ByteBuf> {
	private Logger log = LoggerFactory.getLogger ( GreetingPacketResultHandler.class );
	
	
	@Override
	public void channelRegistered ( ChannelHandlerContext ctx ) throws Exception {
		
		AttributeKey<String> dbUserKey =  AttributeKey.valueOf ("db.user");
		AttributeKey<String> dbPasswordKey =  AttributeKey.valueOf ("db.password");
		String dbUser = ctx.channel ().attr ( dbUserKey ).get ();
		String dbPassword = ctx.channel ().attr ( dbPasswordKey ).get ();
		
		log.info ( "-----------------------\nUser="+ dbUser+"\nPwd=:"+dbPassword);
		super.channelRegistered ( ctx );
		
		
	}
	
	@Override
	protected void channelRead0( ChannelHandlerContext context, ByteBuf msg) throws Exception {
		try {
			if (null == msg) {
				return;
			}
			
			GreetingPacket greetingPacket = new GreetingPacket ();
			greetingPacket.parse ( msg );
			
			String user = "repl";
			String password = "repl";
			NettyAuthenticateCommand authenticateCommand =
					new NettyAuthenticateCommand (user,password
							,greetingPacket.getScramble ()
							,greetingPacket.getServerCollation ()  );
			
			log.info ( "writeAndFlush authenticateCommand" );
			context.channel ().writeAndFlush ( authenticateCommand.toByteBuf () );
			context.pipeline().remove(this);
		} catch (Exception e) {
			log.error( e.toString());
			throw new Exception(e);
		}
	}
	
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		// cause.printStackTrace();//务必要关闭
//		LoggerUtils.error(logger, cause.toString());
//		NettyUtils.cleanChannelContext(ctx, cause);
		HandlerUtil.cleanChannelContext ( ctx, cause );
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		LoggerUtils.debug(logger, "[channelInactive] socket is closed by remote server");
		HandlerUtil.cleanChannelContext(ctx, null);
	}
	
}
