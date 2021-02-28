package cool.spongecaptain.transport.codec;

import cool.spongecaptain.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 我们的通信协议结构如下所示：
 * TODO commandID 需要么？这是存疑的？
 * Magic Number + versionNumber + serializaitonID + commandID + dataLength + data
 * 长度依次为
 * 4 byyte、1 bytes + 1 bytes + 1 bytes + 4 bytes + depend on dataLength
 * 注意事项：Java 中的
 * - int 即为 4 bytes（32 位）
 *
 * 注意事项：编解码器并不允许使用单例模式，因此必须提供构造器的方式提供编解码器实例
 */

public class MessageEncoder extends MessageToByteEncoder<Object> {

    public static final int MAGIC_NUMBER = 0x10241024;

    public static final byte VERSION_NUMBER = 1;

    public static final byte COMMAND_NUMBER =1 ;//1 代表这是 RPC 消息，2 代表简单的心跳包

    private Serialization serialization;//序列化类实例

    public MessageEncoder(Serialization serialization){
        this.serialization = serialization;
    }


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        //首先我们要利用序列化框架来将实例序列化为字节数据
        byte[] data = serialization.serialize(o);
        int dataLength = data.length;

        byteBuf.writeInt(MAGIC_NUMBER);
        byteBuf.writeByte(VERSION_NUMBER);
        byteBuf.writeByte(serialization.getSerializationId());
        //TODO 不知道是否需要 CommandID，不过暂时先使用着
        byteBuf.writeByte(COMMAND_NUMBER);
        byteBuf.writeInt(dataLength);
        byteBuf.writeBytes(data);
    }
}