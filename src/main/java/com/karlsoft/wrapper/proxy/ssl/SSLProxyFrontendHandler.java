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

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
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
public class SSLProxyFrontendHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = Logger.getLogger(SSLProxyFrontendHandler.class.getName());

    private final String remoteHost;
    private final Integer remotePort;

    private volatile Channel outboundChannel;

    public SSLProxyFrontendHandler(String remoteHost, Integer remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
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
             .channel(NioSocketChannel.class)
             .handler(new SSLBackendInitializer(sslCtx, inboundChannel,
                        remoteHost, remotePort));
        
        ChannelFuture f = b.connect(remoteHost, remotePort);
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

    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) {
        if (outboundChannel.isActive()) {
            outboundChannel.writeAndFlush(msg)
                    .addListener((ChannelFutureListener) (ChannelFuture future) -> {
                        if (future.isSuccess()) {
                            // was able to flush out data, start to read the next chunk
                            ctx.channel().read();
                        } else {
                            future.channel().close();
                        }
                    });
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (outboundChannel != null) {
            closeOnFlush(outboundChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOG.log(Level.SEVERE, "SSLProxyFrontendHandler issue", cause);
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
