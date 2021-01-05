package cool.spongecaptain.loadbalance.leastActive;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.ArrayList;
import java.util.List;

public class TestLeastActiveLoadBalance {
    public static void main(String[] args) {
        List<ServiceInfo> serverList = new ArrayList<>();

        serverList.add(new ServiceInfo("0.0.0.0：8888", -1, 1));//调用数最少，其会优先作为服务提供方交给 Client
        serverList.add(new ServiceInfo("1.1.1.1：8888", -1, 2));
        serverList.add(new ServiceInfo("2.2.2.2：8888", -1, 3));
        serverList.add(new ServiceInfo("3.3.3.3：8888", -1, 3));
        serverList.add(new ServiceInfo("4.4.4.4：8888", -1, 4));
        serverList.add(new ServiceInfo("5.5.5.5：8888", -1, 4));
        serverList.add(new ServiceInfo("6.6.6.6：8888", -1, 5));
        serverList.add(new ServiceInfo("7.7.7.7：8888", -1, 6));
        serverList.add(new ServiceInfo("8.8.8.8：8888", -1, 7));
        serverList.add(new ServiceInfo("9.9.9.9：8888", -1, 8));


        LoadBalance leastActiveLoadBalance = new LeastActiveLoadBalance();

        //用于统计 10k 次后，各个地址负载均衡是否呈现均匀随机化
        int[] array = new int[10];

        //我们创建 1000 个请求进行测试

        for (int i = 0; i < 1000; i++) {
            RpcRequest.Builder builder = new RpcRequest.Builder(String.valueOf(i), "cool.spongecaptain.FooInterface" + i, "barMethod");

            RpcRequest rpcRequest = builder.build();

            String rst = leastActiveLoadBalance.getServerAddress(rpcRequest, serverList);

            switch (rst) {
                case "0.0.0.0：8888":
                    array[0]++;
                    break;
                case "1.1.1.1：8888":
                    array[1]++;
                    break;
                case "2.2.2.2：8888":
                    array[2]++;
                    break;
                case "3.3.3.3：8888":
                    array[3]++;
                    break;
                case "4.4.4.4：8888":
                    array[4]++;
                    break;
                case "5.5.5.5：8888":
                    array[5]++;
                    break;
                case "6.6.6.6：8888":
                    array[6]++;
                    break;
                case "7.7.7.7：8888":
                    array[7]++;
                    break;
                case "8.8.8.8：8888":
                    array[8]++;
                    break;
                case "9.9.9.9：8888":
                    array[9]++;
                    break;
                default:
                    System.out.println("error");
            }
        }

        //因为第一个连接数最少，因此此轮请求实际上都会达到此服务器上（这看起来是一件非常危险的事情，因为可能会导致某一台服务器需要处理激增的请求）
        for (int j = 0; j < array.length; j++) {
            System.out.println(array[j]);
        }
    }
}
