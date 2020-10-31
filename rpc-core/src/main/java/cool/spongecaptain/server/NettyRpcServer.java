package cool.spongecaptain.server;

/**
 * TODO 下面的逻辑有待完成
 * 基于 NettyServer 的服务端启动，启动逻辑包括：
 * 1. 进行服务的扫描
 * 2. 将服务注册到注册中心
 * 3. 初始化 cool.spongecaptain.handler.RequestHandler 实例，后者用于为客户端的 RPC 请求进行响应
 * 4. 启动 NettyServer 提供 RPC 服务
 */
public class NettyRpcServer  implements RpcServer{
    @Override
    public void start() {



    }
}
