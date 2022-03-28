package com.stacktivity.voicenotes.ui.voicenotes

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.stacktivity.voicenotes.R.string
import com.stacktivity.voicenotes.R.layout.voice_notes_screen
import com.stacktivity.voicenotes.R.dimen.VoiceNoteItem_horizontal_space
import com.stacktivity.voicenotes.R.dimen.VoiceNoteItem_vertical_space
import com.stacktivity.voicenotes.adapter.SpacesVoiceNoteItemDecoration
import com.stacktivity.voicenotes.adapter.VoiceNoteListAdapter
import com.stacktivity.voicenotes.databinding.VoiceNotesScreenBinding
import com.stacktivity.voicenotes.utils.launchWhenStarted
import kotlinx.coroutines.flow.onEach

class VoiceNotesFragment : Fragment(voice_notes_screen) {

    private val mTag: String = VoiceNotesFragment::class.java.simpleName

    private val binding by viewBinding(VoiceNotesScreenBinding::bind)

    private val viewModel by lazy {
        ViewModelProvider(this, VoiceNotesViewModelFactory(requireContext().cacheDir))
            .get(VoiceNotesViewModel::class.java)
    }

    private val adapter by lazy { VoiceNoteListAdapter() }

    private var recordingFileName: String? = null
    private val recorder = registerForActivityResult(RequestPermission()) { granted ->
        when {
            granted -> {
                recordingFileName = viewModel.recordAudio()
            }
            shouldShowRequestPermissionRationale(RECORD_AUDIO).not() -> {
                // пользователь поставил галочку Don't ask again
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = getString(string.name)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initUI()
        setupObservers()
        viewModel.fetchItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (viewModel.audioRecording.value) {
            viewModel.stopRecord()
        }
    }

    private fun initUI() {
        initNotesView()
    }

    private fun initNotesView() {
        val horizontalSpace = resources.getDimension(VoiceNoteItem_horizontal_space).toInt()
        val verticalSpace = resources.getDimension(VoiceNoteItem_vertical_space).toInt()

        binding.voiceNoteListRv.apply {
            adapter = this@VoiceNotesFragment.adapter

            addItemDecoration(
                SpacesVoiceNoteItemDecoration(
                    horizontalSpace = horizontalSpace,
                    verticalSpace = verticalSpace
                )
            )
        }
    }

    private fun setupObservers() {
        setupButtonsListeners()

        viewModel.audioRecording.onEach { audioRecording ->
            Log.d(mTag, "audio recording: $audioRecording")
            binding.btnAddVoiceNote.isChecked = audioRecording
        }.launchWhenStarted(lifecycleScope)

        viewModel.voiceNotesFlow.onEach { voiceNotes ->
            Log.d(mTag, "voice notes: ${voiceNotes.joinToString { it.path }}")
            adapter.submitList(voiceNotes)
        }.launchWhenStarted(lifecycleScope)
    }

    private fun setupButtonsListeners() {
        binding.addVoiceNoteOverlay.apply {
            setOnClickListener {
                it.isEnabled = false
                if (viewModel.audioRecording.value) {
                    viewModel.stopRecord()
                    // TODO show rename file dialog or delete
                    viewModel.fetchItems()
                } else {
                    if (shouldShowRequestPermissionRationale(RECORD_AUDIO)) {
                        openSettingsScreen()
                    } else {
                        recorder.launch(RECORD_AUDIO)
                    }
                }
                it.isEnabled = true
            }
        }
    }

    private fun openSettingsScreen() {
        startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.parse("package:com.stacktivity.vkvoicenotes")  // TODO get package
        })
    }
}