package com.archpc.app

import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.archpc.app.core.Distro
import com.archpc.app.installer.ArchBootstrap
import kotlin.concurrent.thread

class InstallerActivity : AppCompatActivity() {

    private lateinit var txtLog: TextView
    private lateinit var progress: ProgressBar
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_installer)

        txtLog = findViewById(R.id.txt_log)
        progress = findViewById(R.id.progress)
        btnConfirm = findViewById(R.id.btn_confirm)

        btnConfirm.setOnClickListener { install() }
    }

    private fun install() {
        btnConfirm.isEnabled = false
        thread {
            val ok = runCatching {
                ArchBootstrap.install(this, ::appendLog) { p -> runOnUiThread { progress.progress = (p * 100).toInt() } }
            }.isSuccess
            runOnUiThread {
                btnConfirm.isEnabled = true
                btnConfirm.setText(
                    if (ok) R.string.install_ok else R.string.install_error
                )
            }
        }
    }

    private fun appendLog(line: String) {
        runOnUiThread { txtLog.append(line + "\n") }
    }
}
