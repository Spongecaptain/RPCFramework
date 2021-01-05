package cool.spongecaptain.registry;

import cool.spongecaptain.registry.zk.ZKServiceDiscovery;

import java.util.List;

public class TestDiscovery {
    public static void main(String[] args) {
        ZKServiceDiscovery zkServiceDiscovery = new ZKServiceDiscovery();

        List<ServiceInfo> serviceInfos = zkServiceDiscovery.lookForService("cool.spongecaptain.api.AddInterface");

        for (int i = 0; i < serviceInfos.size(); i++) {
            System.out.println(serviceInfos.get(i));
        }

        List<ServiceInfo> serviceInfos2 = zkServiceDiscovery.lookForService("cool.spongecaptain.api.SayHelloInterface");

        for (int i = 0; i < serviceInfos2.size(); i++) {
            System.out.println(serviceInfos2.get(i));
        }

    }
}
