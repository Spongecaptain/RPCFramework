package cool.spongecaptain.proxy;

import cool.spongecaptain.client.NettyRpcClient;
import cool.spongecaptain.client.RpcClientConfig;
import cool.spongecaptain.protocol.RpcRequest;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这个类应该提供如下的功能：
 * 1. 客户端向该类提供一个接口的完全类限定名，该类能够返回一个对应的代理类实例，代理类实例能够提供接口的 RPC 方法实现
 * 2. 代理类依赖于底层的 NettyRpcClient 进行 RPC 式的调用
 *
 * 完整的依赖关系是：
 * RpcProxy 依赖于 NettyRpcClient 进行 RpcRequest 的发送以及 RpcResponse 的接收
 * NettyRpcClient 依赖于 ChannelProvider 实例得到 io.netty.channel.Channel 实例进行消息的发送
 * ChannelProvider 依赖于 NettyClient 来维护与服务端的 Channel 连接
 *
 * 这里代理逻辑通过 CGLIB 实现
 */
public class RpcProxy  implements MethodInterceptor {
    private static AtomicInteger count = new AtomicInteger(1);
    private NettyRpcClient nettyRpcClient;
    private RpcClientConfig rpcClientConfig;

    public RpcProxy(NettyRpcClient nettyRpcClient, RpcClientConfig rpcClientConfig) {
        this.nettyRpcClient = nettyRpcClient;
        this.rpcClientConfig = rpcClientConfig;
    }

    private final Enhancer enhancer = new Enhancer();

    //入口参数的含义依次为：(被)代理的对象、代理方法、方法参数、代理方法（非反射实现版本）
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        RpcRequest.Builder requestBuilder = new RpcRequest.Builder(String.valueOf(count.getAndIncrement()),method.getDeclaringClass().getName(),method.getName());

        RpcRequest request = requestBuilder.setVersion(rpcClientConfig.getVersion()).setGroup(rpcClientConfig.getGroup()).setParamTypes(method.getParameterTypes()).setParameters(args).build();

        Object result = nettyRpcClient.sendRequest(request);

        return result;
    }

    public Object newProxyInstance(Class<?> clazz) {
        //设置产生的代理对象的父类，也就是被代理对象（被增强对象）
        enhancer.setSuperclass(clazz);
        //设置代理类的代理方法（代理方法作为回调封装在 MethodInterceptor 实例中）
        enhancer.setCallback(this);
        //使用默认无参数的构造函数创建目标对象
        return enhancer.create();
    }




}
