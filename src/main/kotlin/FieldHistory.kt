package com.dpforge.easyraster

import java.io.File

object FieldHistory {

    fun add(field: Field) {
        val file = getFileForSize(field.size)
        val encoded = FieldCodec.encodeToCompressedString(field)
        file.appendText("$encoded\n")
    }

    private fun getFileForSize(size: Int): File {
        val historyFolder = File("history")
        if (!historyFolder.exists() && !historyFolder.mkdirs()) {
            error("Couldn't create folder: ${historyFolder.absolutePath}")
        }
        return File(historyFolder, "size${size}.txt")
    }

}