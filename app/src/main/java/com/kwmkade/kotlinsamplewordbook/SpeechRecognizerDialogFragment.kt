package com.kwmkade.kotlinsamplewordbook

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class SpeechRecognizerDialogFragment : DialogFragment() {

    private lateinit var _textViewAnnounce: TextView
    private var speechRecognizer: SpeechRecognizer? = null
    private val mHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_speech_recognizer_dialog, container, false)
        this._textViewAnnounce = view.findViewById(R.id.text_announce)
        setAnnounceText("")
        return view
    }

    override fun onStart() {
        super.onStart()
        buildSpeechRecognizer()
        startListening()
    }

    override fun onStop() {
        super.onStop()
        speechRecognizer?.stopListening()
    }

    private fun buildSpeechRecognizer() {
        if (activity != null) {
            this.speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(activity?.applicationContext)
            this.speechRecognizer?.setRecognitionListener(createRecognitionListenerStringStream {
                //addText(it)
                Log.d("RecognitionListener", it)
            })
        }
    }

    private fun setAnnounceText(message: String) {
        this._textViewAnnounce.text = message
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, activity?.packageName)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en")
        speechRecognizer?.startListening(intent)
    }


    private fun createRecognitionListenerStringStream(onResult: (String) -> Unit): RecognitionListener {
        return object : RecognitionListener {

            // サウンドレベルの変更時に呼ばれる。
            override fun onRmsChanged(rmsdB: Float) {
                //Log.d("RecognitionListener", "onRmsChanged")
            }

            // スピーチの傾聴の準備完了時に呼ばれる。
            override fun onReadyForSpeech(params: Bundle) {
                Log.d("RecognitionListener", "onReadyForSpeech")
                setAnnounceText("Ready")
            }

            // 音の受信時に呼ばれる。引数は単一チャネルのオーディオストリームを表すバッファです。
            override fun onBufferReceived(buffer: ByteArray) {
                Log.d("RecognitionListener", "onBufferReceived")
            }

            // 部分認識結果の受信時に呼ばれる。
            override fun onPartialResults(partialResults: Bundle) {
                Log.d("RecognitionListener", "onPartialResults")
            }

            // イベント受信時に呼ばれる。
            override fun onEvent(eventType: Int, params: Bundle) {
                Log.d("RecognitionListener", "onEvent")
            }

            // スピーチの傾聴の開始時に呼ばれる。
            override fun onBeginningOfSpeech() {
                Log.d("RecognitionListener", "onBeginningOfSpeech")
                setAnnounceText("Now recording ...")
            }

            override fun onEndOfSpeech() {
                Log.d("RecognitionListener", "onEndOfSpeech")

                setAnnounceText("Stop")

                speechRecognizer?.stopListening()
                startListening()
            }

            override fun onError(error: Int) {

                val errorMessage = when (error) {
                    // 録音エラー
                    SpeechRecognizer.ERROR_AUDIO -> "ERROR_AUDIO"
                    // その他のクライアント側のエラー
                    SpeechRecognizer.ERROR_CLIENT -> "ERROR_CLIENT"
                    // 権限が不十分
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "ERROR_INSUFFICIENT_PERMISSIONS"
                    // 要求された言語を使用できない
                    SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED -> "ERROR_LANGUAGE_NOT_SUPPORTED"
                    // 要求された言語はサポートされているが、現在利用できない（ダウンロードされていないなど）
                    SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> "ERROR_LANGUAGE_UNAVAILABLE"
                    // その他のネットワーク関連のエラー
                    SpeechRecognizer.ERROR_NETWORK -> "ERROR_NETWORK"
                    // ネットワーク操作のタイムアウト
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "ERROR_NETWORK_TIMEOUT"
                    // 一致する認識結果はない
                    SpeechRecognizer.ERROR_NO_MATCH -> "ERROR_NO_MATCH"
                    // RecognitionServiceがビジー
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "ERROR_RECOGNIZER_BUSY"
                    // サーバーエラー。エラーステータスを送信
                    SpeechRecognizer.ERROR_SERVER -> "ERROR_SERVER"
                    // サーバーが切断
                    SpeechRecognizer.ERROR_SERVER_DISCONNECTED -> "ERROR_SERVER_DISCONNECTED"
                    // 音声入力なし
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "ERROR_SPEECH_TIMEOUT"
                    // 同じクライアントからのリクエストが多すぎる
                    SpeechRecognizer.ERROR_TOO_MANY_REQUESTS -> "ERROR_TOO_MANY_REQUESTS"
                    else -> "UNKNOWN"
                }


                Log.d("RecognitionListener", "onError ($error) [$errorMessage]")

                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    speechRecognizer?.stopListening()
                    speechRecognizer?.destroy()

                    mHandler.postDelayed({
                        buildSpeechRecognizer()
                        startListening()
                    }, 1000)
                }
            }

            override fun onResults(results: Bundle) {
                val stringArray =
                    results.getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)
                onResult("onResults " + stringArray.toString())

                speechRecognizer?.stopListening()
                startListening()
            }
        }
    }
}