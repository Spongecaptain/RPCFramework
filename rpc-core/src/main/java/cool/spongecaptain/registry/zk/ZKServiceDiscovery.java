package cool.spongecaptain.registry.zk;

import cool.spongecaptain.registry.ServiceDiscovery;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ZKServiceDiscovery  implements ServiceDiscovery {
    /**
     * key 为 serviceName
     * value 为提供该服务的 IP 地址+端口
     */

    //此时返回的内容应当是一个 List<ServiceInfo> 集合

    private final ConcurrentHashMap<String, List<ServiceInfo>> cache = new ConcurrentHashMap<>();


    @Override
    public List<ServiceInfo> lookForService(String serviceName) {
        //缓存优先
        List<ServiceInfo> serviceList;
        if(cache.contains(serviceName)){
            return cache.get(serviceName);
        }else{
            //获取当前服务对应的所有服务提供者的地址：获取 serviceName/Provider 节点下的所有子节点
            List<String> addresses = CuratorUtil.getChildrenNodes(serviceName+"/Provider",this);
            //获取上述地址中所有节点的值，即权重
            serviceList = new ArrayList<>();

            //同一个 Client 并不需要多线程地进行更新，因此这里进行上锁处理，不过需要进行 double-check 机制，避免重复进行缓存更新
            synchronized (cache){
                if(!cache.contains(serviceName)){
                    for (int i = 0; i < addresses.size(); i++) {
                        //得到节点对应的权重
                        String weight = CuratorUtil.getData(serviceName+"/Provider/"+addresses.get(i),this);
                        serviceList.add(new ServiceInfo(addresses.get(i),Integer.parseInt(weight),-1));
                    }
                    //缓存来自 ZooKeeper 的查询结果
                    cache.put(serviceName,serviceList);
                }
            }

        }
        return serviceList;
    }
}
