package cool.spongecaptain.transport.server.handler;

import cool.spongecaptain.handler.RequestHandler;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RPCRequestHandler 类用于处理向后传播的 RPCRequest
 *
 */
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequest.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        logger.info("get one RPC Request");
        //然后调用 RequestHandler#handleRequest 方法来处理此 RPCRequest，最终得到 RPCResponse 实例
        RpcResponse rpcResponse = RequestHandler.handleRequest(rpcRequest);
        //然后将 RPCResponse 从 TailContext 向前传播
        channelHandlerContext.channel().writeAndFlush(rpcResponse);

    }
}
