package cool.spongecaptain.transport.server.handler;

import cool.spongecaptain.protocol.RpcHeartBeatRequest;
import cool.spongecaptain.protocol.RpcHeartBeatResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端处理客户端心跳包处理器
 */
@ChannelHandler.Sharable
public class HeatBeatRequestHandler extends SimpleChannelInboundHandler<RpcHeartBeatRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HeatBeatRequestHandler.class);
    //单例模式
    private static volatile HeatBeatRequestHandler heartBeatTimerHandler;

    private HeatBeatRequestHandler(){}

    public static HeatBeatRequestHandler getHeartBeatTimerHandler() {
        if(heartBeatTimerHandler==null){
            synchronized (HeatBeatRequestHandler.class){
                if(heartBeatTimerHandler==null){
                    heartBeatTimerHandler = new HeatBeatRequestHandler();
                }
            }
        }
        return heartBeatTimerHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcHeartBeatRequest msg) throws Exception {
        //服务端接收到来自客户端心跳包时，其简单返回一个心跳响应
        logger.info("server get one heartbeat from "+ctx.channel().remoteAddress());
        ctx.channel().writeAndFlush(new RpcHeartBeatResponse());
    }
}
