package com.iflytek.longformasrdemo.asr

import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.iflytek.longformasrdemo.model.AsrAudioStatus
import com.iflytek.longformasrdemo.model.AsrParams
import com.iflytek.longformasrdemo.model.AsrResponseData
import com.iflytek.longformasrdemo.util.AudioRecorder
import com.iflytek.longformasrdemo.util.AuthUtil
import com.iflytek.longformasrdemo.util.GsonUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString.Companion.toByteString
import java.util.concurrent.TimeUnit


class AsrHelper : AsrSpeech, AudioRecorder.RecorderListener {

    companion object {
        private const val TIME_OUT = 15_1000L
        private const val TAG = "AsrHelper"

        //实现一个okhttpclient
        private val okhttpClient = OkHttpClient.Builder()
            .readTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.MILLISECONDS)
            .build()
    }

    private var webSocket: WebSocket? = null

    private var asrSpeechRecognizer: AsrSpeechRecognizer? = null

    private var recorder: AudioRecorder = AudioRecorder(this)

    @Volatile
    private var audioStatus: AsrAudioStatus = AsrAudioStatus.BEGIN

    private val asrResultBuilder = StringBuffer()

    private fun getHttpClient(): OkHttpClient {
        return okhttpClient
    }


    fun setAsrSpeechRecognizer(asrSpeechRecognizer: AsrSpeechRecognizer) {
        this.asrSpeechRecognizer = asrSpeechRecognizer
    }


    override fun startSpeech() {
        val authUrl = "wss://rtasr.xfyun.cn/v1/ws" + AuthUtil.getHandShakeParams(AuthUtil.APP_ID, AuthUtil.API_KEY)
        val request = Request.Builder().url(authUrl).build()
        webSocket = getHttpClient().newWebSocket(request, webSocketListener())
    }

    override fun stopSpeech() {
        recorder.stopReadAudio()
    }

    override fun cancel() {
        webSocket?.cancel()
        webSocket = null
    }

    private fun webSocketListener() = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            Log.w(TAG, "听写wss=====>onOpen")
            //连接成功，开启麦克风
            recorder.startReadAudio()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            Log.w(TAG, "听写wss=====>onMessage: $text")
            handleWssMessage(text)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            Log.w(TAG, "听写wss=====>onClosed")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
            Log.w(TAG, "听写wss=====>onClosing")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            Log.w(TAG, "听写wss=====>onFailure")
            asrSpeechRecognizer?.onFailure()
            cancel()
            stopSpeech()
        }
    }

    private fun handleWssMessage(text: String) {
        if (TextUtils.isEmpty(text)) return
        //{"action":"started","code":"0","data":"","desc":"success","sid":"rta08404398@dx2f5f1a45c970000100"}
        val resp = GsonUtil.fromJson(text, AsrResponseData::class.java) ?: return
        if (resp.code != 0) {
            audioStatus = AsrAudioStatus.END
            asrSpeechRecognizer?.onFailure()
            stopSpeech()
            return
        }
        if (resp.action == "started" || resp.action == "result") {
            audioStatus = AsrAudioStatus.CONTINUE
            val dataStr = resp.data
            if (!TextUtils.isEmpty(dataStr)) {
                val data = GsonUtil.fromJson(dataStr, AsrResponseData.Data::class.java) ?: return
                Log.d(TAG, "实时转写结果====> ${data.toString()}")
                val resultBuilder = StringBuffer()
                val cn = data.cn
                val rt = cn?.st?.rt?: emptyArray()
                try {
                    for (rt1 in rt) {
                        val ws = rt1.ws?: emptyArray()
                        for (w in ws) {
                            val cws = w.cw ?: emptyArray()
                            for (cw in cws) {
                                val wStr = cw.w ?: ""
                                resultBuilder.append(wStr)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (cn?.st?.type == "0") {
                    asrResultBuilder.append(resultBuilder.toString())
                    asrSpeechRecognizer?.onResult(asrResultBuilder.toString())
                }else{
                    asrSpeechRecognizer?.onResult(asrResultBuilder.toString() + resultBuilder.toString())
                }
            }
        }else if (resp.action == "error") {
            recorder.stopReadAudio()
            audioStatus = AsrAudioStatus.END
            webSocket?.close(1000, "")
        }
    }

    private fun sendMessage(data: ByteArray) {
        if (webSocket == null) return
        webSocket?.send(data.toByteString())
    }

    override fun onRecordStart() {
        asrResultBuilder.setLength(0)
        audioStatus = AsrAudioStatus.BEGIN
        asrSpeechRecognizer?.onStart()
    }

    override fun onRecord(data: ByteArray?) {
        if (data == null) return
        sendMessage(data)
        audioStatus = AsrAudioStatus.CONTINUE
    }

    override fun onRecordStop() {
        if (audioStatus == AsrAudioStatus.END) return
        audioStatus = AsrAudioStatus.END
        sendMessage("{\"end\": true}".toByteArray())
    }

    override fun onRecordError() {
        cancel()
        audioStatus = AsrAudioStatus.END
        asrSpeechRecognizer?.onFailure()
    }

    fun destroy() {
        asrSpeechRecognizer = null
        recorder.destroy()
        cancel()
    }
}

interface AsrSpeechRecognizer {
    fun onStart()
    fun onResult(text: String)
    fun onFailure()
}