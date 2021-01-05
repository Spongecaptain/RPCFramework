package cool.spongecaptain.registry.zk;

import cool.spongecaptain.registry.ServiceRegistry;

public class ZKServiceRegistry implements ServiceRegistry {


    public void registerService(String serviceName, String address, int weight) {
        //TODO 这个服务注册方法需要进行修改
        //我们实际上向 ZooKeeper 注册的节点应当是（不讨论上层节点）：节点名：cool.spongecaptain.Echo/Provider/ip，节点 value :权值
        String nodeName = serviceName + "/" + "Provider" + "/" + address;
        CuratorUtil.createPersistentNode(nodeName, String.valueOf(weight));
    }

    /**
     * @param serviceName 为了简化模型，这个参数直接为 NAMESPACE，注意事项：这里是递归地删除所有子节点
     */
    @Override
    public void deleteService(String serviceName) {
        CuratorUtil.deleteCurrentNodeAndChildren(serviceName);
    }
}
