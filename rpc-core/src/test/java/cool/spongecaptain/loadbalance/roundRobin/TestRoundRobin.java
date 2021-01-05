package cool.spongecaptain.loadbalance.roundRobin;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class TestRoundRobin {
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


        LoadBalance loadBalance = new RoundRobinLoadBalance();

        //用于统计 10k 次后，各个地址负载均衡是否呈现完全的均匀化
        int[] array = new int[10];

        for (int i = 0; i < 10000; i++) {
            //这里我们的背景是总共有 100 个服务
            RpcRequest.Builder builder = new RpcRequest.Builder(String.valueOf(i), "cool.spongecaptain.FooInterface" + i%100, "barMethod");

            RpcRequest rpcRequest = builder.build();

            String rst = loadBalance.getServerAddress(rpcRequest, serverList);

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
        //因为是轮询，因此每一个值都应当是相同的 1000 次
        for (int j = array.length - 1; j >= 0; j--) {
            System.out.println(array[j]);
        }

    }
}
