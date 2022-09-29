package top.iceclean.chatspace.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import top.iceclean.chatspace.websocket.handler.AuthHandler;
import top.iceclean.chatspace.websocket.handler.ChatHandler;

/**
 * Websocket 服务端，由 Netty 构建
 * @author : Ice'Clean
 * @date : 2022-09-29
 */
public class WebSocketServer {
    /** 服务端端口 */
    private final Integer port;
    /** 服务通道 */
    private Channel channel;

    /** 读超时，60s */
    private static final int READ_IDLE_TIME_OUT = 60;
    /** 写超时，无 */
    private static final int WRITE_IDLE_TIME_OUT = 0;
    /** 所有超时，无 */
    private static final int ALL_IDLE_TIME_OUT = 0;

    public WebSocketServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        // Netty 自带 Http 编解码器
                        .addLast(new HttpServerCodec())
                        // 用于处理大数据流的块传输处理器
                        .addLast(new ChunkedWriteHandler())
                        // Http 数据聚合器
                        .addLast(new HttpObjectAggregator(64 * 1024))
                        // 增加一个权限校验处理器
                        .addLast("AuthIn", new AuthHandler.Inbound())
                        .addLast("AuthOut", new AuthHandler.Outbound())
                        // WebSocket 数据压缩
                        .addLast(new WebSocketServerCompressionHandler())
                        // WebSocket 处理器
                        .addLast(new WebSocketServerProtocolHandler("/space/ws/chat", "token", true, 10 * 1024))
                        // 心跳机制
                        .addLast(new IdleStateHandler(READ_IDLE_TIME_OUT, WRITE_IDLE_TIME_OUT, ALL_IDLE_TIME_OUT))
                        // 聊天业务处理器
                        .addLast(new ChatHandler());
            }
        });

        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            channel = future.channel();
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void stop() {

    }
}
