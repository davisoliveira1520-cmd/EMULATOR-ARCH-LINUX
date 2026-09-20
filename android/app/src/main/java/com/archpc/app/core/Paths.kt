package com.archpc.app.core

import java.io.File

object Paths {

    const val ROOT_DIR = "arch"
    const val PROOT_DIR = "proot"
    const val TAG = "ArchPC"

    fun root(ctx: android.content.Context): File = File(ctx.filesDir, ROOT_DIR)

    fun rootfs(ctx: android.content.Context): File {
        val abi = android.os.Build.SUPPORTED_ABIS.first()
        val arch = if (abi.startsWith("arm") || abi == "aarch64") "aarch64" else "x86_64"
        return File(root(ctx), "root.$arch")
    }

    fun proot(ctx: android.content.Context): File = File(root(ctx), "$PROOT_DIR/bin/proot")

    fun prootLibs(ctx: android.content.Context): File = File(root(ctx), PROOT_DIR)
}
