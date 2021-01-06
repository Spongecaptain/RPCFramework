package cool.spongecaptain.loadbalance.consistentHash;

import cool.spongecaptain.loadbalance.LoadBalance;
import cool.spongecaptain.protocol.RpcRequest;
import cool.spongecaptain.registry.ServiceInfo;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 一致性 Hash 版本的负载均衡策略
 */
public class ConsistentHashLoadBalance implements LoadBalance {

    @Override
    public String getServerAddress(RpcRequest rpcRequest, List<ServiceInfo> serverList) {

        //从入口参数 List<ServiceInfo> serverList 处进行构造 Hash 环
        SortedMap<Integer, String> sortedMap = new TreeMap<>();
        /**
         * 利用 serverList 来构造 Hash 环:
         * 不过事实上并没有单独的 Hash 环作为数据结构，
         * Hash 环的存在重在我们如何来看待 SortedMap<Integer, String> 这一个数据结构
         * 把 Hash 环拉直，就是一个 key 为 hash，value 为服务器地址的有序的 HashMap(SortedMap)
         * 可以参考 URL:https://segmentfault.com/a/1190000021199728
         */
        for (int i = serverList.size() - 1; i >= 0; i--) {
            int hash = getHash(serverList.get(i).getAddress());
            sortedMap.put(hash,serverList.get(i).getAddress());
        }

        /**
         * 首先，我们利用 rpcRequest 请求的服务名进行 Hash 运算
         * 服务名是 接口名 还是 接口名+方法名 取决于 Provider 在服务注册中心的服务注册粒度，这里就仅仅以接口
         */
        //1.得到请求的 hash
        int hashOfRequest = getHash(rpcRequest.getInterfaceName());

        //2.得到大于 hashOfRequest 的所有 Map

        SortedMap<Integer,String> subMap = sortedMap.tailMap(hashOfRequest);

        if (subMap.isEmpty()) {
            //如果没有比该key的hash值大的，则从第一个node开始
            Integer i = sortedMap.firstKey();
            //返回对应的服务器
            return sortedMap.get(i);
        } else {
            //第一个Key就是顺时针过去离node最近的那个结点
            Integer i = subMap.firstKey();
            //返回对应的服务器
            return subMap.get(i);
        }
    }

    //一个基于 FNV1_32_HASH 的 Hash 算法，用于计算服务名的 hash 值
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值
        if (hash < 0)
            hash = Math.abs(hash);
        return hash;
    }
}
