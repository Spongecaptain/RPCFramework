package cool.spongecaptain.remote;

import cool.spongecaptain.transport.server.NettyServer;

public class ServerBootstrap {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
    }
}
