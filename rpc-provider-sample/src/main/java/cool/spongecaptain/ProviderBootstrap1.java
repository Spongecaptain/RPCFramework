package cool.spongecaptain;

import cool.spongecaptain.server.NettyRpcServer;

/**
 * 这是一个服务端 Provider 启动案例
 */
public class ProviderBootstrap1 {
    public static void main(String[] args) {
        NettyRpcServer server = new NettyRpcServer(8233);
        server.start();
    }
}
