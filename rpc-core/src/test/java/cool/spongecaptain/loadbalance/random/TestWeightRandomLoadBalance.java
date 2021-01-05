package cool.spongecaptain.loadbalance.random;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class TestWeightRandomLoadBalance {
    public static void main(String[] args) {
        List<ServiceInfo> serverList = new ArrayList<>();
        //1,2,3,4 大小级别的权重设置
        serverList.add(new ServiceInfo("0.0.0.0：8888",1,-1));
        serverList.add(new ServiceInfo("1.1.1.1：8888",2,-1));
        serverList.add(new ServiceInfo("2.2.2.2：8888",3,-1));
        serverList.add(new ServiceInfo("3.3.3.3：8888",4,-1));

        LoadBalance loadBalance = new WeightRandomLoadBalance();

        //用于统计 10k 次后，各个地址负载均衡是否呈现均匀随机化
        int[] array = new int[4];

        for (int i = 0; i < 10000; i++) {
            String rst = loadBalance.getServerAddress(null, serverList);

            switch (rst){
                case "0.0.0.0：8888":array[0]++;break;
                case "1.1.1.1：8888":array[1]++;break;
                case "2.2.2.2：8888":array[2]++;break;
                case "3.3.3.3：8888":array[3]++;break;
                default:
                    System.out.println("error");
            }
        }
        //在统计意义上，呈现一个大致的 1:2:3:4 的随机分布
        for (int j = 0; j < array.length; j++) {
            System.out.println(array[j]);
        }

    }
}
