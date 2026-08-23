package com.example.truelineapp.audio

interface AudioPlayer {
    fun play(urlOrDataUri: String, onComplete: () -> Unit = {})
    fun stop()
    fun isPlaying(): Boolean
}

expect fun getAudioPlayer(): AudioPlayer
