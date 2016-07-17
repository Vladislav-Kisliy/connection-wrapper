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

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import static io.netty.channel.ChannelFutureListener.CLOSE;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.HOST;
import static io.netty.handler.codec.http.HttpHeaderNames.USER_AGENT;
import static io.netty.handler.codec.http.HttpMethod.GET;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.handler.proxy.Socks4ProxyHandler;
import static io.netty.util.CharsetUtil.UTF_8;
import java.net.InetSocketAddress;

/**
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public class SocksTest {

    public static void main(String[] args) throws Exception {
        final String ua = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
        final String host = "forum.ru-board.com";
        final int port = 80;

        Bootstrap b = new Bootstrap();
        b.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addFirst(new Socks4ProxyHandler(new InetSocketAddress("103.224.29.26", 1080)));
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpContentDecompressor());
                        p.addLast(new HttpObjectAggregator(10_485_760));
                        p.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(final ChannelHandlerContext ctx) throws Exception {
                                HttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, GET, "/e.pl");
                                request.headers().set(HOST, host + ":" + port);
                                request.headers().set(USER_AGENT, ua);
                                request.headers().set(CONNECTION, CLOSE);

                                ctx.writeAndFlush(request);

                                System.out.println("!sent");
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println("!answer");
                                if (msg instanceof FullHttpResponse) {
                                    FullHttpResponse httpResp = (FullHttpResponse) msg;

                                    ByteBuf content = httpResp.content();
                                    String strContent = content.toString(UTF_8);
                                    System.out.println("body: " + strContent);

                                    return;
                                }

                                super.channelRead(ctx, msg);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                cause.printStackTrace(System.err);
                                ctx.close();
                            }
                        });
                    }
                });

        b.connect(host, port).awaitUninterruptibly();
        System.out.println("!connected");
    }

}
