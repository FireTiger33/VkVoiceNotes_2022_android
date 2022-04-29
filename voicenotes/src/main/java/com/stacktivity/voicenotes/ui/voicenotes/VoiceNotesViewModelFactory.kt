package com.stacktivity.voicenotes.ui.voicenotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.stacktivity.voicenotes.repo.VoiceNotesRepository
import java.io.File

class VoiceNotesViewModelFactory(private val cacheDir: File): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val repository = VoiceNotesRepository.getInstance(appContext.cacheDir)
        @Suppress("UNCHECKED_CAST")
        return VoiceNotesViewModel(repository, provideMusicServiceConnection(appContext)) as T
    }
}