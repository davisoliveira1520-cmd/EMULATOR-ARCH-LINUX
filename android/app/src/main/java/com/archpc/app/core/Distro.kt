package com.archpc.app.core

import android.content.Context

object Distro {

    private const val PREF = "archpc"

    fun isInstalled(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean("installed", false)

    fun markInstalled(ctx: Context, arch: String) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putBoolean("installed", true).putString("arch", arch).apply()
    }

    fun wipe(ctx: Context) {
        Paths.root(ctx).deleteRecursively()
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putBoolean("installed", false).apply()
    }

    fun freeSpaceMb(ctx: Context): Long {
        val stat = android.os.StatFs(ctx.filesDir.absolutePath)
        return stat.availableBytes / (1024 * 1024)
    }
}
