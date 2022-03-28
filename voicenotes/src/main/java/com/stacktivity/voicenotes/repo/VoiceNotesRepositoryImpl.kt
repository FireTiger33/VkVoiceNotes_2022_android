package com.stacktivity.voicenotes.repo

import com.stacktivity.core.utils.CacheWorker
import com.stacktivity.voicenotes.model.VoiceNoteItem
import java.io.File

private const val voiceNotesDir = "notes"

internal class VoiceNotesRepositoryImpl(cacheDir: File): VoiceNotesRepository {

    private val cacheWorker = CacheWorker(cacheDir)


    override fun getNewVoiceNoteFile(): File {
        return cacheWorker.getNewFile(voiceNotesDir)
    }

    override fun renameFile(from: String, to: String) {
        cacheWorker.getFile(from, voiceNotesDir)
            .renameTo(cacheWorker.getFile(to, voiceNotesDir))
    }

    override fun fetchVoiceNotes(): List<VoiceNoteItem> {
        return cacheWorker.getListFiles(voiceNotesDir)
            .map { VoiceNoteItem.mapFromFile(it) }
            .sortedByDescending { it.createTime }
    }

}