package com.dpforge.easyraster

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import kotlin.random.Random

object GeneratorJob {

    private val buffers: Map<Int, BlockingQueue<Field>> =
        Constants.SUPPORTED_SIZES.associateWith { ArrayBlockingQueue(25) }

    fun start() {
        Constants.SUPPORTED_SIZES.forEach { size ->
            Thread { startGeneration(size) }.start()
        }
        log("Started")
    }

    fun getField(size: Int): Field {
        log("Field of size $size requested")
        val field = buffers.getValue(size).take()
        log("Field of size $size returned")
        return field
    }

    private fun startGeneration(size: Int) {
        val buffer = buffers.getValue(size)
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