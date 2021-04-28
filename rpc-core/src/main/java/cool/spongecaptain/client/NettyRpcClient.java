package cool.spongecaptain.client;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.loadbalance.random.RandomLoadBalance;
import cool.spongecaptain.loadbalance.random.WeightRandomLoadBalance;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.protocol.RpcResponse;
import cool.spongecaptain.registry.ServiceDiscovery;
import cool.spongecaptain.registry.ServiceInfo;
import cool.spongecaptain.transport.client.ChannelProvider;
import cool.spongecaptain.transport.client.UnprocessedRequests;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.nio.ch.Net;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 这个类主要负责的任务有：
 * <p>
 * 1. 得到 request 实例中的接口名，通过接口名查询提供此服务的 Provider 的地址
 * 2. 向 ChannelProvider 索要已连接状态的 Channel，然后发送此 Requst 实例
 * 3. 等待 RPC 的完成，阻塞时间包括（网络传输时间、序列化与反序列化等 CPU 时间、RPC 方法调用时间）
 * 4. 返回 RPC 调用过程中的响应结果
 */
public class NettyRpcClient implements RpcClient {
    public final static int TIME_OUT = 3000;//默认超时时间设置为 3000 ms
    public final static int RESEND_TIMES = 3;//默认重试 3 次
    //服务发现组件
    private ServiceDiscovery serviceDiscovery;
    //ChannelProvider 实例实际上依赖于 NettyClient 完成工作
    private ChannelProvider channelProvider;

    private static Logger logger = LoggerFactory.getLogger(NettyRpcClient.class);

    //负载均衡器
    private LoadBalance loadBalance;

    public NettyRpcClient(ServiceDiscovery serviceDiscovery, ChannelProvider channelProvider) {
        this.serviceDiscovery = serviceDiscovery;
        this.channelProvider = channelProvider;
        try {
            this.loadBalance = initLoadBalance();
        } catch (Exception e) {
            System.err.println("please write correct loadBalance properties");
            this.loadBalance = new RandomLoadBalance();
        }
    }

    private LoadBalance initLoadBalance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String f = "consumer.properties";
        Properties properties = new Properties();
        InputStream in = NettyRpcClient.class.getClassLoader().getResourceAsStream("consumer.properties");
        String loadbalance = null;
        try {
            properties.load(in);
            loadbalance = properties.getProperty("loadbalance", "cool.spongecaptain.loadbalance.random.RandomLoadBalance");
            logger.info("loadbalance is " + loadbalance);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Class clazz = Class.forName(loadbalance);

        Object object = clazz.newInstance();

        return (LoadBalance) object;
    }

    /**
     * 这是一个同步阻塞方法，当客户端调用时，阻塞直到服务端将相关 RPC 请求的响应返回给客户端
     *
     * @param request
     * @return
     */
    @Override
    public Object sendRequest(RpcRequest request) {
        //1. 得到接口的完全限定名，也就是服务名
        String serviceName = request.getInterfaceName();
        //2. 进行服务的查询
        List<ServiceInfo> serviceList = serviceDiscovery.lookForService(serviceName);
        String address = loadBalance.getServerAddress(request, serviceList);

        //将 localhost:8233 的地址分别得到 hostName 以及端口

        String[] split = address.split(":");

        String host = split[0];
        String port = split[1];

        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, Integer.parseInt(port));

        //3. 利用 ChannelProvider 进行消息的传输（其底层依赖于 NettyClient）

        Object result = doSend(inetSocketAddress, request, RESEND_TIMES);

        return result;

    }


    private Object doSend(InetSocketAddress inetSocketAddress, RpcRequest request, int resendTimes) {
        if (resendTimes < 1) {
            return null;
        }
        Channel channel = channelProvider.getChannel(inetSocketAddress);
        //4. 得到 RpcRequest 请求对应的处理结果：注意 RpcResponse 并不会通过 ChannelFuture 返回，而是通过另一个异步过程，因此需要 UnprocessedRequests 类的帮助
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();

        Object result = null;
        UnprocessedRequests.put(request, resultFuture);

        channel.writeAndFlush(request).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                //注意，这里仅仅是 Consumer 向 Provider 成功完成了请求的发送，但是并没有收到 response
                logger.info("client send message: [{}]", request);
            } else {
                resultFuture.completeExceptionally(future.cause());
                logger.error("Send failed:", future.cause());
            }
        });

        try {
            //超时等待
            int time = TIME_OUT << (RESEND_TIMES - resendTimes);
            result = resultFuture.get(time,TimeUnit.MILLISECONDS).getBody();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Resend a request");
            //重试
            doSend(inetSocketAddress, request, resendTimes - 1);
        }
        return result;
    }
}

