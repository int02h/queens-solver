package com.dpforge.easyraster

import java.io.File
import kotlin.random.Random

class GeneratorApp(
    folder: File,
    private val size: Int,
    private val limit: Int = 10_000,
    private val threadCount: Int = 4,
) {

    private val db = FieldDB(folder.resolve("size${size}.fdb"))

    init {
        if (!folder.exists() && !folder.mkdirs()) {
            error("Could not create folder $folder")
        }
    }

    fun start() {
        val random = Random(System.currentTimeMillis())
        repeat(threadCount) {
            Thread { startGeneration(random) }.start()
        }
        log("Started. DB size: ${db.getFieldCount()}")
    }

    private fun startGeneration(random: Random) {
        while (true) {
            val field = Generator(random).generate(size, allowEnlarging = false)
            val count = addField(field)
            if (count == limit) {
                break
            }
        }
        log("Done")
    }

    @Synchronized
    private fun addField(field: Field): Int {
        var count = db.getFieldCount()
        if (count >= limit) {
            return count
        }
        db.addField(field)
        count = db.getFieldCount()
        if (count % 10 == 0) {
            print("Field of size $size generated. Field count: $count\r")
        }
        return count
    }

    private fun log(message: String) {
        println("[GENERATION APP] $message")
    }
}