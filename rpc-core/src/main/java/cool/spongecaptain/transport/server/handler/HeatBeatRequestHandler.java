package cool.spongecaptain.transport.server.handler;

import cool.spongecaptain.protocol.RpcHeartBeatRequest;
import cool.spongecaptain.protocol.RpcHeartBeatResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端处理客户端心跳包处理器
 */
public class HeatBeatRequestHandler extends SimpleChannelInboundHandler<RpcHeartBeatRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HeatBeatRequestHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcHeartBeatRequest msg) throws Exception {
        //服务端接收到来自客户端心跳包时，其简单返回一个心跳响应
        logger.info("server get one heartbeat");
        ctx.writeAndFlush(new RpcHeartBeatResponse());
    }
}
