package cool.spongecaptain.protocol;

/**
 * 这代表 Provider 向 Consumer 返回的一个 RPC 调用响应
 * 一个 RPC 响应要比 RPC 请求要简单很多，包括如下内容：
 * 请求 ID、响应状态码、响应消息、响应体
 * 因为 Response 相对简单，因此就不使用构造者模式，直接使用构造器即可
 */
public class RpcResponse<T> {
    //请求 id，注意其为 String 类型，这个 id 用于 request 以及 response 的一一对应
    private String requestId;
    //响应状态码：反映本次 RPC 远程调用是否成功
    private Integer code;
    //响应消息：作为响应状态码的具体补充，文字上更详细的说明
    private String message;
    //响应体：之所以为泛型是因为 RPC 调用的返回值类型并不能确定，这是最重要的数据部分
    private T body;
    //Kryo 序列化与反序列化必须要有无参构造器
    public RpcResponse(){}
    public RpcResponse(String requestId, Integer code, String message, T body) {
        this.requestId = requestId;
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
