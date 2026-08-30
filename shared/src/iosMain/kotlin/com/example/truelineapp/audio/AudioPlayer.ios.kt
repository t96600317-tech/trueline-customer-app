package com.example.truelineapp.audio

private object IOSAudioPlayer : AudioPlayer {
    private var playing = false

    override fun play(urlOrDataUri: String, onComplete: () -> Unit) {
        stop()
        // Voice call media is handled by the native Zego engine. The separate
        // customer audio player will be replaced with a Swift AVFoundation
        // bridge when the customer media UI is ported to iOS.
        onComplete()
    }

    override fun stop() {
        playing = false
    }

    override fun isPlaying(): Boolean = playing
}

actual fun getAudioPlayer(): AudioPlayer = IOSAudioPlayer
