package cool.spongecaptain.client;

/**
 * 请求的版本号、group 应当对于一个 Client 统一配置的
 */
public class RpcClientConfig {
    private String version;
    private String group;

    public RpcClientConfig(String version, String group) {
        this.version = version;
        this.group = group;
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
}
