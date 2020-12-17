package cool.spongecaptain.transport.handler;

import cool.spongecaptain.transport.server.handler.HeatBeatRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * 这是 客户端 & 服务端共用的一个 Handler，用于在失去通信时及时关闭连接
 */
public class MyIdleStateHandler extends IdleStateHandler {

    private static final Logger logger = LoggerFactory.getLogger(IdleStateHandler.class);


    //理论上客户端与服务端每 5 秒应当有至少一次的通信，因此这里断开数值设置为 11 s
    private static final int READER_IDLE_TIME = 11;

    public MyIdleStateHandler() {
        super(READER_IDLE_TIME,0,0, TimeUnit.SECONDS);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        logger.info("Doesn't read any data in "+READER_IDLE_TIME+ " s from "+ ctx.channel().remoteAddress()+", so close the channel");
        ctx.channel().close();
    }
}
