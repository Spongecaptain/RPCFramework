package cool.spongecaptain.loadbalance.random;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class TestRandomLoadBalance {
    public static void main(String[] args) {
        List<ServiceInfo> serverList = new ArrayList<>();

        serverList.add(new ServiceInfo("0.0.0.0：8888",-1,-1));
        serverList.add(new ServiceInfo("1.1.1.1：8888",-1,-1));
        serverList.add(new ServiceInfo("2.2.2.2：8888",-1,-1));
        serverList.add(new ServiceInfo("3.3.3.3：8888",-1,-1));
        serverList.add(new ServiceInfo("4.4.4.4：8888",-1,-1));
        serverList.add(new ServiceInfo("5.5.5.5：8888",-1,-1));
        serverList.add(new ServiceInfo("6.6.6.6：8888",-1,-1));
        serverList.add(new ServiceInfo("7.7.7.7：8888",-1,-1));
        serverList.add(new ServiceInfo("8.8.8.8：8888",-1,-1));
        serverList.add(new ServiceInfo("9.9.9.9：8888",-1,-1));

        LoadBalance loadBalance = new RandomLoadBalance();

        //用于统计 10k 次后，各个地址负载均衡是否呈现均匀随机化
        int[] array = new int[10];

        for (int i = 0; i < 10000; i++) {
            String rst = loadBalance.getServerAddress(null, serverList);

            switch (rst){
                case "0.0.0.0：8888":array[0]++;break;
                case "1.1.1.1：8888":array[1]++;break;
                case "2.2.2.2：8888":array[2]++;break;
                case "3.3.3.3：8888":array[3]++;break;
                case "4.4.4.4：8888":array[4]++;break;
                case "5.5.5.5：8888":array[5]++;break;
                case "6.6.6.6：8888":array[6]++;break;
                case "7.7.7.7：8888":array[7]++;break;
                case "8.8.8.8：8888":array[8]++;break;
                case "9.9.9.9：8888":array[9]++;break;
                default:
                    System.out.println("error");
            }
        }
        //在统计意义上，呈现一个大致的均匀分布
        for (int j = array.length - 1; j >= 0; j--) {
            System.out.println(array[j]);
        }


    }
}
