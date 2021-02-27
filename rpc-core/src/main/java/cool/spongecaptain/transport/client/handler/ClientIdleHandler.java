package cool.spongecaptain.transport.client.handler;

import cool.spongecaptain.protocol.RpcHeartBeatRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端 IdleHandler
 * 发生 idle 事件后尝试发送 hearbeat
 */

@ChannelHandler.Sharable
public class ClientIdleHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientIdleHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if(evt instanceof IdleStateEvent){
            logger.info(ctx.channel().remoteAddress() + " is idle, try send a heartbeat");

            Channel channel = ctx.channel();
            //注意要从 Pipeline Tail 开始向前传播，不要使用 ChannelHandlerContext#writeAndFlush 方法
            if(channel.isActive()){
                channel.writeAndFlush(new RpcHeartBeatRequest());
            }
            //如果 Channel 对应的 TCP 连接已经关闭了，那么无需做其他，TCP 重连在缓存层有逻辑负责提供
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
