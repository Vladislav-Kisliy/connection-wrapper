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
package com.karlsoft.wrapper.proxy.hex;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
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

public class MainTest {

    private static final Logger LOG = Logger.getLogger(MainTest.class.getName());
    private final List<ConnectionInfo> HOSTS = new LinkedList<>();
    private final ByteBuf buf;

    public MainTest() {
        HOSTS.add(new ConnectionInfo("127.0.0.1", 10000));
        HOSTS.add(new ConnectionInfo("127.0.0.1", 20000));
        HOSTS.add(new ConnectionInfo("127.0.0.1", 30000));
        HOSTS.add(new ConnectionInfo("127.0.0.1", 40000));
        HOSTS.add(new ConnectionInfo("127.0.0.1", 50000));

        buf = Unpooled.buffer(100);
        for (int i = 0; i < buf.capacity(); i++) {
            buf.writeByte((byte) i);
        }
    }
    

    public static void main(String[] args) throws InterruptedException {
        new MainTest().start();
    }

    public void start() throws InterruptedException {
        for (ConnectionInfo connectionInfo : HOSTS) {
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
                                p.addLast(new MultiplierClientHandler(buf.copy()));
                            }
                        });
                // Start the client.
                ChannelFuture f = b.connect(connectionInfo.getHost(), connectionInfo.getPort());
                f.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (future.isSuccess()) {
                            // connection complete start to read first data
                            LOG.log(Level.INFO, "Connected to {0}:{1} successfully.",
                                    new Object[]{connectionInfo.getHost(), connectionInfo.getPort().toString()});
                        } else {
                            // Close the connection if the connection attempt has failed.
                            LOG.log(Level.WARNING, "Connection problem to {0}:{1}.",
                                    new Object[]{connectionInfo.getHost(), connectionInfo.getPort().toString()});
                        }
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

    private class ConnectionInfo {

        private final String host;
        private final Integer port;

        public ConnectionInfo(String host, int port) {
            this.host = host;
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public Integer getPort() {
            return port;
        }
    }
}
