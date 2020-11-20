package org.cat.tunnel.proxy.server.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpConstants;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpDatagramHandler extends ChannelInboundHandlerAdapter {

    volatile boolean requestStart = true;
    int contentLength = 0;
    StringBuilder requestDatagramHeader = null;
    StringBuilder requestDatagramBody = null;

    StringBuilder line = new StringBuilder();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    volatile boolean bodyStart = false;

    public static String GET = "GET";
    public static String POST = "POST";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = ((ByteBuf)msg).copy();
        super.channelRead(ctx, msg);
        if(requestStart){
            requestDatagramHeader = new StringBuilder();
            requestDatagramBody = new StringBuilder();
            requestStart = false;
        }
        System.out.println("process http request datagram index=" + atomicInteger.incrementAndGet());
        byteBuf.forEachByte(b->{
            char c = (char) (b & 0xFF);
            // Drop CR if we had a CRLF pair  CRLF http end flag
            if(!bodyStart) {
                if (c == HttpConstants.LF) {
                    if (line.charAt(line.length() - 1) == HttpConstants.CR) {
                        line.append(c);
                        requestDatagramHeader.append(line);
                        String lineStr = line.toString();
                        if (lineStr.startsWith("Content-Length:")) {
                            contentLength = parseContentLength(lineStr);
                        }
                        if (line.charAt(0) == HttpConstants.CR && line.charAt(1) == HttpConstants.LF) {
                            if(contentLength == 0){ // 无body可读
                                requestStart = true;
                                bodyStart =false;
                            }else{
                                bodyStart = true;
                            }
                            line = new StringBuilder();
                            return true;
                        }
                        if (lineStr.startsWith(GET)
                                || lineStr.startsWith(POST)) { //新的请求
                        }
                        line = new StringBuilder();
                        return true;
                    }
                }
            }
            if(bodyStart){
                contentLength--;
            }
            line.append(c);
            if(bodyStart && contentLength == 0){ // body 读取完全
                requestDatagramBody.append(line);
                line = new StringBuilder();
                bodyStart = false;
                requestStart = true;
            }
            return true;
        });

        // 报文结束
        if(requestStart) {
            System.out.println("time at " + LocalDateTime.now().toString() + " request datagram: \n" + requestDatagramHeader.toString());
            System.out.println("body content length=" + requestDatagramBody.length());
            requestDatagramBody = null;
            requestDatagramHeader = null;
        }
    }

    private int parseContentLength(String line){
        if(StringUtils.isBlank(line)){
            return 0;
        }
        String[] segments = line.split(":");
        if(segments.length != 2){
            return 0;
        }
        return Integer.parseInt(segments[1].trim());
    }
}
