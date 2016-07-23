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
package com.karlsoft.wrapper.proxy.plain;

import com.google.common.net.HostAndPort;
import com.karlsoft.wrapper.api.AbstractService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Set up/shutdown plain proxy service.
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public final class PlainProxyService extends AbstractService {

    private static final Logger LOG
            = Logger.getLogger(PlainProxyService.class.getName());

    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final Integer localPort;
    private final HostAndPort targetServer;

    public PlainProxyService(String localPort, String remoteHost) {
        this.localPort = Integer.parseInt(localPort);
        this.targetServer = HostAndPort.fromString(remoteHost);
        serviceName = "Plain proxy";
    }

    @Override
    protected void startService() throws InterruptedException {
        LOG.log(Level.INFO, "Proxying *:{0} to {1}",
                new Object[]{localPort.toString(), targetServer});
        // Configure the bootstrap.
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new PlainProxyInitializer(targetServer))
                .childOption(ChannelOption.AUTO_READ, false)
                .bind(localPort).sync()
                .channel().closeFuture();
    }

    @Override
    protected void stopService() throws InterruptedException {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
