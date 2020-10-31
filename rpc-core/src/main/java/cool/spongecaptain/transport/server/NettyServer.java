package cool.spongecaptain.transport.server;

import cool.spongecaptain.exception.RpcException;
import cool.spongecaptain.serialize.kyro.KryoSerialization;
import cool.spongecaptain.transport.codec.ByteDecoder;
import cool.spongecaptain.transport.codec.MessageEncoder;
import cool.spongecaptain.transport.server.handler.RpcRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NettyServer {

    ServerBootstrap bootstrap;

    EventLoopGroup bossGroup;

    EventLoopGroup workerGroup;

    public static final int PORT = 8233;

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public NettyServer() {
        bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.SO_BACKLOG,128)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //Server-in1/out2：用于长时间未通信后的 TCP 关闭
                        ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        //Server-in2：Decoder 负责将 ByteBuf 的字节数据转换为 RpcRequest 实例
                        ch.pipeline().addLast(new ByteDecoder(new KryoSerialization()));
                        //Server-in3：RPCRequestHandler 负责处理 RpcRequest 对应的 RPC 方法，执行后产生对应的 RPCResponse 实例返回
                        ch.pipeline().addLast(new RpcRequestHandler());
                        //Server-out1：Encoder 负责将 RPCResponse 转换为 ByteBuf，然后再向前传播
                        ch.pipeline().addLast(new MessageEncoder(new KryoSerialization()));
                    }
                });

    }

    public void start(){
        try {
            //同步等待 ServerSocketChannel 端口绑定完成
            ChannelFuture f = bootstrap.bind(PORT).sync();
            //等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error(e.toString());
            throw new RpcException(e.toString());
        }finally {
            logger.info("shutdown the EventLoopGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
