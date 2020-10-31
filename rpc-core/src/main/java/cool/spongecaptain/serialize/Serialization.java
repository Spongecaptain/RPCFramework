package cool.spongecaptain.serialize;

public interface Serialization {

    //Java 对象序列化为字节数据
    byte[] serialize(Object object);
    //返回序列化算法 ID
    int getSerializationId();
    //反序列化方法
    Object deserialize(byte[] bytes);

}
