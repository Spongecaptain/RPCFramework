package cool.spongecaptain.transport.client.handler;

import cool.spongecaptain.protocol.RpcResponse;
import cool.spongecaptain.transport.client.UnprocessedRequests;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class RpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse<Object>> {

    //单例模式
    public static volatile RpcResponseHandler rpcResponseHandler;

    public RpcResponseHandler(){};

    public static RpcResponseHandler getRpcResponseHandler() {
        if(rpcResponseHandler==null){
            synchronized (RpcResponseHandler.class){
                if(rpcResponseHandler==null){
                    rpcResponseHandler = new RpcResponseHandler();
                }
            }
        }
        return rpcResponseHandler;
    }



    private static final Logger logger = LoggerFactory.getLogger(RpcResponseHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext chx, RpcResponse<Object> response) throws Exception {
        //可能会因为消息重发而出现多个同一个 requestID  RPC 响应
        logger.info("complete one RPC Response, the result is {} from {}",response.getBody(),chx.channel().remoteAddress());
        //通知异步响应已经接收到
        UnprocessedRequests.complete(response);
    }
}
