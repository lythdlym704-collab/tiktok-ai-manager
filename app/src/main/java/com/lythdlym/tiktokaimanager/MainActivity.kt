package com.lythdlym.tiktokaimanager

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var web: WebView
    private var tts: TextToSpeech? = null
    private var ttsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.allowFileAccess = true
        web.webViewClient = WebViewClient()
        web.webChromeClient = WebChromeClient()
        web.addJavascriptInterface(NativeVoice(), "AndroidTTS")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ar", "SA"))
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
            tts?.setSpeechRate(0.90f)
        }
    }

    inner class NativeVoice {
        @JavascriptInterface
        fun speak(text: String, rate: Float) {
            runOnUiThread {
                if (!ttsReady) {
                    Toast.makeText(this@MainActivity,
                        "ثبّت صوتاً عربياً من إعدادات تحويل النص إلى كلام في الهاتف",
                        Toast.LENGTH_LONG).show()
                    return@runOnUiThread
                }
                tts?.setSpeechRate(rate.coerceIn(0.75f, 1.05f))
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "story-${System.currentTimeMillis()}")
            }
        }
        @JavascriptInterface
        fun stop() { runOnUiThread { tts?.stop() } }
    }

    @Deprecated("Deprecated in Android API")
    override fun onBackPressed() {
        if (::web.isInitialized && web.canGoBack()) web.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        if (::web.isInitialized) web.destroy()
        super.onDestroy()
    }
}
