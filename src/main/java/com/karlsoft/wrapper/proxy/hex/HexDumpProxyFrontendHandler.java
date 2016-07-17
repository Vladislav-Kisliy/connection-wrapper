/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.karlsoft.wrapper.proxy.hex;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import java.util.LinkedList;
import java.util.List;

public class HexDumpProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private final int remotePort;

    private final List<String> HOSTS = new LinkedList<>();
    private final List<Channel> CHANNELS = new LinkedList<>();

//    private volatile Channel outboundChannel;
    public HexDumpProxyFrontendHandler(int remotePort) {
        this.remotePort = remotePort;
        HOSTS.add("127.0.0.1:10000");
        HOSTS.add("127.0.0.1:20000");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        final Channel inboundChannel = ctx.channel();

        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
//                .handler(new HexDumpProxyBackendHandler(inboundChannel))
                .option(ChannelOption.AUTO_READ, false);
//        for (String host : HOSTS) {
//            ChannelFuture f = b.connect(host, remotePort);
//            CHANNELS.add(f.channel());
////        outboundChannel = f.channel();
//            f.addListener(new ChannelFutureListener() {
//                @Override
//                public void operationComplete(ChannelFuture future) {
//                    if (future.isSuccess()) {
//                        // connection complete start to read first data
//                        inboundChannel.read();
//                    } else {
//                        // Close the connection if the connection attempt has failed.
//                        inboundChannel.close();
//                    }
//                }
//            });
//        }

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        System.out.println("get message" +msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
//        if (outboundChannel != null) {
//            closeOnFlush(outboundChannel);
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    /**
     * Closes the specified channel after all queued write requests are flushed.
     */
    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
