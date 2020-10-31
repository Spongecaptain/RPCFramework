package cool.spongecaptain.transport.client;

import cool.spongecaptain.serialize.kyro.KryoSerialization;
import cool.spongecaptain.transport.client.handler.RpcResponseHandler;
import cool.spongecaptain.transport.codec.ByteDecoder;
import cool.spongecaptain.transport.codec.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NettyClient {
    private static final int MAX_RETRY = 5;//最大重连次数设置为 5

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private NioEventLoopGroup workerGroup;

    private Bootstrap bootstrap;

    public NettyClient() {
        workerGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(
                        new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel ch) throws Exception {
                                //Client-in1/out2：用于长时间未通信后的 TCP 关闭
                                ch.pipeline().addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                                //Client-in2：Decoder：负责将 ByteBuf 转换为 RPCResponse 实例
                                ch.pipeline().addLast(new ByteDecoder(new KryoSerialization()));
                                //Client-in3：负责 RpcResponse 的处理，包括服务端对 request 中指定方法的调用
                                //TODO 这里先简单地在 ChannelInboundHandler 中在控制台打印出相关结果
                                ch.pipeline().addLast(new RpcResponseHandler());
                                //out1：Encoder RPCRequest 转为 ByteBuf 后向前传播
                                ch.pipeline().addLast(new MessageEncoder(new KryoSerialization()));

                            }
                        }
                );
    }


    //这个方法实际上会因为 TCP 连接的建立而阻塞，直到对应的连接建立是否成功后才会返回 Channel 实例
    //最大重连次数设置为 5，随着重连次数增多，重连操作的间隔也会逐渐增大
    public Channel doConnect(InetSocketAddress inetSocketAddress, int retry) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            //连接成功，那么返回
            if (future.isSuccess()) {
                logger.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
                //重连次数用完，那么抛出一个异常
            } else if (retry == 0) {
                throw new IllegalStateException();
            } else {
                //第几次重连
                int order = MAX_RETRY - retry + 1;
                //本次重连的间隔
                int delay = 1 << order;
                logger.info("the client connect to [{}] has failed the [{}] times", inetSocketAddress.toString(), order);
                bootstrap.config().group().schedule(() -> doConnect(inetSocketAddress, retry - 1), delay, TimeUnit.SECONDS);
            }

        });
        return completableFuture.get();
    }

}