package cool.spongecaptain.handler;

import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.protocol.RpcResponse;
import cool.spongecaptain.protocol.RpcRequestType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具类，为 Server 处理来自 Client 的消息服务
 */
public class RequestHandler {
    //key 为 serviceName，value 为服务具体实现类实例
    private static ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<>();

    public static RpcResponse handleRequest(RpcRequest request) {

        String requestId = request.getRequestId();
        String group = request.getGroup();
        String version = request.getVersion();
        String interfaceName = request.getInterfaceName();
        String methodName = request.getMethodName();
        Class<?>[] paramTypes = request.getParamTypes();
        Object[] parameters = request.getParameters();
        RpcRequestType rpcMessageType = request.getRpcMessageType();

        RpcResponse<Object> response = null;

        //1. 我们从 cache 中获取提供该服务的实例
        Object service = cache.get(interfaceName);
        //2.结果
        Object result = null;
        try {
            //3.利用 RRC 请求中的方法名以及方法参数列表，向服务类 Class 得到 Method 实例
            Method method = service.getClass().getMethod(methodName, paramTypes);
            //4. 利用反射来执行 Method 实例的方法，参数依次为：服务实例、RPC 中的入口参数列表
            result = method.invoke(service, parameters);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        //利用 result 构造 RpcResponse 实例
        response = new RpcResponse<Object>(requestId, 1, "well done", result);
        return response;
    }

    public static void initService(String interfaceName, Object object) {
        cache.put(interfaceName, object);
    }

}
