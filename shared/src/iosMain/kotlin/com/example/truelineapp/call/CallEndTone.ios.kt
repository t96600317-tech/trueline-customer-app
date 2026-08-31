package com.example.truelineapp.call

import platform.AudioToolbox.AudioServicesPlaySystemSound

actual fun playCallEndedTone() {
    AudioServicesPlaySystemSound(1057u)
}
