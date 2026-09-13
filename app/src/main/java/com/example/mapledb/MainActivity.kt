package com.example.mapledb

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * 冒险岛怀旧服数据工具 - 原生外壳
 * WebView 加载内嵌的 tool.html（离线可用），并通过原生桥接联网抓取最新数据（绕过浏览器 CORS 限制）。
 */
class MainActivity : Activity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.webViewClient = WebViewClient()
        webView.addJavascriptInterface(AndroidBridge(), "AndroidBridge")
        webView.loadUrl("file:///android_asset/tool.html")
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    inner class AndroidBridge {

        /** 供 JS 调用的异步网络请求：结果通过 window.__bridgeCallback(requestId, err, text) 回传 */
        @JavascriptInterface
        fun fetchText(requestId: String, url: String) {
            thread {
                var err = ""
                var text = ""
                try {
                    val conn = URL(url).openConnection() as HttpURLConnection
                    conn.connectTimeout = 15000
                    conn.readTimeout = 25000
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 10) MapleClassicDB/1.0")
                    conn.setRequestProperty("Accept", "application/json, text/html, */*")
                    val code = conn.responseCode
                    if (code in 200..299) {
                        text = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                    } else {
                        err = "HTTP $code"
                    }
                    conn.disconnect()
                } catch (e: Exception) {
                    err = e.message ?: "网络错误"
                }
                val finalErr = err
                val finalText = text
                runOnUiThread {
                    val js = "window.__bridgeCallback(${JSONObject.quote(requestId)}, ${JSONObject.quote(finalErr)}, ${JSONObject.quote(finalText)})"
                    webView.evaluateJavascript(js, null)
                }
            }
        }

        /** 持久化更新后的数据到应用内部存储 */
        @JavascriptInterface
        fun saveData(json: String) {
            try {
                File(filesDir, "data.json").writeText(json, Charsets.UTF_8)
            } catch (_: Exception) {
            }
        }

        /** 读取上次持久化的数据，无则返回空串 */
        @JavascriptInterface
        fun loadData(): String {
            return try {
                val f = File(filesDir, "data.json")
                if (f.exists()) f.readText(Charsets.UTF_8) else ""
            } catch (_: Exception) {
                ""
            }
        }
    }
}
