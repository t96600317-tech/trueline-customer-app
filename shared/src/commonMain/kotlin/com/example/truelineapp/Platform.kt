package com.example.truelineapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform