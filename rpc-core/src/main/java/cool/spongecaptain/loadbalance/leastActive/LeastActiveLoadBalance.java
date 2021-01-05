package cool.spongecaptain.loadbalance.leastActive;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.List;

public class LeastActiveLoadBalance implements LoadBalance {

    @Override
    public String getServerAddress(RpcRequest request, List<ServiceInfo> serverList) {
        //这个算法就简单了，找出调用数最少的服务器地址即可

        int min = Integer.MAX_VALUE;
        int index =0;

        for (int i = serverList.size() - 1; i >= 0; i--) {
            int curNumber = serverList.get(i).getInvokeNumber();
            if(curNumber<min){
                min = curNumber;
                index = i;
            }
        }
        return serverList.get(index).getAddress();

    }
}
