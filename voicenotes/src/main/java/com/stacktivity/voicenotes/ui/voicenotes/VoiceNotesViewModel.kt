package com.stacktivity.voicenotes.ui.voicenotes

import android.media.MediaRecorder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stacktivity.voicenotes.model.VoiceNoteItem
import com.stacktivity.voicenotes.repo.VoiceNotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class VoiceNotesViewModel(private val repository: VoiceNotesRepository) : ViewModel() {

    private var mediaRecorder: MediaRecorder = MediaRecorder()

    private val _voiceNotesFlow = MutableStateFlow<List<VoiceNoteItem>>(listOf())
    val voiceNotesFlow: StateFlow<List<VoiceNoteItem>> get() = _voiceNotesFlow.asStateFlow()

    private val _audioRecording = MutableStateFlow(false)
    val audioRecording: StateFlow<Boolean> get() = _audioRecording.asStateFlow()

    private var recordingFile: File? = null
    private val voiceNoteFormat = "3gpp"


    fun fetchItems() {
        viewModelScope.launch {
            _voiceNotesFlow.tryEmit(repository.fetchVoiceNotes())
        }
    }

    fun recordAudio(): String {
        val audioFile = repository.getNewFile(voiceNoteFormat).also { recordingFile = it }
        val audioPath = audioFile.path
        viewModelScope.launch {
            initRecorder().apply {
                setOutputFile(audioPath)
                prepare()
                _audioRecording.value = true
                start()
            }
        }

        return audioFile.nameWithoutExtension
    }

    fun stopRecord() {
        mediaRecorder.apply {
            stop()
            _audioRecording.value = false
//            release()  // TODO
        }
    }

    fun applyRecordedAudioName(from: String, to: String) {
        if (from != to) {
            repository.renameFile(from, to, voiceNoteFormat)
        }
        viewModelScope.launch {
            val saveResult = repository.saveToRemote(to, voiceNoteFormat)
        }
    }


    private fun initRecorder(): MediaRecorder {
        return mediaRecorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        }
    }
}