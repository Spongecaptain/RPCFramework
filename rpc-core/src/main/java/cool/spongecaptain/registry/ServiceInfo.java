package cool.spongecaptain.registry;

/**
 * 为了同一负载均衡处接口的方法设计，对于一个可以处理某一个请求的服务，我们不仅仅需要使用地址来进行描述
 * 还需要使用 权重、调用数来进行描述
 */
public class ServiceInfo {
    private String address;
    //-1 代表没有权重设置
    private int weight;
    //-1 代表没有连接数的数据
    private int invokeNumber;

    public ServiceInfo(String address, int weight, int invokeNumber) {
        this.address = address;
        this.weight = weight;
        this.invokeNumber = invokeNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getInvokeNumber() {
        return invokeNumber;
    }

    public void setInvokeNumber(int invokeNumber) {
        this.invokeNumber = invokeNumber;
    }

    @Override
    public String toString() {
        return address + " " + weight + " " +invokeNumber;
    }
}
