/*
 * Copyright (C) 2016 vlad
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
package com.karlsoft.wrapper.proxy.multi;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.karlsoft.wrapper.proxy.plain.*;
import com.karlsoft.wrapper.api.AbstractService;
import com.karlsoft.wrapper.proxy.hex.MultiplierServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Set up/shutdown plain proxy service.
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public final class MultiplierProxyService extends AbstractService {

    private static final Logger LOG
            = Logger.getLogger(MultiplierProxyService.class.getName());

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final Integer localPort;
    private final List<HostAndPort> hosts = new LinkedList<>();

    public MultiplierProxyService(String localPort, Object stringArray) {
        Preconditions.checkArgument(stringArray instanceof String[]);
        this.localPort = Integer.parseInt(localPort);
        serviceName = "Multiplier proxy";
        Lists.newArrayList((String[]) stringArray).stream().forEach((host) -> {
            hosts.add(HostAndPort.fromString(host));
        });
        System.out.println("list ="+hosts);
    }

    @Override
    protected void startService() throws InterruptedException {
        LOG.log(Level.INFO, "Proxying *:{0} to {1}:{2}",
                new Object[]{localPort.toString()});
        // Configure the server.
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MultiplierProxyInitializer(hosts));
            // Start the server.
            ChannelFuture f = b.bind(localPort).sync();
            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            stopService();
        }
    }

    @Override
    protected void stopService() throws InterruptedException {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
