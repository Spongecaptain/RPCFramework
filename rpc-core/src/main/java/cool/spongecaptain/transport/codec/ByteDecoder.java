package cool.spongecaptain.transport.codec;

import cool.spongecaptain.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * 还是需要参造通信协议进行解码逻辑的执行
 * 注意事项：编解码器并不允许使用单例模式，因此必须提供构造器的方式提供编解码器实例
 */
public class ByteDecoder extends ByteToMessageDecoder {

    //添加日志组件

    private static final Logger logger = LoggerFactory.getLogger(ByteToMessageDecoder.class);

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

        in.markReaderIndex();
        //魔数
        int magicNumber = in.readInt();

        //添加校验魔数的功能，如果魔数错误，那么为了服务器节约资源，这里直接选择断开连接

        if(magicNumber != MessageEncoder.MAGIC_NUMBER){

            ctx.channel().close();
            logger.info("message from "+ctx.channel().remoteAddress()+" has a error magic number, so close the channel.");
            return;
        }

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
        //将序列化结果作为事件向 Pipeline 上的 ChannelInboundHandler 传递，类型即 RpcRequest 或者 RpcHeartBeatRequest
        out.add(o);
    }
}
