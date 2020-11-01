package cool.spongecaptain;

import cool.spongecaptain.api.AddInterface;
import cool.spongecaptain.api.SayHelloInterface;
import cool.spongecaptain.client.NettyRpcClient;
import cool.spongecaptain.client.RpcClientConfig;
import cool.spongecaptain.proxy.RpcProxy;
import cool.spongecaptain.registry.ServiceDiscovery;
import cool.spongecaptain.registry.zk.ZKServiceDiscovery;
import cool.spongecaptain.transport.client.ChannelProvider;
import cool.spongecaptain.transport.client.NettyClient;

/**
 * 这是一个客户端启动类，用于演示 Consumer 如何进行 RPC 调用
 */
public class ConsumerBootstrap {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        ChannelProvider channelProvider = new ChannelProvider(nettyClient);
        ServiceDiscovery serviceDiscovery = new ZKServiceDiscovery();
        NettyRpcClient nettyRpcClient = new NettyRpcClient(serviceDiscovery, channelProvider);
        RpcClientConfig rpcClientConfig = new RpcClientConfig("1","1");
        RpcProxy rpcProxy = new RpcProxy(nettyRpcClient,rpcClientConfig);

        //1.测试 SayHelloInterface#sayHello 方法
        SayHelloInterface sayHello = (SayHelloInterface)rpcProxy.newProxyInstance(SayHelloInterface.class);
        String result = sayHello.sayHello("spongecaptain");
        System.out.println("From ConsumerBootstrap: sayHello result: "+result);

        //2.测试 AddInterface#add 方法
        AddInterface add = (AddInterface)rpcProxy.newProxyInstance(AddInterface.class);
        int addResult = add.add(1, 2);
        System.out.println("From ConsumerBootstrap: add result: "+addResult);
    }
}

