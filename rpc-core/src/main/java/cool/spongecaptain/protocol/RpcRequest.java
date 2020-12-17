package cool.spongecaptain.protocol;

/**
 * 这代表 Consumer 向 Provider 发送的一个 RPC 调用请求
 * 一个完整的 RPC 请求应当包括两个部分：
 * 1. 消息的 metadata：请求 ID、RPC 消息类型、版本号、组别
 * 2. 方法调用的 metadata：接口的完全限定名（服务名）、方法名、方法入口参数类型、方法入口参数的具体值
 * 由于参数太多了，这里不妨使用构建者模式
 */
public class RpcRequest {
    //请求 id，注意其为 String 类型，这个 id 用于 request 以及 response 的一一对应
    private String requestId;
    //RPC 消息类型
    private final static RpcRequestType rpcMessageType = RpcRequestType.CALL;
    //版本号
    private String version;
    //组别
    private String group;
    //接口名(服务名)
    private String interfaceName;
    //方法名
    private String methodName;
    //方法入口参数的类型数组
    private Class<?>[] paramTypes;
    //方法入口参数数组
    private Object[] parameters;

    //Builder 下构造器应当是私有的，但是为了进行 Kryo 序列化以及反序列化，必须要有无参构造器

    public RpcRequest(){
    }



    private RpcRequest(Builder builder) {
        this.requestId = builder.requestId;
        this.version = builder.version;
        this.group = builder.group;
        this.interfaceName = builder.interfaceName;
        this.methodName = builder.methodName;
        this.paramTypes = builder.paramTypes;
        this.parameters = builder.parameters;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public RpcRequestType getRpcMessageType() {
        return rpcMessageType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public void setParamTypes(Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public static class Builder {
        private String requestId;//必须
        private String interfaceName;//必须
        private String methodName;//必须
        private RpcRequestType rpcMessageType;//可选
        private String version;//可选
        private String group;//可选
        private Class<?>[] paramTypes;//可选（代表无参）
        private Object[] parameters;//可选（代表无参）

        public Builder(String requestId, String interfaceName, String methodName) {
            this.requestId = requestId;
            this.interfaceName = interfaceName;
            this.methodName = methodName;
        }

        public Builder setRpcMessageType(RpcRequestType rpcMessageType) {
            this.rpcMessageType = rpcMessageType;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder setParamTypes(Class<?>[] paramTypes) {
            this.paramTypes = paramTypes;
            return this;
        }

        public Builder setParameters(Object[] parameters) {
            this.parameters = parameters;
            return this;
        }

        public RpcRequest build(){
            return new RpcRequest(this);
        }

        public String getRequestId() {
            return requestId;
        }

        public String getInterfaceName() {
            return interfaceName;
        }

        public String getMethodName() {
            return methodName;
        }

        public RpcRequestType getRpcMessageType() {
            return rpcMessageType;
        }

        public String getVersion() {
            return version;
        }

        public String getGroup() {
            return group;
        }

        public Class<?>[] getParamTypes() {
            return paramTypes;
        }

        public Object[] getParameters() {
            return parameters;
        }
    }

}
