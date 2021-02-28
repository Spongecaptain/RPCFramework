package cool.spongecaptain.transport.server;

import cool.spongecaptain.exception.RpcException;
import cool.spongecaptain.serialize.kyro.KryoSerialization;
import cool.spongecaptain.transport.codec.ByteDecoder;
import cool.spongecaptain.transport.codec.MessageEncoder;
import cool.spongecaptain.transport.server.handler.HeatBeatRequestHandler;
import cool.spongecaptain.transport.server.handler.RpcRequestHandler;
import cool.spongecaptain.transport.server.handler.ServerIdleHandler;
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

    public  final int PORT;

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    public static final int SERVER_IDLE_TIME = 30;

    public NettyServer(int port) {

        this.PORT = port;


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
                        //Decoder 负责将 ByteBuf 的字节数据转换为 RpcRequest or Rpc 实例
                        ch.pipeline().addLast(new ByteDecoder(new KryoSerialization()));
                        //idle 检测
                        ch.pipeline().addLast(new IdleStateHandler(0,0,SERVER_IDLE_TIME, TimeUnit.SECONDS));
                        //响应 Idle 事件，关闭连接
                        ch.pipeline().addLast(ServerIdleHandler.getServerIdleHandler());
                        //RPCRequestHandler 负责处理 RpcRequest 对应的 RPC 方法，执行后产生对应的 RPCResponse 实例返回
                        ch.pipeline().addLast(RpcRequestHandler.getRpcRequestHandler());
                        //Encoder 负责将 RPCResponse 转换为 ByteBuf，然后再向前传播
                        ch.pipeline().addLast(new MessageEncoder(new KryoSerialization()));
                        //心跳处理 Handler
                        ch.pipeline().addLast(HeatBeatRequestHandler.getHeartBeatTimerHandler());
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
