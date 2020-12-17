package cool.spongecaptain.transport.client.handler;

import cool.spongecaptain.protocol.RpcHeartBeatRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 用于应用层对 TCP 长连接的保持
 * Client 定时向 Server 发送一个数据包
 */
public class HeartBeatTimerHandler extends ChannelInboundHandlerAdapter {

    private static final int HEARTBEAT_INTERVAL = 5;//心跳间隔

    private static final Logger logger = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
            registryHeartBeat(ctx);
            super.channelActive(ctx);
    }

    //私有方法，进行定时发送心跳包的逻辑
    private void registryHeartBeat(ChannelHandlerContext ctx){
        ctx.executor().schedule(()->{
            if(ctx.channel().isActive()){
                logger.info("client send one heartbeat");
                ctx.writeAndFlush(new RpcHeartBeatRequest());
                registryHeartBeat(ctx);
            }
        },HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
    }
}
