package cool.spongecaptain.registry.zk;

import cool.spongecaptain.exception.RpcException;
import cool.spongecaptain.registry.ServiceDiscovery;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CuratorUtil {
    //访问 ZooKeeper 服务端失败后的最大重试次数
    private static final int MAX_RETRY = 3;
    //访问 ZooKeeper 服务端重试的时间间隔(单位 ms)
    private static final int INTERVAL = 1000;
    //服务器列表，多个则用逗号隔开
    private static final String SERVER_LIST = "localhost:2181";
    //会话超时时间(单位：ms)
    private static final int SESSION_TIMEOUT = 5000;
    //连接超时时间
    private static final int CONNECT_TIMEOUT = 5000;
    //此 Curator 客户端的以 /rpc 作为相对路径进行操作
    public static final String NAME_SPACE = "/rpc";
    private static final RetryPolicy retryPolicy = new ExponentialBackoffRetry(INTERVAL, MAX_RETRY);
    //日志组件
    private static final Logger logger = LoggerFactory.getLogger(CuratorUtil.class);
    //Curator 客户端
    private static CuratorFramework client;


    /**
     * @return CuratorFramework 返回一个已经初始化并启动了的 ZooKeeper 客户端
     */
    public static CuratorFramework getStartedClient() {
        //如果未初始化或者未启动，那么就重新创建一个 CuratorFramework 实例返回
        if (client == null || client.getState() != CuratorFrameworkState.STARTED) {
            client =
                    CuratorFrameworkFactory.builder()
                            .connectString(SERVER_LIST)
                            .sessionTimeoutMs(SESSION_TIMEOUT)
                            .connectionTimeoutMs(CONNECT_TIMEOUT)
                            .retryPolicy(retryPolicy)
                            .build();
            client.start();
        }
        logger.info("Create A CuratorFramework Instance");
        return client;
    }


    /**
     * 返回某个 path 下的所有子节点的 path
     * 这个方法通常用于得到路径下所有的子节点，在这里，因为应用场景是服务注册，因此 path 是 NAMESPACE
     *
     * @param path             这里的输入值就是 NAMESPACE
     * @param serviceDiscovery
     * @return 每一个 String 元素对应于一个节点对应的 path 字符串
     */
    public static List<String> getChildrenNodes(String path, ServiceDiscovery serviceDiscovery) {
        List<String> childNodeList = null;
        String fullPath = turnPathIntoFullPath(path);
        try {
            childNodeList = getStartedClient().getChildren().forPath(fullPath);
            setChildWatcher(fullPath, serviceDiscovery);
        } catch (Exception e) {
            throw new RpcException("Fail to get child node");
        }
        return childNodeList;
    }

    /**
     * 为 path （相对于 NAMESPACE）以 data 创建节点
     * 如果没有上层阶段，Curator 会替我们创建
     * @param path
     * @param data
     */
    public static void createPersistentNode(String path, String data) {
        String fullPath = turnPathIntoFullPath(path);
        try {
            getStartedClient().create().creatingParentContainersIfNeeded().forPath(fullPath, data.getBytes());
        } catch (Exception e) {
            //如果异常是因为节点已经存在，我们应当尝试进行节点数据的更新，如果再出错，那么就抛出异常
            try {
                getStartedClient().setData().forPath(fullPath,data.getBytes());
            } catch (Exception exception) {
                throw new RpcException("Fail to create " + path + "with value of " + data + " in ZooKeeper Server on " + SERVER_LIST);
            }
        }
    }


    /**
     * 递归地删除 path 目录下的所有节点，因此这里的输入参数 path 通常为 NAMESPACE
     *
     * @param path
     */
    public static void deleteCurrentNodeAndChildren(String path) {
        CuratorFramework client = getStartedClient();
        try {
            client.delete().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            throw new RpcException("Fail to recursively delete " + path + " in ZooKeeper Server on " + SERVER_LIST);
        }
    }


    /**
     * 更新节点操作
     *
     * @param path
     * @param data
     */
    public static void updateNode(String path, String data) {
        try {
            getStartedClient().setData().forPath(path, data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用于为指定路径下所有的 Child 节点设置 Watcher，在此应用场合下，path 就是 NAMESPACE
     * 注意：首次注册 Watcher 时，我们会顺便得到 NAMESPACE 下所有的子节点的 data
     *
     * @param path             与 fullPath 实际上都是 NAMESPACE
     * @param serviceDiscovery 注意，由于在 getData 节点时会设置具体一个节点的 Watcher，因此在这里并不需要为节点的更新操作进行 Watcher，仅仅需要注册新增节点的操作
     */
    public static void setChildWatcher(final String path, final ServiceDiscovery serviceDiscovery) {
        String fullPath = turnPathIntoFullPath(path);
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(getStartedClient(), fullPath, true);

        // 设置 Watcher 触发时对服务发现的回调，这部分逻辑是设置给当 ZooKeeper 的 child 节点修改时再改变
        PathChildrenCacheListener pathChildrenCacheListener = (client1, event) -> {
            PathChildrenCacheEvent.Type type = event.getType();
            //得到产生事件的子节点的 path
            String childPath = event.getData().getPath();
            //要将子节点的完整路径名转为 serviceName，这样才能够加入缓存中
            String serviceName = turnFullPathIntoServiceName(childPath);
            switch (type) {
                //节点添加事件
                case CHILD_ADDED:
                    byte[] dataBytes = client1.getData().forPath(childPath);
                    String nodeData = new String(dataBytes);
                    serviceDiscovery.addCache(serviceName, nodeData);
                    logger.info("添加了一个新节点" + serviceName + " " + nodeData);
                    break;
                //节点删除事件
                case CHILD_REMOVED:
                    serviceDiscovery.deleteCache(serviceName);
                    logger.info("删除了一个缓存" + serviceName);
                    break;
                //节点更新事件
                case CHILD_UPDATED:
                    byte[] dataBytes2 = client1.getData().forPath(childPath);
                    String nodeData2 = new String(dataBytes2);
                    serviceDiscovery.updateCache(serviceName, nodeData2);
                    logger.info("更新了一个缓存 " + serviceName + nodeData2);
                    break;
                default:
                    break;//do noting
            }

        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RpcException("Fail to get one Watcher Notification");
        }

    }

    /**
     * @param path 的格式我们设定为：NAMESPACE + "/" + 接口的完全限定名
     * @return
     */
    private static String turnFullPathIntoServiceName(String path) {

        int delStrLength = NAME_SPACE.length() + 1;
        String serviceName = "";
        if (path.startsWith(NAME_SPACE + '/')) {
            serviceName = path.substring(delStrLength);
        }
        return serviceName;
    }

    /**
     * @param relativePath
     * @return 当 relativePath 为 NAME_SPACE 时直接返回，否则加上前缀 NAME_SPACE 然后再返回
     */
    private static String turnPathIntoFullPath(String relativePath) {

        if (relativePath.equals(NAME_SPACE)) {
            return relativePath;
        }
        return NAME_SPACE + "/" + relativePath;
    }


    //    /**
//     * 为 path 注册一个 Watcher
//     * 目的是为了在服务端修改、添加、删除服务注册信息时，客户端能够得知这一消息
//     *
//     * full path 对应于 ZooKeeper
//     *
//     * 所有的 Watcher 逻辑都写于 NAMESPACE 下，因此这个方法是多余的，实际上不需要单独为某一个节点设置 Watcher
//     */
//
//    @Deprecated
//    public static void setWatcher(final String path, final ServiceDiscovery serviceDiscovery) {
//
//        final String fullPath = turnPathIntoFullPath(path);
//
//        NodeCache nodeCache = new NodeCache(getStartedClient(), fullPath);
//        NodeCacheListener listener = ()->{
//            ChildData currentData = nodeCache.getCurrentData();
//
//            if (null != currentData) {
//                //更新缓存中的条目
//                System.out.println("更新一条记录");
//                serviceDiscovery.updateCache(fullPath,currentData.toString());
//            } else {
//                //删除缓存中的条目
//                System.out.println("删除一条记录");
//                serviceDiscovery.deleteCache(fullPath);
//            }
//
//        };
//        nodeCache.getListenable().addListener(listener);
//
//        try {
//            nodeCache.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//

    /**
     * 得到具体某一个 path 对应节点的 data
     * 并不需要单位得到某一个 path 节点对应 data 的逻辑，因此注释掉此方法
     *
     * @param path
     * @param serviceDiscovery
     * @return
     * @throws Exception
     */

    public static String getData(String path, ServiceDiscovery serviceDiscovery) {
        final String fullPath = NAME_SPACE + "/" + path;

        byte[] resultBytes = new byte[0];
        try {
            resultBytes = getStartedClient().getData().forPath(fullPath);
        } catch (Exception e) {
            throw new RpcException(e.getMessage());
        }

        //setWatcher(path, serviceDiscovery); 因为在 /rpc 下对所有子节点都进行 Watcher 的注册，因此这里不需要再次设置 Watcher

        String nodeData = new String(resultBytes);
        //更新缓存
        serviceDiscovery.updateCache(path, nodeData);
        return nodeData;
    }
}
