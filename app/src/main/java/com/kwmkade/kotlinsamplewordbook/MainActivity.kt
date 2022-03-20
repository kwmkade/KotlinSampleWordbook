package com.kwmkade.kotlinsamplewordbook

import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    private lateinit var _sampleLabel: TextView
    private lateinit var _buttonOpenDialog: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this._sampleLabel = findViewById(R.id.sampleLabel)
        this._buttonOpenDialog = findViewById(R.id.button_open_dialog)
        setButtonOpenDialogEnabled(false)

        if (checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setButtonOpenDialogEnabled(true)
        } else {
            activateButtonOpenDialogWithPermissionCheck()
        }

        this._buttonOpenDialog.setOnClickListener { _ ->
            Log.d("Debug", "test")

            val dialog = SpeechRecognizerDialogFragment()
            dialog.show(supportFragmentManager, "aaa")
        }
    }

    private fun setButtonOpenDialogEnabled(bEnabled: Boolean) {
        runOnUiThread {
            _buttonOpenDialog.isEnabled = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun addText(text: String) {
        runOnUiThread {
            val curr = this._sampleLabel.text
            this._sampleLabel.text = "$curr\n$text"
        }
    }

    @NeedsPermission(RECORD_AUDIO)
    fun activateButtonOpenDialog() {
        setButtonOpenDialogEnabled(true)
    }

    @OnShowRationale(RECORD_AUDIO)
    fun onCameraShowRationale(request: PermissionRequest) {
        AlertDialog.Builder(this)
            .setPositiveButton("許可") { _, _ -> request.proceed() }
            .setNegativeButton("許可しない") { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage("マイクを利用します")
            .show()
    }
}