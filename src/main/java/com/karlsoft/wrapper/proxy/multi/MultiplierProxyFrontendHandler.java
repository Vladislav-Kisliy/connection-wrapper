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
package com.karlsoft.wrapper.proxy.multi;

import com.google.common.net.HostAndPort;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public class MultiplierProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = Logger.getLogger(MultiplierProxyFrontendHandler.class.getName());
    private final List<HostAndPort> hosts = new LinkedList<>();
    private ByteBuf buf;

    public MultiplierProxyFrontendHandler() {
        hosts.add(HostAndPort.fromString("127.0.0.1: 10000"));
        hosts.add(HostAndPort.fromString("127.0.0.1:20000"));
        hosts.add(HostAndPort.fromString("127.0.0.1:30000"));
        hosts.add(HostAndPort.fromString("127.0.0.1:40000"));
        hosts.add(HostAndPort.fromString("127.0.0.1:50000"));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        buf = (ByteBuf) msg;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws InterruptedException {
        ctx.flush();
        ctx.close();
        for (HostAndPort host : hosts) {
            // Configure the client.
            EventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap b = new Bootstrap();
                b.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel ch) throws Exception {
                                ChannelPipeline p = ch.pipeline();
                                //p.addLast(new LoggingHandler(LogLevel.INFO));
                                p.addLast(new MultiplierProxyBackendHandler(buf.copy()));
                            }
                        });
                // Start the client.
                ChannelFuture f = b.connect(host.getHost(), host.getPort());
                f.addListener((ChannelFutureListener) (ChannelFuture future) -> {
                    if (future.isSuccess()) {
                        // connection complete start to read first data
                        LOG.log(Level.INFO, "Connected to {0}:{1} successfully.",
                                new Object[]{host.getHost(), host.getPort()});
                    } else {
                        // Close the connection if the connection attempt has failed.
                        LOG.log(Level.WARNING, "Connection problem to {0}:{1}.",
                                new Object[]{host.getHost(), host.getPort()});
                    }
                });
                // Wait until the connection is closed.
                f.channel().closeFuture().sync();
            } finally {
                // Shut down the event loop to terminate all threads.
                group.shutdownGracefully();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
