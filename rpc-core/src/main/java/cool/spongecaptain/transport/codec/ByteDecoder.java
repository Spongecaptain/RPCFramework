package cool.spongecaptain.transport.codec;

import cool.spongecaptain.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *
 * 还是需要参造通信协议进行解码逻辑的执行
 * 注意事项：编解码器并不允许使用单例模式，因此必须提供构造器的方式提供编解码器实例
 */
public class ByteDecoder extends ByteToMessageDecoder {
    //在我们的协议中，数据包最小的长度就是：4+1+1+1+4 = 11 字节长度，因此首先要判断长度是否达标
    public static final int MIN_LENGTH = 11;

    private Serialization serialization;
    //在 ByteDecoder 类上我们也使用单例模式
    private static volatile ByteDecoder byteDecoder;

    public ByteDecoder(Serialization serialization) {
        this.serialization = serialization;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()<MIN_LENGTH ){
            return ;
        }
        //TODO 我们这里先假设魔数不会出问题，出问题暂时不考虑...，因此这里即使读到数据，也仅仅是起到消费 ByteBuf 数据的作用
        in.markReaderIndex();
        //魔数
        int magicNumber = in.readInt();
        //版本号
        byte versionNumber = in.readByte();
        //序列化号
        byte serializationID = in.readByte();
        //commandID
        byte commandID = in.readByte();
        //dataLength
        int dataLength = in.readInt();

        //为了解决所谓的 TCP "粘包"问题，我们使用如下逻辑
        if(in.readableBytes()<dataLength) {
            in.resetReaderIndex();
            return;
        }
        //否则进行反序列处理
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object o = serialization.deserialize(data);
        //将序列化结果作为事件向 Pipeline 上的 ChannelInboundHandler 传递
        out.add(o);
    }
}
