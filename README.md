# README

> 本项目使用了若干个来自于 [Dubbo](https://dubbo.apache.org/) 的术语，项目结构上也相当程度上参考了 Dubbo。

这个项目是一个简单的 RPC 框架，将依赖于：

- ZooKeeper（包括 ZooKeeper 客户端 Curator）：用于实现服务注册；
- Netty：用于实现底层通信协议实现以及 NIO 式网络传输；
- CGLIB：实现动态代理，用于向 Consumer 隐藏 RPC 远程调用中的网络通信过程；
- Kryo：Kryo 是本项目的第三方序列化框架；

模块分为如下：

- rpc-common：一些公共类，例如异常；
- rpc-core：RPC 的核心功能，例如服务注册；
- rpc-api-sample：定义了接口以及接口的具体实现类，一并提供给 Consumer 以及 Provider；
- rpc-consumer-sample：提供一个客户端启动类，用于演示 Consumer 进行 RPC 调用；
- rpc-provider-sample：提供一个服务端启动类，用于演示 Provider 进行 RPC 调用；

## 1. 注册中心

Dubbo 中使用 ZooKeeper 服务中心如下图所示：

![/user-guide/images/zookeeper.jpg](doc/images/zookeeper.jpg)

我将上述注册中心模型进行简化（我们没有必要如此复杂），将 consumers 节点去除。

在根节点 `/rpc` 下的每一个节点都是服务节点：

- 节点的 path：一个接口的完全限定名，例如图中的 cool.spongecaptain.Foo 接口；
- 服务节点的子节点 path：能够提供此接口的服务的服务器地址，例如 localhost:2222，其 value 对应于服务权重。

注册中心能够提供的功能有：

- Provider 能够向注册、更新、删除中心注册其能够提供的服务；
- Consumer 能够从注册中心获取其所需的服务，并在服务被增删改时得到提醒；

注册中心的作用事实上在 Dubbo 的 Dubbo Architecture 说地很明白了，如下图所示：

![img](doc/images/architecture.png)

---

项目的注意事项，[Dubbo-samples](https://github.com/Spongecaptain/dubbo-samples) 中的不少模块有提供内嵌的 ZooKeeper 以供调试使用，但是本项目要求你必须在本地启动一个 ZooKeeper（Standalone 模式即可）。且项目默认 ZooKeeper 服务端监听着 2181 端口。

使用 Docker 来启动一个 ZooKeeper 是一个很好的选择。

## 2. 基于 Netty 的消息传输

Netty 已经非常大程度上减少我们在 Java 网络传输上的复杂度，但是要理解整个 RPC 消息传输以及响应的过程也不是一件简单的事情。

### 2.1 项目模块说明

- client 向代理层暴露一个发送 RpcRequest 消息，并得到 RPC 执行结果的接口；
- server 面向服务端服务，主要功能是向注册中心进行服务注册以及提供 RPC 调用的监听；
- handler 在服务端提供对 RprRequest 的处理能力；
- protocol 规定了RpcRequest 以及 RpcResponse 类的字段结构；
- serialize 提供了对 RpcRequest 以及 RpcResponse 的序列化功能；
- transport 处于整个框架的底层，基于 Netty 实现了请求-响应中的客户端与服务端；

### 2.2 Netty 消息传播模型

Netty 客户端实现类为 cool.spongecaptain.transport.client.NettyClient，Netty 服务端实现类为 cool.spongecaptain.transport.server.NettyServer，它们的管道模型非常类似，都可以用下图表示：

![RpcNettyPipeline](doc/images/RpcNettyPipeline.png)

不过，客户端与服务端在每一个 ChannelHandler 的类型选择上有所不同。

Netty 客户端：

- in1/out1：IdleStateHandler 用于长时间未通信后的 TCP 关闭
- in2：ByteDecoder 负责将 ByteBuf 转换为 RPCResponse 实例
- in3：RpcResponseHandler 负责 RpcResponse 的处理，包括服务端对 request 中指定方法的调用
- out1：MessageEncoder RPCRequest 转为 ByteBuf 后向前传播

Netty 服务端：

- in1/out1：用于长时间未通信后的 TCP 关闭
- in2：Decoder 负责将 ByteBuf 的字节数据转换为 RpcRequest 实例
- in3：RPCRequestHandler 负责处理 RpcRequest 对应的 RPC 方法，执行后产生对应的 RPCResponse 实例返回
- out1：Encoder 负责将 RPCResponse 转换为 ByteBuf，然后再向前传播

### 2.3 消息传播协议

自定义的消息传播协议基于 TCP 协议，其默认的格式为：

```
Magic Number + versionNumber + serializaitonID + commandID + dataLength + data
```

长度依次为：

```
4 byyte、1 bytes + 1 bytes + 1 bytes + 4 bytes + depend on dataLength
```

> TODO commandID 需要么？这是存疑的？

## 3. RPC 代理策略

RPC 代理类为 cool.spongecaptain.proxy.RpcProxy。

Apache Dubbo 是一个被广泛使用的 Java RPC 框架，其通过 Spring XML 扩展的方式实现了对代理的封装，详细实现可以参考我的博文：[Spring XML schema 扩展机制](https://spongecaptain.cool/post/spring/spring_xml_schema/)

下面是一个例子（来自于 [dubbo-samples/dubbo-samples-basic](https://github.com/Spongecaptain/dubbo-samples) 模块）：

```java
public class BasicConsumer {
  public static void main(String[] args) {
    //1. 加载配置
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/dubbo-demo-consumer.xml");
    //2. 获取消费代理
    context.start();
    DemoService demoService = (DemoService) context.getBean("demoService");
    //改为调用 3 次
    for (int i = 0; i < 3; i++) {
      //3. 调用远程方法
      String hello = demoService.sayHello("world");
      //在控制台输出红色更醒目一点
      System.err.println(hello);
    }
    //System.in.read(); 用于避免 Consumer 的 main 方法暂停运行
    try {
      System.in.read();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
```

对于 BasicConsumer 客户端而言，Dubbo 做到了将 `demoService.sayHello("world")` 方法的调用过程中的 RPC 过程完全向上屏蔽。

但对于我们的简单 RPC 框架而言，并不需要隔离地如此彻底，因此我采用了 CGLIB 作为动态代理实现框架，实现了如下功能：通过向 RpcProxy#newInstance 方法传入接口名，能够得到接口的 RPC 代理实现类。代理实现类提供了如下的逻辑：将方法的调用转换为 RpcRequest 实例，然后将 RpcRequest 实例交给 NettyRpcClient 来进行处理。

## 4. 负载均衡

负载均衡是本框架新添加的功能，其接口设计如下：

```java
public interface LoadBalance {
    //String 对应于能够提供服务的服务器地址
    String getServerAddress(RpcRequest request, List<ServiceInfo> serverList);
}
```

我将负载均衡器接口根据函数式编程思想进行设计，其具体输出只与方法的输入参数有关，而没有任何状态。 这种设计既去耦合，又线程并发安全，符合接口设计规范。

具体的负载均衡器有如下若干种类型设计：

- RandomLoadBalance：简单随机；
- WeightRandomLoadBalance：权重随机（这是默认的使用策略）；
- ConsistentHashLoadBalance：一致性 Hash；
- LeastActiveLoadBalance：最少调用数优先；
- RoundRobinLoadBalance：轮询策略；

关于负载均衡策略，更多的内容可以参考我个人博客的文章：：https://spongecaptain.cool/post/rpc/addloadbalanceintorpc/

## 5. 启动类

- 客户端启动类的具体类型为 rpc-consumer-sample 模块下的 cool.spongecaptain.ConsumerBootstrap 类；
- 服务端启动类的具体类型为 rpc-provider-sample 模块下的 ：
  - cool.spongecaptain.ProviderBootstrap1 类
  - cool.spongecaptain.ProviderBootstrap2 类

其中，两个 Provider 监听本地不同的服务器端口，作为两个不同的 RPC 服务提供这。

> From ConsumerBootstrap: sayHello result: Hello spongecaptain !
>
> From ConsumerBootstrap: add result: 3

## 6. 框架说明

可以参考个人的博客：https://spongecaptain.cool/post/rpc/myrpcframework/

## 7. TODO

- [ ] 实现 RPC 调用中的超时与重试机制；
- [ ] 利用配置文件，使各个组件可以根据配置动态可替换；
