package com.dpforge.easyraster

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.File
import java.net.InetSocketAddress


class WebServer {
    fun start(port: Int = 0) {
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext("/", MainHandler())
        server.start()
        println("Web server started on port: ${server.address.port}")
        println("http://localhost:${server.address.port}")
    }
}

class MainHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.getPath()
        val filename = path.substring(path.lastIndexOf('/') + 1)
        if (filename.isEmpty()) {
            val response = generateIndexHtml()
            exchange.responseHeaders.set("Content-Type", "text/html; charset=UTF-8")
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.use { os ->
                os.write(response)
            }
        } else {
            val file = File("web-content", filename)
            if (file.exists()) {
                val response = file.readBytes()
                exchange.responseHeaders.set("Content-Type", getContextType(filename))
                exchange.sendResponseHeaders(200, response.size.toLong())
                exchange.responseBody.use { os ->
                    os.write(response)
                }
            } else {
                val notFound = "<h1>404 Not Found</h1>"
                exchange.responseHeaders.set("Content-Type", "text/html; charset=UTF-8")
                exchange.sendResponseHeaders(404, notFound.length.toLong())
                exchange.responseBody.use { os ->
                    os.write(notFound.toByteArray())
                }
            }
        }
    }

    private fun generateIndexHtml(): ByteArray {
        val field = FieldCodec.decodeFromCompressedString(
            "3w2g3vyd2pwgb3vydpo2wb3vydpo2w4lydpo2w4l2ypo2w4l2yp2o3wlr2y3p3wlr2y4p2w3ry5p4wy"
        )
        var template = File("web-content", "index.template.html").readText()
        val table = buildString {
            for (row in 0 until field.size) {
                append("<tr>")
                for (col in 0 until field.size) {
                    val cell = field.colorRegions.getColor(Position(row, col))
                    append("<td><div id=\"row${row}-col${col}\" class=\"cell ${cell.name.lowercase()}\"></div></td>")
                }
                append("</tr>\n")
            }
        }
        template = template
            .replace("{{FIELD}}", table)
            .replace("{{FIELD_SIZE}}", "${field.size}")
        return template.toByteArray()
    }

}

private fun getContextType(filename: String): String {
    if (filename.endsWith(".js")) {
        return "application/javascript; charset=UTF-8"
    } else if (filename.endsWith(".css")) {
        return "text/css; charset=UTF-8"
    } else if (filename.endsWith(".html")) {
        return "text/html; charset=UTF-8"
    }
    error("Unsupported file type")
}

private fun Map<Color, Set<Position>>.getColor(pos: Position): Color {
    for ((color, posSet) in this) {
        if (posSet.contains(pos)) {
            return color
        }
    }
    error("Cell not found: $pos")
}