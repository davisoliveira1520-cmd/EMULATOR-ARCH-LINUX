package com.archpc.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.archpc.app.core.Distro

class MainActivity : AppCompatActivity() {

    private lateinit var txtStatus: TextView
    private lateinit var btnInstall: Button
    private lateinit var btnTerminal: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtStatus = findViewById(R.id.txt_status)
        btnInstall = findViewById(R.id.btn_install)
        btnTerminal = findViewById(R.id.btn_terminal)

        btnInstall.setOnClickListener {
            startActivity(Intent(this, InstallerActivity::class.java))
        }
        btnTerminal.setOnClickListener {
            startActivity(Intent(this, TerminalActivity::class.java))
        }
        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        if (Distro.isInstalled(this)) {
            txtStatus.setText(R.string.home_installed_yes)
            btnTerminal.isEnabled = true
        } else {
            txtStatus.setText(R.string.home_installed_no)
            btnTerminal.isEnabled = false
        }
    }
}
