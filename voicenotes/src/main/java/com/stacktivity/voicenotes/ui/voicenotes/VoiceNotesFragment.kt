package com.stacktivity.voicenotes.ui.voicenotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.stacktivity.voicenotes.R
import com.stacktivity.voicenotes.R.layout.voice_notes_screen
import com.stacktivity.voicenotes.databinding.VoiceNotesScreenBinding

class VoiceNotesFragment : Fragment(voice_notes_screen) {

    private val binding by viewBinding(VoiceNotesScreenBinding::bind)

    private val viewModel by lazy {
        ViewModelProvider(this).get(VoiceNotesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = getString(R.string.name)

        return super.onCreateView(inflater, container, savedInstanceState)
    }
}