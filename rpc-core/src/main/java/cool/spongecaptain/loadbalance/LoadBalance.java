package cool.spongecaptain.loadbalance;

import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.List;

public interface LoadBalance {
    //String 对应于能够提供服务的服务器地址
    String getServerAddress(RpcRequest request, List<ServiceInfo> serverList);
}
