package io.github.zhangliangbo.external.inner.downloader;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author zhangliangbo
 * @since 2023-02-04
 */
public class ClientDownloadFileInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel sc) throws Exception {
        ChannelPipeline pipeline = sc.pipeline();
        pipeline.addLast("codec", new HttpClientCodec());
        pipeline.addLast(new ClientDownloadFileHandler());
    }

}
