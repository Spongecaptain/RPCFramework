package cool.spongecaptain.zk;

import cool.spongecaptain.registry.zk.ZKServiceDiscovery;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static cool.spongecaptain.registry.zk.CuratorUtil.getChildrenNodes;

public class TestCurator {
    private static final String NAME_SPACE = "/rpc";

    @Test
    public void testCurator(){
        ZKServiceDiscovery zkServiceDiscovery = new ZKServiceDiscovery();
        try {
            System.out.println("init-----------------");
            List<String> childrenNodes = getChildrenNodes(NAME_SPACE, zkServiceDiscovery);

            childrenNodes.forEach(System.out::println);

            System.out.println("init-----------------");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(10000);//休眠 20 秒，在 20 秒内你可以尝试增删改查一个新节点
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ConcurrentHashMap<String, String> cache = zkServiceDiscovery.getCache();

        for(String key:cache.keySet()){
            System.out.println(key+" "+ cache.get(key));
        }
    }
}
