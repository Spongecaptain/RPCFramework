package cool.spongecaptain.handler;

import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.protocol.RpcResponse;
import cool.spongecaptain.protocol.RpcRequestType;

/**
 * 工具类，为 Server 处理来自 Client 的消息服务
 */
public class RequestHandler {


    public static RpcResponse handleRequest(RpcRequest request){

        String requestId = request.getRequestId();
        String group = request.getGroup();
        String version = request.getVersion();
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Object[] parameters = request.getParameters();
        RpcRequestType rpcMessageType = request.getRpcMessageType();

        RpcResponse<Object> response = null;
        //这里我们的实现策略先简单一点，返回一个默认的 Response

        response= new RpcResponse<Object>(requestId,1,"well done","Hello");

        return response;
    }
}
