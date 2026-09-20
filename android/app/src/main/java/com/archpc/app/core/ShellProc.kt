package com.archpc.app.core

import android.util.Log
import java.io.File
import java.io.IOException

object ShellProc {
    private const val TAG = "ArchPC"

    private fun prootArgs(proot: File, rootfs: File): MutableList<String> = mutableListOf(
        proot.absolutePath,
        "--link2symlink", "-0",
        "-r", rootfs.absolutePath,
        "-b", "/dev", "-b", "/proc", "-b", "/sys",
        "-w", "/root",
        "/usr/bin/env",
        "-i",
        "HOME=/root", "TERM=xterm-256color", "LANG=C.UTF-8",
        "PATH=/usr/local/sbin:/usr/local/bin:/usr/bin:/bin",
    )

    fun run(
        proot: File,
        rootfs: File,
        cmd: List<String>,
        stdinFile: java.io.File? = null,
        onLine: (String) -> Unit = {},
    ): Int {
        val args = prootArgs(proot, rootfs)
        args.addAll(cmd)
        val pb = ProcessBuilder(args)
        pb.directory(rootfs)
        pb.redirectErrorStream(true)
        Log.i(TAG, "> " + args.joinToString(" "))

        val proc = try { pb.start() } catch (e: Exception) {
            throw IllegalStateException("Não foi possível iniciar proot: ${e.message}", e)
        }

        if (stdinFile != null) {
            thread { runCatching { proc.outputStream.write(stdinFile.readBytes()) } }
        }

        val reader = proc.inputStream.bufferedReader()
        val t = thread { try { reader.forEachLine { onLine(it) } } catch (e: IOException) {} }
        t.join()
        return proc.waitFor()
    }

    fun interactive(
        proot: File,
        rootfs: File,
        cmd: List<String>,
        nextLine: () -> String?,
        onChunk: (String) -> Unit,
    ) {
        val args = prootArgs(proot, rootfs)
        args.addAll(cmd)
        val pb = ProcessBuilder(args)
        pb.directory(rootfs)
        pb.redirectErrorStream(true)
        Log.i(TAG, "> " + args.joinToString(" "))

        val proc = try { pb.start() } catch (e: Exception) {
            throw IllegalStateException("Não foi possível iniciar proot (interativo): ${e.message}", e)
        }

        thread(name = "stdin-pump", isDaemon = true) {
            try {
                while (true) {
                    val line = nextLine() ?: break
                    proc.outputStream.write((line + "\n").toByteArray())
                    proc.outputStream.flush()
                }
            } catch (e: IOException) {
            } finally {
                runCatching { proc.outputStream.close() }
            }
        }

        val reader = proc.inputStream.bufferedReader()
        val sa = TextAccumulator(onChunk)
        thread(name = "stdout-pump") {
            try {
                val buf = CharArray(4096)
                while (true) {
                    val n = reader.read(buf)
                    if (n < 0) break
                    if (n > 0) sa.push(String(buf, 0, n))
                }
            } catch (e: IOException) {
            }
        }.join()
        sa.flush()
        runCatching { proc.waitFor() }
    }

    private fun thread(name: String? = null, isDaemon: Boolean = false, body: () -> Unit) =
        Thread(body).also { it.name = name ?: "shell"; it.isDaemon = isDaemon; it.start() }
}
