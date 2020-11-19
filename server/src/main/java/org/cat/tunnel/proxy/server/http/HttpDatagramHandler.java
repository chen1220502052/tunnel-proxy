package org.cat.tunnel.proxy.server.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpDatagramHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = ((ByteBuf)msg).copy();
        super.channelRead(ctx, msg);
        StringBuilder sb = new StringBuilder();
        byteBuf.forEachByte(b->{
            char c = (char) (b & 0xFF);
            sb.append(c);
            return true;
        });
        System.out.println(sb.toString());
    }
}
