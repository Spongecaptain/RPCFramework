package cool.spongecaptain.remote;

import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.transport.client.NettyClient;
import cool.spongecaptain.transport.server.NettyServer;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

public class ClientBootstrap {
    public static void main(String[] args) {
        NettyClient nettyClient = new NettyClient();

        try {
            Channel channel = nettyClient.doConnect(new InetSocketAddress("localhost", NettyServer.PORT), 5);

            RpcRequest.Builder builder = new RpcRequest.Builder("17","foo","bar");
            RpcRequest request = builder.build();

            for (int i = 0; i < 5; i++) {
                //间隔一秒，发送一次
                Thread.sleep(1000);
                channel.writeAndFlush(request);
            }


        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
