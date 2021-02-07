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
package com.karlsoft.wrapper.proxy.ssl;

import com.google.common.net.HostAndPort;
import com.karlsoft.wrapper.proxy.base.BaseProxyFrontendHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLException;

/**
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public class SSLProxyFrontendHandler extends BaseProxyFrontendHandler {

    private static final Logger LOG = Logger.getLogger(SSLProxyFrontendHandler.class.getName());

    public SSLProxyFrontendHandler(HostAndPort targetServer) {
        super(targetServer);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws SSLException {
        final Channel inboundChannel = ctx.channel();
        // Configure SSL.
        SslContext sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        LOG.log(Level.INFO, "SSLProxyFrontendHandler set up ssl");
        // Start the connection attempt.
        Bootstrap b = new Bootstrap();
        b.group(inboundChannel.eventLoop())
                .channel(ctx.channel().getClass())
                .handler(new SSLBackendInitializer(sslCtx, inboundChannel,
                        targetServer))
                .option(ChannelOption.AUTO_READ, false);
        
        ChannelFuture f = b.connect(targetServer.getHost(), targetServer.getPort());
        outboundChannel = f.channel();
        f.addListener((ChannelFutureListener) (ChannelFuture future) -> {
            if (future.isSuccess()) {
                // connection complete start to read first data
                LOG.log(Level.SEVERE, "SSLProxyFrontendHandler connect success");
                inboundChannel.read();
            } else {
                // Close the connection if the connection attempt has failed.
                LOG.log(Level.SEVERE, "SSLProxyFrontendHandler connect failure");
                inboundChannel.close();
            }
        });
    }
}
