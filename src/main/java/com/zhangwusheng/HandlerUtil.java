package com.zhangwusheng;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created by zhangwusheng on 17/10/1.
 */
public class HandlerUtil {
    public static void cleanChannelContext( ChannelHandlerContext ctx, Throwable cause) {
        ctx.close ();
    }
    
    public static void cleanChannelContext( ChannelHandlerContext ctx) {
        cleanChannelContext( ctx, null );
    }
}
