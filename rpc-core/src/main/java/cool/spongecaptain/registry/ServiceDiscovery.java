package cool.spongecaptain.registry;

/**
 * 服务发现接口
 * 供客户端使用
 * TODO 缓存记得使用 ConcurrentHashMap 因为存在异步更新的可能
 */
public interface ServiceDiscovery {
    //发现一个服务：返回拥有该服务的地址
    String lookForService(String serviceName);
    //服务中心删除 ServiceDiscovery 处的一个消息缓存
    void deleteCache(String serviceName);
    //服务中心通知 ServiceDiscovery 进行服务缓存的更新(修改)
    void updateCache(String serviceName, String address);
    //服务中心通知 ServiceDiscovery 进行服务缓存的增加(新加了一个服务)
    void addCache(String serviceName,String address);
}
