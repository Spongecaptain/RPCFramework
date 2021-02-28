package cool.spongecaptain;

import cool.spongecaptain.api.AddInterface;
import cool.spongecaptain.api.SayHelloInterface;
import cool.spongecaptain.client.NettyRpcClient;
import cool.spongecaptain.client.RpcClientConfig;
import cool.spongecaptain.loadbalance.random.WeightRandomLoadBalance;
import cool.spongecaptain.proxy.RpcProxy;
import cool.spongecaptain.registry.zk.ZKServiceDiscovery;
import cool.spongecaptain.transport.client.ChannelProvider;
import cool.spongecaptain.transport.client.NettyClient;

/**
 * 这是一个客户端启动类，用于演示 Consumer 如何进行 RPC 调用
 */
public class ConsumerBootstrap {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        //这里默认使用一个权重随机算法，还可以采用其他算法，例如：一致性 Hash、简单随机、轮询，因为默认权重都是 1 ，因此两个服务端各自收到的请求数大约各为 1000 个
        NettyRpcClient nettyRpcClient = new NettyRpcClient(new ZKServiceDiscovery(), new ChannelProvider(nettyClient),new WeightRandomLoadBalance());


        RpcClientConfig rpcClientConfig = new RpcClientConfig("1","1");
        RpcProxy rpcProxy = new RpcProxy(nettyRpcClient,rpcClientConfig);


        //这里进行测试负载均衡算法

        //分别进行 10 次测试

        for (int i = 0; i < 10; i++) {
            //测试 SayHelloInterface#sayHello 方法
            SayHelloInterface sayHello = (SayHelloInterface)rpcProxy.newProxyInstance(SayHelloInterface.class);
            String result = sayHello.sayHello("spongecaptain " + i);
            System.out.println("From ConsumerBootstrap: sayHello result: "+result);
        }


        for (int i = 0; i < 10; i++) {
            //测试 AddInterface#add 方法
            AddInterface add = (AddInterface)rpcProxy.newProxyInstance(AddInterface.class);
            int addResult = add.add(i-1, i);
            System.out.println("From ConsumerBootstrap: add result: "+addResult);
        }

    }
}

