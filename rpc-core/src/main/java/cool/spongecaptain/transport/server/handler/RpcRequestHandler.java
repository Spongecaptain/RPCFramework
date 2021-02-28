package cool.spongecaptain.transport.server.handler;

import cool.spongecaptain.handler.RequestHandler;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.protocol.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * RPCRequestHandler 类用于处理向后传播的 RPCRequest
 */
@ChannelHandler.Sharable
public class RpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcRequest.class);
    private static AtomicInteger count = new AtomicInteger(1);//rpc 计数器

    //单例模式
    private static RpcRequestHandler rpcRequestHandler;

    private RpcRequestHandler(){}

    public static RpcRequestHandler getRpcRequestHandler() {
        if (rpcRequestHandler == null) {
            synchronized (RpcRequestHandler.class) {
                if (rpcRequestHandler == null) {
                    rpcRequestHandler = new RpcRequestHandler();
                }
            }
        }
        return rpcRequestHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        int andIncrement = count.getAndIncrement();
        logger.info("get one RPC Request, count is " + andIncrement);
        //然后调用 RequestHandler#handleRequest 方法来处理此 RPCRequest，最终得到 RPCResponse 实例
        RpcResponse rpcResponse = RequestHandler.handleRequest(rpcRequest);
        //然后将 RPCResponse 从 TailContext 向前传播
        channelHandlerContext.channel().writeAndFlush(rpcResponse);

    }
}
