package cool.spongecaptain.registry;

/**
 * 服务注册接口
 * 供服务端使用
 *
 */
public interface ServiceRegistry {
    /**
     * @param serviceName 服务名，对应接口的完全限定名，例如：cool.spongecaptain.Echo
     * @param address 服务地址，对应于一个 "IP 地址 + 端口" 的字符串
     */
    void registerService(String serviceName, String address);
    //递归删除一个注册的服务
    void deleteService(String serviceName);

}
