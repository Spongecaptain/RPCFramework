package cool.spongecaptain.hearbeat;

import cool.spongecaptain.api.SayHelloInterface;
import cool.spongecaptain.client.NettyRpcClient;
import cool.spongecaptain.client.RpcClientConfig;
import cool.spongecaptain.loadbalance.random.WeightRandomLoadBalance;
import cool.spongecaptain.proxy.RpcProxy;
import cool.spongecaptain.registry.zk.ZKServiceDiscovery;
import cool.spongecaptain.transport.client.ChannelProvider;
import cool.spongecaptain.transport.client.NettyClient;

public class IdleClient {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();
        NettyRpcClient nettyRpcClient = new NettyRpcClient(new ZKServiceDiscovery(), new ChannelProvider(nettyClient));


        RpcClientConfig rpcClientConfig = new RpcClientConfig("1","1");
        RpcProxy rpcProxy = new RpcProxy(nettyRpcClient,rpcClientConfig);

        SayHelloInterface sayHello = (SayHelloInterface)rpcProxy.newProxyInstance(SayHelloInterface.class);
        String result = sayHello.sayHello("spongecaptain " + 123);
        System.out.println("From ConsumerBootstrap: sayHello result: "+result);

        //Deliberately blocked
        while(true){

        }




    }
}
