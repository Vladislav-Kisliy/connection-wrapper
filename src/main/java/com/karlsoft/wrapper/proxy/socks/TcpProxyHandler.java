/*
 * Copyright (C) 2016 Vladislav Kislyi <vladislav.kisliy@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.karlsoft.wrapper.proxy.socks;

import com.karlsoft.wrapper.proxy.multi.MultiplierProxyInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class TcpProxyHandler extends ChannelInboundHandlerAdapter {

    private static final List<String> HOSTS = new LinkedList<>();
    private static final List<String> connected = new LinkedList<>();
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();

    static {
        HOSTS.add("127.0.0.1:10000");
        HOSTS.add("127.0.0.1:20000");
    }

    static final ChannelGroup channels = new DefaultChannelGroup(
            GlobalEventExecutor.INSTANCE);

    
    public static void main(String[] args) throws InterruptedException {
        
        // Configure the bootstrap.
//        try {
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
//                    .childHandler(new BroadProxyInitializer(remoteHost, remotePort))
//                    .childOption(ChannelOption.AUTO_READ, false)
//                    .bind(localPort).sync()
//                    .channel().closeFuture().sync();
//        } finally {
//            stopService();
//        }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel inboundChannel = ctx.channel();

        for (String host : HOSTS) {
            if (!connected.contains(host)) {
                String address = host.split(":")[0];
                int port = Integer.parseInt(host.split(":")[1]);
//                Channel outboundChannel = ConnectionPool.getConnection(address,
//                        port);
                Bootstrap b = new Bootstrap();
                b.group(inboundChannel.eventLoop())
                        .channel(ctx.channel().getClass())
                        .handler(new TcpProxyBackendHandler(inboundChannel))
                        .option(ChannelOption.AUTO_READ, false);
                ChannelFuture f = b.connect(address, port);
                Channel outboundChannel = f.channel();
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future)
                            throws Exception {
                        if (future.isSuccess()) {
                            // connection complete start to read first data
                            inboundChannel.read();
                        } else {
                            // Close the connection if the connection
                            // attempt
                            // has failed.
                            inboundChannel.close();
                        }
                    }
                });

                channels.add(outboundChannel);
                connected.add(host);
                System.out.println("Connected to " + host);
            }

        }

    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg)
            throws Exception {
        channels.flushAndWrite(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(
                    ChannelFutureListener.CLOSE);
        }
    }

    static class TcpProxyBackendHandler extends ChannelInboundHandlerAdapter {

        private final Channel inboundChannel;

        public TcpProxyBackendHandler(Channel inboundChannel) {
            this.inboundChannel = inboundChannel;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.read();
            ctx.write(Unpooled.EMPTY_BUFFER);
        }

        @Override
        public void channelRead(final ChannelHandlerContext ctx, Object msg)
                throws Exception {
            inboundChannel.writeAndFlush(msg).addListener(
                    new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future)
                        throws Exception {
                    if (future.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        future.channel().close();
                    }
                }
            });
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            TcpProxyHandler.closeOnFlush(inboundChannel);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            cause.printStackTrace();
            TcpProxyHandler.closeOnFlush(ctx.channel());
        }

    }

}
