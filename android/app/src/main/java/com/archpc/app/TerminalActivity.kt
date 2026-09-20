package com.archpc.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.archpc.app.core.Distro
import com.archpc.app.core.ShellProc
import com.archpc.app.core.TextAccumulator
import com.archpc.app.installer.PathMeta
import java.io.File

class TerminalActivity : AppCompatActivity() {

    private lateinit var txtOutput: TextView
    private lateinit var txtInput: EditText
    private lateinit var btnSend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminal)

        txtOutput = findViewById(R.id.txt_output)
        txtInput = findViewById(R.id.txt_input)
        btnSend = findViewById(R.id.btn_send)

        val accum = TextAccumulator { line -> runOnUiThread { txtOutput.append(line + "\n") } }
        val env = Distro.environment(this)
        val proot = PathMeta.PROOT
        val rootfs = Distro.rootfs(this)

        btnSend.setOnClickListener {
            val line = txtInput.text.toString()
            if (line.isBlank()) return@setOnClickListener
            txtOutput.append("$ $line\n")
            txtInput.setText("")
            thread {
                try {
                    ShellProc.interactive(proot, rootfs, env, listOf("/bin/bash", "-lc", line)) {
                        runOnUiThread { accum.push(it) }
                    }
                } catch (e: Exception) {
                    runOnUiThread { txtOutput.append("! ${e.message}\n") }
                }
            }
        }
    }
}
