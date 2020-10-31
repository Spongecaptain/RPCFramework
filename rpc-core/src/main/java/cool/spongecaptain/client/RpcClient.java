package cool.spongecaptain.client;

import cool.spongecaptain.protocol.RpcRequest;

/**
 * RpcClient 接口的作用非常简单，就是能够发送一个 RPC 消息，然后得到 RPC 响应中的结果
 */
public interface RpcClient {
    Object sendResponse(RpcRequest request);
}
