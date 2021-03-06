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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class SocksProxyInitializer extends ChannelInitializer<SocketChannel> {

    private final HostAndPort socksProxy;
    private final HostAndPort targetHost;
    private final Mode serviceMode;

    public SocksProxyInitializer(HostAndPort socksProxy, HostAndPort targetHost,
            Mode serviceMode) {
        this.socksProxy = socksProxy;
        this.targetHost = targetHost;
        this.serviceMode = serviceMode;
    }


    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(
//                new LoggingHandler(LogLevel.INFO),
                new SocksProxyFrontendHandler(socksProxy, targetHost, serviceMode));
    }
}
