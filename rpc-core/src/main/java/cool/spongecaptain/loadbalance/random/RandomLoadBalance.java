package cool.spongecaptain.loadbalance.random;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.List;
import java.util.Random;

/**
 * 简单随机
 */
public class RandomLoadBalance implements LoadBalance {

    private Random random;

    public RandomLoadBalance() {
        random = new Random();
    }

    @Override
    public String getServerAddress(RpcRequest rpcRequest, List<ServiceInfo> serverList) {
        //首先，确定随机整数的最大值
        int max = serverList.size();
        //在 0 - size-1 返回内进行随机
        int index = random.nextInt(max);//返回 [0,max)返回内的一个整数，左闭右开
        //返回服务器地址
        return serverList.get(index).getAddress();
    }

}
