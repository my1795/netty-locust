package multiplier

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel


object MultiplierServer {
    const val PORT = 9595

    @JvmStatic
    fun main(args: Array<String>) {
        val bossGroup = NioEventLoopGroup(4)
        val workerGroup = NioEventLoopGroup(512)
        try {
            val b = ServerBootstrap()
            b.option(ChannelOption.SO_BACKLOG, 1024)
            b.option(ChannelOption.TCP_NODELAY, true)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel::class.java)
                    .childHandler(HttpHelloWorldServerInitializer())
            val ch = b.bind(PORT).sync().channel()
            ch.closeFuture().sync()
        } finally {
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
    }
}