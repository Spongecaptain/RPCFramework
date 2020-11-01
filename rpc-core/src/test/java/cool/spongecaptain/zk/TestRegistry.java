package cool.spongecaptain.zk;

import cool.spongecaptain.exception.RpcException;
import cool.spongecaptain.registry.ServiceDiscovery;
import cool.spongecaptain.registry.ServiceRegistry;
import cool.spongecaptain.registry.zk.CuratorUtil;
import cool.spongecaptain.registry.zk.ZKServiceDiscovery;
import cool.spongecaptain.registry.zk.ZKServiceRegistry;
import org.junit.Test;

public class TestRegistry {

//    @Test
//    public void testRegistry(){
//        ServiceRegistry serviceRegistry = new ZKServiceRegistry();
//
//        ServiceDiscovery serviceDiscovery = new ZKServiceDiscovery();
//
//        //先测试删除
//        serviceRegistry.deleteService(CuratorUtil.NAME_SPACE);
//
//        String rpc1 = null;
//        try {
//            rpc1 = serviceDiscovery.lookForService("cool.spongecaptain.Echo");
//        } catch (RpcException e) {
//            e.printStackTrace();
//        }
//        System.out.println(rpc1);
//        //在测试添加
//
//        serviceRegistry.registerService("cool.spongecaptain.Echo","localhost:2222");
//
//        String rpc2 = null;
//        try {
//            rpc2 = serviceDiscovery.lookForService("cool.spongecaptain.Echo");
//        } catch (RpcException e) {
//            e.printStackTrace();
//        }
//        System.out.println(rpc2);
//
//    }
}
