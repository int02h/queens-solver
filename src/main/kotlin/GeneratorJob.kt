package com.dpforge.easyraster

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.random.Random

object GeneratorJob {

    private val buffer7: BlockingQueue<Field> = ArrayBlockingQueue(25)
    private val buffer8: BlockingQueue<Field> = ArrayBlockingQueue(25)
    private val buffer9: BlockingQueue<Field> = ArrayBlockingQueue(25)
    private val buffer10: BlockingQueue<Field> = ArrayBlockingQueue(25)

    fun start() {
        Thread { startGeneration(buffer7, 7) }.start()
        Thread { startGeneration(buffer8, 8) }.start()
        Thread { startGeneration(buffer9, 9) }.start()
        Thread { startGeneration(buffer10, 10) }.start()
        log("Started")
    }

    fun getField(size: Int): Field {
        log("Field of size $size requested")
        val field = when (size) {
            7 -> buffer7.take()
            8 -> buffer8.take()
            9 -> buffer9.take()
            10 -> buffer10.take()
            else -> error("Unsupported field size: $size")
        }
        log("Field of size $size returned")
        return field
    }

    private fun startGeneration(buffer: BlockingQueue<Field>, size: Int) {
        while (true) {
            val field = Generator(Random(System.nanoTime())).generate(size)
            buffer.put(field)
            log("Field of size $size generated. Buffer size: ${buffer.size}")
        }
    }

    private fun log(message: String) {
        println("[GENERATION JOB] $message")
    }

}