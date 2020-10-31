package cool.spongecaptain.transport.client.handler;

import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse<Object>> {

    private static final Logger logger = LoggerFactory.getLogger(RpcResponseHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse<Object> response) throws Exception {
        logger.info("get one RPC Response");
        System.out.println(response.getBody());
    }
}
