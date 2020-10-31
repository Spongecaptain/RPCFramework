package cool.spongecaptain.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * 供客户端使用
 */
public interface ServiceDiscovery {
    //发现一个服务：返回拥有该服务的地址
    InetSocketAddress lookForService(String serviceName);
    //服务中心删除 ServiceDiscovery 处的一个消息缓存
    void deleteCache(String serviceName);
    //服务中心通知 ServiceDiscovery 进行服务缓存的更新(修改)
    void updateCache(String serviceName, String address);
    //服务中心通知 ServiceDiscovery 进行服务缓存的增加(新加了一个服务)
    void addCache(String serviceName,String address);
}
