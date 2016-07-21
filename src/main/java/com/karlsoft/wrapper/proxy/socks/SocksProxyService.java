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
package com.karlsoft.wrapper.proxy.socks;

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
import javax.net.ssl.SSLException;

/**
 * Simple SSL chat client modified from {@link TelnetClient}.
 */
public final class SocksProxyService  extends AbstractService {

    private static final Logger LOG = Logger.getLogger(SocksProxyService.class.getName());
    
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final HostAndPort socksProxy;
    private final HostAndPort targetServer;
    private final Integer localPort;

    public SocksProxyService(String localPort, String socksHostPort, String targetHostPort) {
        this.localPort = Integer.parseInt(localPort);
        this.socksProxy = HostAndPort.fromString(socksHostPort);
        this.targetServer = HostAndPort.fromString(targetHostPort);
        serviceName = "Socks proxy";
    }
    
    @Override
    protected void startService() throws SSLException, InterruptedException {
        LOG.log(Level.INFO, "Proxying *:{0} to {1}", 
                new Object[]{localPort.toString(), targetServer});
        // Configure the bootstrap.
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new SocksProxyInitializer(socksProxy, targetServer))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(localPort).sync()
                    .channel().closeFuture().sync();
        } finally {
            stopService();
        }
    }

    @Override
    protected void stopService() throws InterruptedException {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
