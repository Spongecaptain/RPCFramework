package cool.spongecaptain.transport.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端 IdleHandler
 * 发生 idle 事件则直接关闭
 */
@ChannelHandler.Sharable
public class ServerIdleHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(ServerIdleHandler.class);

    //单例模式

    private static volatile ServerIdleHandler serverIdleHandler;

    private ServerIdleHandler(){}

    public static ServerIdleHandler getServerIdleHandler() {
        if(serverIdleHandler==null){
            synchronized (ServerIdleHandler.class){
                if(serverIdleHandler==null){
                    serverIdleHandler = new ServerIdleHandler();
                }
            }
        }
        return serverIdleHandler;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            logger.info(ctx.channel().remoteAddress() + " is idle, close the channel");
            Channel channel = ctx.channel();
            if(channel.isActive()){
                channel.close();
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }
}
