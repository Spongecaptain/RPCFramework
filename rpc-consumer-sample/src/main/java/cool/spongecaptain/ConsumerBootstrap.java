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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 这是一个客户端启动类，用于演示 Consumer 如何进行 RPC 调用
 */
public class ConsumerBootstrap {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        NettyRpcClient nettyRpcClient = new NettyRpcClient(new ZKServiceDiscovery(), new ChannelProvider(nettyClient));
        RpcClientConfig rpcClientConfig = new RpcClientConfig("1", "1");
        RpcProxy rpcProxy = new RpcProxy(nettyRpcClient, rpcClientConfig);
        SayHelloInterface sayHello = (SayHelloInterface) rpcProxy.newProxyInstance(SayHelloInterface.class);

        for (int i = 0; i < 10; i++) {
            //测试 SayHelloInterface#sayHello 方法
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String result = sayHello.sayHello("spongecaptain " + i);
            System.out.println("From ConsumerBootstrap: sayHello result: " + result);
        }

        AddInterface add = (AddInterface) rpcProxy.newProxyInstance(AddInterface.class);

        for (int i = 0; i < 10; i++) {
            //测试 AddInterface#add 方法
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int addResult = add.add(i - 1, i);
            System.out.println("From ConsumerBootstrap: add result: " + addResult);
        }

    }
}

