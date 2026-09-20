package com.archpc.app

data class DistroState(
    val rootfsDir: java.io.File,
    val installed: Boolean,
    val presentFiles: Int,
)

object ArchMeta {
    const val knSrc = "https://geo.mirror.pkgbuild.com/iso/latest/"
    const val knBootstrapZst = "archlinux-bootstrap-x86_64.tar.zst"

    const val knArmSrc = "https://mirror.archlinuxarm.org/os/"
    const val knArmTarGz = "ArchLinuxARM-aarch64-latest.tar.gz"
}
