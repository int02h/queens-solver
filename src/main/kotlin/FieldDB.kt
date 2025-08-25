package com.dpforge.easyraster

import java.io.File
import java.io.RandomAccessFile
import kotlin.random.Random

class FieldDB(file: File) {

    private val raf = RandomAccessFile(file, "rw")
    private val entryBuffer = ByteArray(ENTRY_SIZE)

    fun addField(field: Field) {
        raf.seek(raf.length())
        val encoded = FieldCodec.encodeToCompressedString(field)
        val entry = encoded.padEnd(ENTRY_SIZE, padChar = ' ')
        raf.write(entry.toByteArray(Charsets.US_ASCII))
        raf.fd.sync()
    }

    fun getField(index: Int): Field {
        raf.seek((index * ENTRY_SIZE).toLong())
        raf.read(entryBuffer)
        val entry = entryBuffer.toString(Charsets.US_ASCII).trimEnd()
        return FieldCodec.decodeFromCompressedString(entry)
    }

    fun getRandomField(random: Random = Random): Field {
        val entryCount = raf.length().toInt() / ENTRY_SIZE
        val index = random.nextInt(0, entryCount)
        return getField(index)
    }

    fun getFieldCount(): Int {
        return raf.length().toInt() / ENTRY_SIZE
    }

    companion object {
        private const val ENTRY_SIZE = 128
    }

}