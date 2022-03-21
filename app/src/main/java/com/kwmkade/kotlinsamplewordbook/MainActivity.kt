package com.kwmkade.kotlinsamplewordbook

import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    private lateinit var _buttonOpenDialog: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            _buttonOpenDialog.isEnabled = bEnabled
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