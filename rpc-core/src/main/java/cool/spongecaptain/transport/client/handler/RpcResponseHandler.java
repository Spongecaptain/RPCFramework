package cool.spongecaptain.transport.client.handler;

import cool.spongecaptain.protocol.RpcResponse;
import cool.spongecaptain.transport.client.UnprocessedRequests;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse<Object>> {

    private static final Logger logger = LoggerFactory.getLogger(RpcResponseHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse<Object> response) throws Exception {
        logger.info("complete one RPC Response, the result is {}",response.getBody());
        //通知异步响应已经接收到
        UnprocessedRequests.complete(response);
    }
}
