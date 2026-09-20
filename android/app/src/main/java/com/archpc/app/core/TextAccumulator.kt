package com.archpc.app.core

class TextAccumulator(private val onLine: (String) -> Unit) {
    private val sb = StringBuilder()

    fun push(chunk: String) {
        sb.append(chunk)
        var i: Int
        while (sb.indexOf("\n").also { i = it } >= 0) {
            val line = sb.substring(0, i)
            sb.delete(0, i + 1)
            onLine(line.trimEnd('\r'))
        }
        if (sb.length >= 65536) flush()
    }

    fun flush() {
        if (sb.isNotEmpty()) {
            onLine(sb.toString())
            sb.setLength(0)
        }
    }
}
