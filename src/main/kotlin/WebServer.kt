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
        server.createContext("/game/", GameHandler())
        server.start()
        GeneratorJob.start()
        println("Web server started on port: ${server.address.port}")
        println("http://localhost:${server.address.port}")
    }
}

class MainHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.getPath()
        val filename = path.substring(path.lastIndexOf('/') + 1)
        if (filename.isEmpty()) {
            val template = File("web-content", "index.template.html").readText()
            val response = template
                .replace("{{URL_7x7}}", makeFieldUrlPath(GeneratorJob.getField(7)))
                .replace("{{URL_8x8}}", makeFieldUrlPath(GeneratorJob.getField(8)))
                .replace("{{URL_9x9}}", makeFieldUrlPath(GeneratorJob.getField(9)))
                .replace("{{URL_10x10}}", makeFieldUrlPath(GeneratorJob.getField(10)))
                .toByteArray()
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
                error404(exchange)
            }
        }
    }
}

class GameHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.getPath()
        val encodedField = path.substring(path.lastIndexOf('/') + 1)
        val field = try {
            FieldCodec.decodeFromCompressedString(encodedField)
        } catch (_: Exception) {
            null
        }
        if (field != null) {
            FieldHistory.add(field)
            val response = generateGameHtml(field)
            exchange.responseHeaders.set("Content-Type", "text/html; charset=UTF-8")
            exchange.sendResponseHeaders(200, response.size.toLong())
            exchange.responseBody.use { os ->
                os.write(response)
            }
        } else {
            error404(exchange)
        }
    }

    private fun generateGameHtml(field: Field): ByteArray {
        var template = File("web-content", "game.template.html").readText()
        val table = buildString {
            for (row in 0 until field.size) {
                append("<tr>")
                for (col in 0 until field.size) {
                    val color = field.colorRegions.getColor(Position(row, col))
                    val id = "row${row}-col${col}"
                    val colorName = color.name.lowercase()
                    val cssClass = "cell $colorName"
                    append("<td><div id=\"$id\" class=\"$cssClass\" data-color=\"${colorName}\"></div></td>")
                }
                append("</tr>\n")
            }
        }
        val solution = try {
            Solver().solveField(field)
        } catch (e: Exception) {
            val encodedField = FieldCodec.encodeToCompressedString(field)
            System.err.println("Probable field is not solvable: ${e.message}\nField: $encodedField")
            SolutionFinder(exitEarlier = true).findAllSolutions(field).first()
        }
        val encodedSolution = solution
            .map { "${it.row}:${it.col}" }
            .sorted()
            .joinToString(",")
        val nextField = GeneratorJob.getField(field.size)
        template = template
            .replace("{{FIELD}}", table)
            .replace("{{FIELD_SIZE}}", "${field.size}")
            .replace("{{SOLUTION}}", encodedSolution)
            .replace("{{URL_NEXT}}", makeFieldUrlPath(nextField))
        return template.toByteArray()
    }

}

private fun error404(exchange: HttpExchange) {
    val notFound = "<h1>404 Not Found</h1>"
    exchange.responseHeaders.set("Content-Type", "text/html; charset=UTF-8")
    exchange.sendResponseHeaders(404, notFound.length.toLong())
    exchange.responseBody.use { os ->
        os.write(notFound.toByteArray())
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

private fun makeFieldUrlPath(field: Field): String {
    val compressed = FieldCodec.encodeToCompressedString(field)
    return "/game/$compressed"
}

private fun Map<Color, Set<Position>>.getColor(pos: Position): Color {
    for ((color, posSet) in this) {
        if (posSet.contains(pos)) {
            return color
        }
    }
    error("Cell not found: $pos")
}