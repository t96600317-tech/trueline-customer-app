@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.example.truelineapp.audio

import platform.AVFoundation.AVPlayer
import platform.Foundation.NSURL

private object IOSAudioPlayer : AudioPlayer {
    private var player: AVPlayer? = null

    override fun play(urlOrDataUri: String, onComplete: () -> Unit) {
        stop()
        if (urlOrDataUri.isBlank() || urlOrDataUri.startsWith("data:")) {
            onComplete()
            return
        }
        val url = NSURL.URLWithString(urlOrDataUri)
        if (url == null) {
            onComplete()
            return
        }
        player = AVPlayer.playerWithURL(url).also { it.play() }
    }

    override fun stop() {
        player?.pause()
        player = null
    }

    override fun isPlaying(): Boolean = (player?.rate ?: 0f) > 0f
}

actual fun getAudioPlayer(): AudioPlayer = IOSAudioPlayer
