/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.remoting.transport.dispatcher;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.remoting.ChannelHandler;
import org.apache.dubbo.remoting.Dispatcher;
import org.apache.dubbo.remoting.exchange.support.header.HeartbeatHandler;
import org.apache.dubbo.remoting.transport.MultiMessageHandler;

public class ChannelHandlers {

    private static ChannelHandlers INSTANCE = new ChannelHandlers();

    protected ChannelHandlers() {
    }

    public static ChannelHandler wrap(ChannelHandler handler, URL url) {
        return ChannelHandlers.getInstance().wrapInternal(handler, url);
    }

    protected static ChannelHandlers getInstance() {
        return INSTANCE;
    }

    static void setTestingChannelHandlers(ChannelHandlers instance) {
        INSTANCE = instance;
    }
    /**
     2      * 这里又是层层包裹：
     3      * MultiMessageHandler
     4      * --HeartbeatHandler
     5      *   --AllChannelHandler
     6      *     --DecodeHandler
     7      *       --HeaderExchangeHandler
     8      *         --ExchangeHandlerAdapter
     9      * @param handler
     10      * @param url
     11      * @return
     12      */
    protected ChannelHandler wrapInternal(ChannelHandler handler, URL url) {
        //ExtensionLoader.getExtensionLoader(Dispatcher.class).getAdaptiveExtension()获取到一个Dispatcher$Adaptive适配类。
        //Dispatcher$Adaptive.dispatch(DecodeHandler对象, providerUrl)获取到AllDispatcher
        //AllDispatcher.dispatch 获得AllChannelHandler，实例=先初始化父类的构造WrappedChannelHandler
        return new MultiMessageHandler(new HeartbeatHandler(ExtensionLoader.getExtensionLoader(Dispatcher.class)
                .getAdaptiveExtension().dispatch(handler, url)));
    }
}
