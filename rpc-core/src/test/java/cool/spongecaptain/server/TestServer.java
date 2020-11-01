package cool.spongecaptain.server;

public class TestServer {
    private static final int PORT = 8233;
    public static void main(String[] args) {
        NettyRpcServer nettyRpcServer = new NettyRpcServer(PORT);
        nettyRpcServer.start();
    }
}
