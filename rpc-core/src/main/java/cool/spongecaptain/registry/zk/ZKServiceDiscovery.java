package cool.spongecaptain.registry.zk;

import cool.spongecaptain.exception.RpcException;
import cool.spongecaptain.registry.ServiceDiscovery;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ZKServiceDiscovery  implements ServiceDiscovery {
    /**
     * key 为 serviceName
     * value 为提供该服务的 IP 地址+端口
     */
    private ConcurrentHashMap<String,String> cache = new ConcurrentHashMap<>();
    @Override
    public String lookForService(String serviceName) {
        //缓存优先
        if (cache.contains(serviceName)) {
            return cache.get(serviceName);
        }else{

            //如果 serviceName 为 NAMESPACE，说明想要获取所有 NAMESPACE 下的所有子节点，其不注重返回值
            if(serviceName.equals(CuratorUtil.NAME_SPACE)){
                //1. 获得 /rpc 根节点下的所有节点列表(这个操作会默认将注册信息缓存到 cache 中)
                List<String> childrenNodes = CuratorUtil.getChildrenNodes(serviceName, this);
                //2. 检查 List 是否为空
                if(childrenNodes==null||childrenNodes.size() ==0){
                    throw new RpcException("/rpc 没有找到任何服务 "+serviceName);
                }

                return "/rpc";
             //说明只是想获取某一个节点的数据
            }else{
                    String nodeData = CuratorUtil.getData(serviceName, this);
                    return  nodeData;
            }
        }



    }


    @Override
    public void deleteCache(String serviceName) {
        cache.remove(serviceName);
    }

    @Override
    public void updateCache(String serviceName, String address) {
        addCache(serviceName,address);
    }

    @Override
    public void addCache(String serviceName, String address) {
        cache.put(serviceName,address);
    }


    public ConcurrentHashMap<String, String> getCache(){
        return cache;
    }
}
