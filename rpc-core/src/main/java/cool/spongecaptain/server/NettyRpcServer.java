package cool.spongecaptain.server;

import cool.spongecaptain.handler.RequestHandler;
import cool.spongecaptain.registry.zk.ZKServiceRegistry;
import cool.spongecaptain.transport.server.NettyServer;

import java.util.Set;

/**
 * TODO 下面的逻辑有待完成
 * 基于 NettyServer 的服务端启动，启动逻辑包括：
 * 1. 进行服务的扫描
 * 2. 将服务注册到注册中心
 * 3. 初始化 cool.spongecaptain.handler.RequestHandler 实例，后者用于为客户端的 RPC 请求进行响应
 * 4. 启动 NettyServer 提供 RPC 服务
 */
public class NettyRpcServer implements RpcServer {

    private int port;//Provider 提供服务的端口

    public NettyRpcServer(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        //1. 服务扫描，非递归地扫描是为了仅仅扫描接口，而不扫描 cool.spongecaptain.api.impl 下的具体实现类
        Set<String> interfaces = ClassUtils.getClassName("cool.spongecaptain.api", false);
        //2. 扫描具体的服务实现类
        Set<String> impls = ClassUtils.getClassName("cool.spongecaptain.api.impl", false);
        //3. 我们需要进行具体的服务注册
        ZKServiceRegistry zkServiceRegistry = new ZKServiceRegistry();

        interfaces.forEach((String interfaceName) -> {
            zkServiceRegistry.registerService(interfaceName, "localhost:"+ port,1);
        });
        //4. 我们需要进行服务具体实现类的构造，然后将具体实例添加到 RequestHandler 实例中去
        impls.forEach((String impl) -> {
            String serviceName = null;
            //4.1 利用反射构造实例
            Class clazz = null;
            Object service = null;
            try {
                clazz = Class.forName(impl);
                service = clazz.newInstance();
                Class[] interfacesArray = clazz.getInterfaces();

                serviceName =interfacesArray[0].getName();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }

            //4.3 进行 Provider 端的服务实现类实例注册
            RequestHandler.initService(serviceName, service);
        });


        //5.启动 NettyServer 提供 RPC 服务
        NettyServer nettyServer = new NettyServer(port);
        nettyServer.start();

    }

}


