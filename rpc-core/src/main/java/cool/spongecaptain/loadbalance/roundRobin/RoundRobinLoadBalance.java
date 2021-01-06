package cool.spongecaptain.loadbalance.roundRobin;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询版本的负载均衡策略，轮询版本的负载均衡策略其难点在于需要注意并发安全性，这个类并发安全性尚未确保
 */
public class RoundRobinLoadBalance implements LoadBalance {

    //为每一个 RPC 的服务名创建一个轮询的索引计数
    //key 服务名，value 轮询所用的索引
    final Map<String, AtomicInteger> roundRobinMap = new HashMap<>();
    //注意线程安全性！！！！
    @Override
    public String getServerAddress(RpcRequest rpcRequest, List<ServiceInfo> serverList) {

        //得到轮询索引
        AtomicInteger index = roundRobinMap.get(rpcRequest.getInterfaceName());

        //进行 double check 式的空值初始化
        if (index == null) {
            synchronized (roundRobinMap) {
                if (roundRobinMap.get(rpcRequest.getInterfaceName())==null) {
                    index = new AtomicInteger(-1);
                    roundRobinMap.put(rpcRequest.getInterfaceName(), index);
                }
            }
        }
        //进行取余操作
        int serverListIndex = Math.abs(index.incrementAndGet() % serverList.size());
        //返回地址
        return serverList.get(serverListIndex).getAddress();
        /**
         * 注意，虽然 AtomicInteger 会有溢出的危险，但是当 AtomicInteger 达到最大值以后，
         * 并不会继续增大，而是会转换为 -2147483648，因此，只要进行取取余后的值进行取绝对值，就符合本轮询的使用方式（取余无所谓从整数变为负数）
         */
    }
}
