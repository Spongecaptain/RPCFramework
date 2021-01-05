package cool.spongecaptain.registry;

import java.util.List;

/**
 * 服务发现接口
 * 供客户端使用
 */
public interface ServiceDiscovery {
    //发现一个服务：返回拥有该服务的地址集合
    List<ServiceInfo> lookForService(String serviceName);
}
