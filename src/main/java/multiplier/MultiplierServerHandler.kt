import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFutureListener
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.*


class MultiplierServerHandler : SimpleChannelInboundHandler<HttpObject?>() {
    override fun channelReadComplete(ctx: ChannelHandlerContext) {
        ctx.flush()
    }

    public override fun channelRead0(ctx: ChannelHandlerContext, msg: HttpObject?) {
        if (msg is HttpRequest) {
            val req = msg
            val keepAlive = HttpUtil.isKeepAlive(req)

            // Extract query parameters
            val queryParams = getQueryParams(req.uri())
            val num1 = queryParams["num1"]?.toIntOrNull() ?: 0
            val num2 = queryParams["num2"]?.toIntOrNull() ?: 0

            // Perform multiplication
            val result = num1 * num2

            val responseContent = "Result: $result"
            val response: FullHttpResponse = DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(responseContent.toByteArray()))

            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                    .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes())

            if (keepAlive) {
                if (!req.protocolVersion().isKeepAliveDefault) {
                    response.headers()[HttpHeaderNames.CONNECTION] = HttpHeaderValues.KEEP_ALIVE
                }
            } else {
                response.headers()[HttpHeaderNames.CONNECTION] = HttpHeaderValues.CLOSE
            }

            val f = ctx.write(response)
            if (!keepAlive) {
                f.addListener(ChannelFutureListener.CLOSE)
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        cause.printStackTrace()
        ctx.close()
    }

    private fun getQueryParams(uri: String): Map<String, String> {
        val queryParams = mutableMapOf<String, String>()
        val queryString = uri.split("?", limit = 2).getOrNull(1)
        if (queryString != null) {
            val pairs = queryString.split("&")
            for (pair in pairs) {
                val keyValuePair = pair.split("=", limit = 2)
                if (keyValuePair.size == 2) {
                    val key = keyValuePair[0]
                    val value = keyValuePair[1]
                    queryParams[key] = value
                }
            }
        }
        return queryParams
    }

    companion object {
        private val CONTENT = byteArrayOf(Byte.MIN_VALUE)
    }
}
