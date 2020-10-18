package cool.spongecaptain.exception;

public class RpcException extends RuntimeException {

    public RpcException(String details) {
        super(details);
    }

    public RpcException(String details, Throwable cause) {
        super(details, cause);
    }

}
