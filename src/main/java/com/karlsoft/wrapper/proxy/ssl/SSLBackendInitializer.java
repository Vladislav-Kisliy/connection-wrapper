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

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public class SSLBackendInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final Channel inboundChannel;
    private final String remoteHost;
    private final Integer remotePort;

    public SSLBackendInitializer(SslContext sslCtx, Channel inboundChannel, String remoteHost, Integer remotePort) {
        this.sslCtx = sslCtx;
        this.inboundChannel = inboundChannel;
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add SSL handler first to encrypt and decrypt everything.
        // In this example, we use a bogus certificate in the server side
        // and accept any invalid certificates in the client side.
        // You will need something more complicated to identify both
        // and server in the real world.
        pipeline.addLast(sslCtx.newHandler(ch.alloc(), remoteHost, remotePort));
//        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
        // and then business logic.
        pipeline.addLast(new SSLProxyBackendHandler(inboundChannel));
    }
}
