package multiplier

import HttpHelloWorldServerHandler
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpContentCompressor
import io.netty.handler.codec.http.HttpServerCodec
import io.netty.handler.codec.http.HttpServerExpectContinueHandler


class HttpHelloWorldServerInitializer : ChannelInitializer<SocketChannel>() {
    public override fun initChannel(ch: SocketChannel) {
        val p = ch.pipeline()
        p.addLast(HttpServerCodec())
        p.addLast(HttpContentCompressor())
        p.addLast(HttpServerExpectContinueHandler())
        p.addLast(HttpHelloWorldServerHandler())
    }
}