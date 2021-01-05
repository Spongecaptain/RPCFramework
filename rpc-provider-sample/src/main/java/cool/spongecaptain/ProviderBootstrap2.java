package cool.spongecaptain;

import cool.spongecaptain.server.NettyRpcServer;

/**
 * 这是一个服务端 Provider 启动案例
 * 与 ProviderBootstrap1 的唯一区别是：作为另一个不同的服务提供方，它们之间的端口号不同
 */
public class ProviderBootstrap2 {
    public static void main(String[] args) {
        NettyRpcServer server = new NettyRpcServer(8666);
        server.start();
    }
}
