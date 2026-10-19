package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit

class AiVoicePlayer(private val context: Context) {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private var mediaPlayer: MediaPlayer? = null
    private var fallbackTts: TextToSpeech? = null
    private var isTtsReady = false

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _statusText = MutableStateFlow<String?>(null)
    val statusText: StateFlow<String?> = _statusText.asStateFlow()

    init {
        fallbackTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                fallbackTts?.language = Locale.FRENCH
                fallbackTts?.setPitch(1.05f)
                fallbackTts?.setSpeechRate(0.95f)
                isTtsReady = true
            }
        }
    }

    suspend fun speak(text: String, voiceName: String = "Kore") = withContext(Dispatchers.Main) {
        if (_isPlaying.value) {
            stop()
            return@withContext
        }

        stop()
        _isGenerating.value = true
        _statusText.value = "Génération de la voix IA humaine…"

        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val audioBytes = fetchGeminiTtsAudio(apiKey, text, voiceName)
                if (audioBytes != null && audioBytes.isNotEmpty()) {
                    playAudioBytes(audioBytes)
                    _isGenerating.value = false
                    _statusText.value = "Lecture Voix Céleste IA"
                    return@withContext
                }
            } catch (e: Exception) {
                // Network or quota issue: fallback to enhanced French TTS smoothly
            }
        }

        // Fallback to high quality Android French TTS
        _isGenerating.value = false
        _statusText.value = "Lecture Voix Spirituelle"
        playFallbackTts(text)
    }

    private suspend fun fetchGeminiTtsAudio(apiKey: String, text: String, voiceName: String): ByteArray? = withContext(Dispatchers.IO) {
        // Clean and prepare the spiritual text
        val cleanedText = text.take(600).replace("\n", " ").trim()
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()

        val jsonBody = JSONObject().apply {
            val contents = JSONArray().apply {
                val part = JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "Lis ce message spirituel avec une voix humaine très claire, chaleureuse, posée et solennelle : $cleanedText")
                        })
                    })
                }
                put(part)
            }
            put("contents", contents)

            val generationConfig = JSONObject().apply {
                put("responseModalities", JSONArray().apply {
                    put("AUDIO")
                })
                put("speechConfig", JSONObject().apply {
                    put("voiceConfig", JSONObject().apply {
                        put("prebuiltVoiceConfig", JSONObject().apply {
                            put("voiceName", voiceName)
                        })
                    })
                })
            }
            put("generationConfig", generationConfig)
        }

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-tts:generateContent?key=$apiKey")
            .post(jsonBody.toString().toRequestBody(jsonMediaType))
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return@withContext null

        val responseStr = response.body?.string() ?: return@withContext null
        val root = JSONObject(responseStr)
        val candidates = root.optJSONArray("candidates") ?: return@withContext null
        val content = candidates.optJSONObject(0)?.optJSONObject("content") ?: return@withContext null
        val parts = content.optJSONArray("parts") ?: return@withContext null

        for (i in 0 until parts.length()) {
            val part = parts.getJSONObject(i)
            val inlineData = part.optJSONObject("inlineData")
            if (inlineData != null) {
                val base64Data = inlineData.optString("data")
                if (!base64Data.isNullOrBlank()) {
                    return@withContext Base64.decode(base64Data, Base64.DEFAULT)
                }
            }
        }
        null
    }

    private fun playAudioBytes(bytes: ByteArray) {
        try {
            val tempAudioFile = File(context.cacheDir, "gemini_voice_${System.currentTimeMillis()}.wav")
            FileOutputStream(tempAudioFile).use { it.write(bytes) }

            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(tempAudioFile.absolutePath)
                prepare()
                setOnCompletionListener {
                    _isPlaying.value = false
                    _statusText.value = null
                    tempAudioFile.delete()
                }
                start()
            }
            _isPlaying.value = true
        } catch (e: Exception) {
            _isPlaying.value = false
            _statusText.value = null
        }
    }

    private fun playFallbackTts(text: String) {
        if (!isTtsReady) return
        fallbackTts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AiVoiceFallback")
        _isPlaying.value = true
        fallbackTts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isPlaying.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isPlaying.value = false
                _statusText.value = null
            }

            override fun onError(utteranceId: String?) {
                _isPlaying.value = false
                _statusText.value = null
            }
        })
    }

    fun stop() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.stop()
            }
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (_: Exception) {}

        try {
            fallbackTts?.stop()
        } catch (_: Exception) {}

        _isPlaying.value = false
        _isGenerating.value = false
        _statusText.value = null
    }

    fun release() {
        stop()
        fallbackTts?.shutdown()
        fallbackTts = null
    }
}
