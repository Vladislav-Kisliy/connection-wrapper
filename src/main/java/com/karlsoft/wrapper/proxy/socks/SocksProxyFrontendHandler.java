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

import com.google.common.net.HostAndPort;
import com.karlsoft.wrapper.proxy.base.BaseProxyFrontendHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

/**
 * Creates a new channel for every client
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public class SocksProxyFrontendHandler extends BaseProxyFrontendHandler {

    private static final Logger LOG = Logger.getLogger(SocksProxyFrontendHandler.class.getName());
    private final HostAndPort socksProxy;
    private final Mode serviceMode;

    public SocksProxyFrontendHandler(HostAndPort socksProxy, HostAndPort targetServer,
            Mode serviceMode) {
        super(targetServer);
        this.socksProxy = socksProxy;
        this.serviceMode = serviceMode;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws SSLException {
        final Channel inboundChannel = ctx.channel();
        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new SocksBackendInitializer(inboundChannel, socksProxy,
                        serviceMode))
                .option(ChannelOption.AUTO_READ, false);

        ChannelFuture f = b.connect(targetServer.getHostText(), targetServer.getPort());
        outboundChannel = f.channel();
        f.addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (future.isSuccess()) {
                // connection complete start to read first data
                LOG.log(Level.SEVERE, "SocksProxyFrontendHandler connect success");
                inboundChannel.read();
            } else {
                // Close the connection if the connection attempt has failed.
                LOG.log(Level.SEVERE, "SocksProxyFrontendHandler connect failure");
                inboundChannel.close();
            }
        });
    }
}
