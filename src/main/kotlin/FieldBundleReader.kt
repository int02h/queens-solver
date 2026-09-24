package com.dpforge.easyraster

import java.io.File
import java.io.RandomAccessFile
import java.nio.charset.StandardCharsets

class FieldBundleReader(
    file: File,
) {

    private val raf = RandomAccessFile(file, "rw")
    private val count: Int

    init {
        raf.seek(0)
        count = raf.readInt()
    }

    fun getItem(index: Int): Field {
        raf.seek(4L + 4L * index)
        val offset = raf.readInt()
        raf.seek(offset.toLong())
        return readItem()
    }

    private fun readItem(): Field {
        val encodedLength = raf.read()
        val encodedBytes = ByteArray(encodedLength)
        raf.read(encodedBytes)
        val encoded = String(encodedBytes, StandardCharsets.US_ASCII)
        val field = FieldCodec.decodeFromCompressedString(encoded)
        return field
    }

}