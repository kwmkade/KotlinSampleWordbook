package com.kwmkade.kotlinsamplewordbook

import android.Manifest.permission.RECORD_AUDIO
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnShowRationale
import permissions.dispatcher.PermissionRequest
import permissions.dispatcher.RuntimePermissions

@RuntimePermissions
class MainActivity : AppCompatActivity() {

    private lateinit var _sampleLabel: TextView
    private lateinit var _startToggleButton: ToggleButton
    private var speechRecognizer: SpeechRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this._sampleLabel = findViewById(R.id.sampleLabel)
        this._startToggleButton = findViewById(R.id.startToggleButton)
        setStartToggleButtonEnabled(false)

        this._startToggleButton.setOnCheckedChangeListener { _, bOn ->

            if (bOn && this.speechRecognizer == null) {
                this.speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)
                this.speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream {
                    addText(it)
                })
            }

            if (bOn) {
                startListening()
            } else {
                speechRecognizer?.stopListening()
            }
        }

        if (checkSelfPermission(RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            setStartToggleButtonEnabled(true)
        } else {
            setupRecognizerWithPermissionCheck()
        }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
        speechRecognizer?.startListening(intent)
    }

    private fun setStartToggleButtonEnabled(bEnabled: Boolean) {
        runOnUiThread {
            _startToggleButton.isEnabled = true
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
    fun setupRecognizer() {
        setStartToggleButtonEnabled(true)
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

    private fun createRecognitionListenerStringStream(onResult: (String) -> Unit): RecognitionListener {
        return object : RecognitionListener {
            override fun onRmsChanged(rmsdB: Float) {
                /** 今回は特に利用しない */
            }

            override fun onReadyForSpeech(params: Bundle) {
                onResult("onReadyForSpeech")
            }

            override fun onBufferReceived(buffer: ByteArray) {
                onResult("onBufferReceived")
            }

            override fun onPartialResults(partialResults: Bundle) {
                onResult("onPartialResults")
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                onResult("onEvent")
            }

            override fun onBeginningOfSpeech() {
                onResult("onBeginningOfSpeech")
            }

            override fun onEndOfSpeech() {
                onResult("onEndOfSpeech")
            }

            override fun onError(error: Int) {
                onResult("onError")
            }

            override fun onResults(results: Bundle) {
                val stringArray =
                    results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                onResult("onResults " + stringArray.toString())
                startListening()
            }
        }
    }
}