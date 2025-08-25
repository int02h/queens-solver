package com.dpforge.easyraster

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.io.File
import java.net.InetSocketAddress

class WebServer {
    fun start(port: Int = 0) {
        val dbPool = File("field-db").listFiles()
            .orEmpty()
            .map { it.nameWithoutExtension.drop(4).toInt() to it }
            .associate { (size, file) -> size to FieldDB(file) }
        val server = HttpServer.create(InetSocketAddress(port), 0)
        server.createContext("/game/", GameHandler(dbPool))
        server.createContext("/health/", HealthHandler())
        server.createContext("/", MainHandler(dbPool))
        server.start()
        println("Web server started on port: ${server.address.port}")
        println("http://localhost:${server.address.port}")
    }
}

class MainHandler(private val dbPool: Map<Int, FieldDB>) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.getPath()
        println("request: ${exchange.requestURI}")
        val filename = path.substring(path.lastIndexOf('/') + 1)
        if (filename.isEmpty()) {
            val template = File("web-content", "index.template.html").readText()
            val buttons = buildString {
                dbPool.forEach { (size, db) ->
                    val path = makeFieldUrlPath(db.getRandomField())
                    append("<button onclick=\"location.pathname='$path'\">${size}x${size}</button>")
                }
            }
            val response = template.replace("{{BUTTONS}}", buttons).toByteArray()

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

class GameHandler(private val dbPool: Map<Int, FieldDB>) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val path = exchange.requestURI.getPath()
        val encodedField = path.substring(path.lastIndexOf('/') + 1)
        val field = try {
            FieldCodec.decodeFromCompressedString(encodedField)
        } catch (_: Exception) {
            null
        }
        if (field != null) {
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
        val nextField = dbPool[field.size]?.getRandomField()
        template = template
            .replace("{{FIELD}}", table)
            .replace("{{FIELD_SIZE}}", "${field.size}")
            .replace("{{FIELD_WIDTH_PX}}", getFieldWidth(field.size))
            .replace("{{SOLUTION}}", encodedSolution)
            .replace("{{URL_NEXT}}", nextField?.let(::makeFieldUrlPath).orEmpty())
        return template.toByteArray()
    }

    private fun getFieldWidth(fieldSize: Int): String {
        val borders = (fieldSize + 1)
        return "${fieldSize * 48 + borders}px"
    }

}

class HealthHandler : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        println("Health check ${exchange.requestURI}")
        val ok = "OK"
        exchange.responseHeaders.set("Content-Type", "text/html; charset=UTF-8")
        exchange.sendResponseHeaders(200, ok.length.toLong())
        exchange.responseBody.use { os ->
            os.write(ok.toByteArray())
        }
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