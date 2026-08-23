package com.example.truelineapp.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Base64
import android.util.Log
import java.io.File
import java.io.FileOutputStream

class AndroidAudioPlayer(private val context: Context) : AudioPlayer {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingUrl: String? = null

    override fun play(urlOrDataUri: String, onComplete: () -> Unit) {
        stop()

        if (urlOrDataUri.isBlank()) {
            onComplete()
            return
        }

        try {
            currentPlayingUrl = urlOrDataUri
            val audioSource: String = if (urlOrDataUri.startsWith("data:audio") || urlOrDataUri.startsWith("data:")) {
                val base64Content = urlOrDataUri.substringAfter(",")
                val decodedBytes = Base64.decode(base64Content, Base64.DEFAULT)
                val tempFile = File(context.cacheDir, "temp_voice_intro_${System.currentTimeMillis()}.m4a")
                FileOutputStream(tempFile).use { it.write(decodedBytes) }
                tempFile.absolutePath
            } else {
                urlOrDataUri
            }

            val player = MediaPlayer()
            mediaPlayer = player

            player.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            player.setDataSource(audioSource)
            player.setOnPreparedListener { mp ->
                try {
                    mp.start()
                    Log.d("AudioPlayer", "Audio playback started for $audioSource")
                } catch (e: Exception) {
                    Log.e("AudioPlayer", "Failed to start player onPrepared: ${e.message}")
                    stop()
                    onComplete()
                }
            }
            player.setOnCompletionListener {
                Log.d("AudioPlayer", "Audio playback completed")
                stop()
                onComplete()
            }
            player.setOnErrorListener { _, what, extra ->
                Log.e("AudioPlayer", "MediaPlayer error: what=$what extra=$extra")
                stop()
                onComplete()
                true
            }
            player.prepareAsync()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Failed to setup audio playback: ${e.message}", e)
            stop()
            onComplete()
        }
    }

    override fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error releasing MediaPlayer: ${e.message}")
        } finally {
            mediaPlayer = null
            currentPlayingUrl = null
        }
    }

    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
}

private var globalAudioPlayer: AudioPlayer? = null

fun initAudioPlayer(context: Context) {
    globalAudioPlayer = AndroidAudioPlayer(context.applicationContext)
}

actual fun getAudioPlayer(): AudioPlayer {
    return globalAudioPlayer ?: object : AudioPlayer {
        override fun play(urlOrDataUri: String, onComplete: () -> Unit) { onComplete() }
        override fun stop() {}
        override fun isPlaying(): Boolean = false
    }
}
