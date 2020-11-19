package org.cat.tunnel.proxy.server.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.ssl.SslContext;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslContext;

    public HttpServerInitializer(SslContext sslContext){
        this.sslContext = sslContext;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline channelPipeline = ch.pipeline();
        if(sslContext != null){
            channelPipeline.addLast(sslContext.newHandler(ch.alloc()));
        }
        channelPipeline.addLast(new HttpDatagramHandler());
        channelPipeline.addLast(new HttpServerCodec());
        channelPipeline.addLast(new HttpServerExpectContinueHandler());
        channelPipeline.addLast(new HttpServerHandler());
    }
}
