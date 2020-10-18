package cool.spongecaptain.registry.zk;

import cool.spongecaptain.registry.ServiceRegistry;

public class ZKServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String serviceName, String address) {
        CuratorUtil.createPersistentNode(serviceName, address);
    }

    /**
     * @param serviceName 为了简化模型，这个参数直接为 NAMESPACE，注意事项：这里是递归地删除所有子节点
     */
    @Override
    public void deleteService(String serviceName) {
        CuratorUtil.deleteCurrentNodeAndChildren(serviceName);
    }
}
