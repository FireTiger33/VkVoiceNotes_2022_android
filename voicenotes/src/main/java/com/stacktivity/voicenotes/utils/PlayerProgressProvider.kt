package com.stacktivity.voicenotes.utils

interface PlayerProgressProvider {
    fun getCurrentTimeMs(): Int
    fun getCurrentProgress(): Int
}