package com.zws.binlog.command;

/**
 *
 * @author zhiqiang.liu
 * @2016年1月1日
 * @QQ: 837500869
 */

import com.zws.ByteUtil;
import com.zws.binlog.network.ClientCapabilities;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class NettyAuthenticateCommand implements Command{
	
	private String scramble;
	private int collation;
	private String user;
	private String password;
	
	public NettyAuthenticateCommand (String user,String password, String scramble, int collation) {
		
		this.user = user;
		this.password = password;
		
		this.scramble = scramble;
		this.collation = collation;
	}
	
	
	public ByteBuf toByteBuf ( ) {
		
		int clientCapabilities = ClientCapabilities.LONG_FLAG | ClientCapabilities.PROTOCOL_41
				| ClientCapabilities.SECURE_CONNECTION;
		byte[] clientCapabilitiesBytes = ByteUtil.writeInt(clientCapabilities, 4);
		
		int maxLength = 0;
		byte[] maxLengthBytes = ByteUtil.writeInt(maxLength, 4);
		byte[] collationBytes = ByteUtil.writeInt(this.collation, 1);
		byte[] zeroBytes = ByteUtil.writeInt(0, 23);
		byte[] usernameBytes = ByteUtil.writeString(this.user);
		byte[] lengthBytes = new byte[1];
		byte[] passwordBytes = null;
		
		if (null == password || password.trim().length() == 0) {
			passwordBytes = new byte[0];// 空的
		} else {
			passwordBytes = passwordCompatibleWithMySQL411(password, scramble);
			
		}
		lengthBytes[0] = (byte) passwordBytes.length;
		
		int totalCount = 0;
		totalCount += clientCapabilitiesBytes.length;
		totalCount += maxLengthBytes.length;
		totalCount += collationBytes.length;
		totalCount += zeroBytes.length;
		totalCount += usernameBytes.length;
		totalCount += lengthBytes.length;
		totalCount += passwordBytes.length;
		byte[] totalCountBytes = ByteUtil.writeInt(totalCount, 3);
		byte[] commandTypeBytes = new byte[1];
		commandTypeBytes[0] = 1;// 对于验证命令，这里就是1,其它都是0
		// 所有内容串联起来
		ByteBuf finalBuf = Unpooled.buffer(totalCount + 4);
		
		finalBuf.writeBytes(totalCountBytes);
		finalBuf.writeBytes(commandTypeBytes);
		finalBuf.writeBytes(clientCapabilitiesBytes);
		finalBuf.writeBytes(maxLengthBytes);
		finalBuf.writeBytes(collationBytes);
		finalBuf.writeBytes(zeroBytes);
		finalBuf.writeBytes(usernameBytes);
		finalBuf.writeBytes(lengthBytes);
		finalBuf.writeBytes(passwordBytes);
		
		return finalBuf;
	}
	
	public static byte[] passwordCompatibleWithMySQL411(String password, String salt) {
		MessageDigest sha;
		try {
			sha = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] passwordHash = sha.digest(password.getBytes());
		return xor(passwordHash, sha.digest(union(salt.getBytes(), sha.digest(passwordHash))));
	}
	
	public static byte[] union(byte[] a, byte[] b) {
		byte[] r = new byte[a.length + b.length];
		System.arraycopy(a, 0, r, 0, a.length);
		System.arraycopy(b, 0, r, a.length, b.length);
		return r;
	}
	
	public static byte[] xor(byte[] a, byte[] b) {
		byte[] r = new byte[a.length];
		for (int i = 0; i < r.length; i++) {
			r[i] = (byte) (a[i] ^ b[i]);
		}
		return r;
	}
	
}
