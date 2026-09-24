package com.dpforge.easyraster

import java.io.File
import java.io.RandomAccessFile

class FieldBundleWriter(
    private val file: File,
) {

    fun write(fields: List<Field>) {
        RandomAccessFile(file, "rw").use { raf ->
            raf.writeInt(fields.size)
            raf.write(ByteArray(4 * fields.size)) // index table

            val offsetTable = IntArray(fields.size)
            for ((index, field) in fields.withIndex()) {
                val offset = raf.filePointer.toInt()
                writeItem(raf, field)
                offsetTable[index] = offset
            }

            raf.seek(4)
            offsetTable.forEach { index -> raf.writeInt(index) }
            raf.fd.sync()
        }
    }

    private fun writeItem(raf: RandomAccessFile, field: Field) {
        val encoded = FieldCodec.encodeToCompressedString(field)
        raf.write(encoded.length and 0xFF)
        raf.write(encoded.toByteArray(Charsets.US_ASCII))
    }
}