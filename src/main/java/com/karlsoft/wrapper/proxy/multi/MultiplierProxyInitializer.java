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
package com.karlsoft.wrapper.proxy.multi;

import com.google.common.net.HostAndPort;
import com.karlsoft.wrapper.proxy.base.BaseProxyFrontendHandler;
import com.karlsoft.wrapper.proxy.hex.MultiplierServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.List;

/**
 * Initializes connector to remote host.
 *
 * @author Vladislav Kislyi <vladislav.kisliy@gmail.com>
 */
public class MultiplierProxyInitializer extends ChannelInitializer<SocketChannel> {

    private final List<HostAndPort> hosts;

    public MultiplierProxyInitializer(List<HostAndPort> hosts) {
        this.hosts = hosts;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO),
                new MultiplierServerHandler(hosts));
    }
}
