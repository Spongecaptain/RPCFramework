package cool.spongecaptain.transport.client;

import cool.spongecaptain.exception.RpcException;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * ChannelProvider 类的作用非常大，因为我们要借助于 Channel 进行写事件的传播
 * ChannelProvider 依赖于 NettyClient 提供 TCP 底层连接的支持
 */
public class ChannelProvider {
    //key 为 InetAddress#toString，value 为缓存的 Channel
    private  Map<String, Channel> channelCache;
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private NettyClient nettyClient;
    private final int RETRY_TIMES = 5;

    public ChannelProvider(NettyClient nettyClient) {
        channelCache = new ConcurrentHashMap<>();
        this.nettyClient = nettyClient;
    }


     public  Channel getChannel(InetSocketAddress inetSocketAddress){
         String key = inetSocketAddress.toString();

         Channel channel = channelCache.get(key);
         //如果 Channel 被缓存且存活，那么直接返回
         if(channel!=null&&channel.isActive()){
             return channel;
         }
         //移除无效缓存
         channelCache.remove(key);
         try {
             //重连接
             channel = nettyClient.doConnect(inetSocketAddress, RETRY_TIMES);
             //更新缓存
             channelCache.put(key,channel);
         } catch (ExecutionException | InterruptedException e) {
             logger.warn("the client connect to [{}] has failed", inetSocketAddress.toString());
             throw new RpcException("connect fail");
         }
         return channel;
     }
}
