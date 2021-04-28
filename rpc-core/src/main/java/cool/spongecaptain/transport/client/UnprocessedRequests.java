package cool.spongecaptain.transport.client;

import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.protocol.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于存放未完成响应的 RpcRequest <-----> RpcResponse 对
 */
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();
    private static final Map<String, RpcRequest> IN_FLIGHT_REQUEST = new ConcurrentHashMap<>();
    public static void put(RpcRequest request, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(request.getRequestId(), future);
        IN_FLIGHT_REQUEST.put(request.getRequestId(),request);
    }

    public static void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        IN_FLIGHT_REQUEST.remove(rpcResponse.getRequestId());
        if (null != future) {
            //将网络 I/O 的处理响应放到 Future 中
            future.complete(rpcResponse);
        } else {
            //说明此 Response 来自于重试的响应
        }
    }
}