package com.stacktivity.voicenotes.repo

import com.stacktivity.voicenotes.model.VoiceNoteItem
import java.io.File

interface VoiceNotesRepository {

    fun getNewVoiceNoteFile(): File

    fun renameFile(from: String, to: String)

    fun fetchVoiceNotes(): List<VoiceNoteItem>

    companion object {
        fun getInstance(cacheDir: File): VoiceNotesRepository {
            return VoiceNotesRepositoryImpl(cacheDir)
        }
    }
}