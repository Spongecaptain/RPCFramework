package cool.spongecaptain.client;

import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceDiscovery;
import cool.spongecaptain.transport.client.ChannelProvider;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

/**
 * 这个类主要负责的任务有：
 *
 * 1. 得到 request 实例中的接口名，通过接口名查询提供此服务的 Provider 的地址
 * 2. 向 ChannelProvider 索要已连接状态的 Channel，然后发送此 Requst 实例
 * 3. 等待 RPC 的完成，阻塞时间包括（网络传输时间、序列化与反序列化等 CPU 时间、RPC 方法调用时间）
 * 4. 返回 RPC 调用过程中的响应结果
 *
 */
public class NettyRpcClient implements RpcClient {

    //服务发现组件
    private ServiceDiscovery serviceDiscovery;
    //ChannelProvider 实例实际上依赖于 NettyClient 完成工作
    private ChannelProvider channelProvider;

    public NettyRpcClient(ServiceDiscovery serviceDiscovery, ChannelProvider channelProvider) {
        this.serviceDiscovery = serviceDiscovery;
        this.channelProvider = channelProvider;
    }

    @Override
    public Object sendResponse(RpcRequest request) {
        //1. 得到接口的完全限定名，也就是服务名
        String serviceName = request.getInterfaceName();
        //2. 进行服务的查询
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookForService(serviceName);
        //3. 利用 ChannelProvider 进行消的传输（其底层依赖于 NettyClient）
        Channel channel = channelProvider.getChannel(inetSocketAddress);

        ChannelFuture channelFuture = channel.writeAndFlush(request);
        Object result =null;
        try {
             result = channelFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }
}

