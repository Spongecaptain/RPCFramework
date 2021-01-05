package cool.spongecaptain.loadbalance.random;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.List;
import java.util.Random;

/**
 * 具有权重的随机负载均衡算法
 */
public class WeightRandomLoadBalance implements LoadBalance {

    private Random random;

    public WeightRandomLoadBalance() {
        random = new Random();
    }

    @Override
    public String getServerAddress(RpcRequest rpcRequest, List<ServiceInfo> serverList) {

        int sum = 0;

        int[] weightArray = new int[serverList.size()];

        //遍历 serverList，计算权重和，并初始化 weightArray 数组
        for (int i = 0; i < serverList.size(); i++) {
            int weight = serverList.get(i).getWeight();
            weightArray[i] = weight;
            sum += weight;
        }

        //首先在 [0,sum) 范围内进行随机数的计算
        int rad = random.nextInt(sum);

        int cur_total = 0;

        for (int i = weightArray.length - 1; i >= 0; i--) {

            cur_total += weightArray[i];

            if (cur_total > rad) {
                return serverList.get(i).getAddress();
            }
        }
        return serverList.get(0).getAddress();
    }

}
