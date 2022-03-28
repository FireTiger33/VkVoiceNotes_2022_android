package com.stacktivity.voicenotes.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.stacktivity.core.utils.FragmentManagers.replaceFragment
import com.stacktivity.voicenotes.R.id.container
import com.stacktivity.voicenotes.R.layout.login_screen
import com.stacktivity.voicenotes.databinding.LoginScreenBinding
import com.stacktivity.voicenotes.ui.voicenotes.VoiceNotesFragment
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.vk.api.sdk.auth.VKScope


class LoginFragment: Fragment(login_screen) {

    private val binding by viewBinding(LoginScreenBinding::bind)

    private val authenticator = registerForActivityResult(
        VK.getVKAuthActivityResultContract()
    ) { authResult ->
        if (authResult is VKAuthenticationResult.Success) {
            showVoiceNotesScreen(testMode = false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            authenticator.launch(arrayListOf(VKScope.DOCS))
        }

        binding.buttonTestMode.setOnClickListener {
            showVoiceNotesScreen(testMode = true)
        }
    }

    private fun showVoiceNotesScreen(testMode: Boolean) {
        val fragment = VoiceNotesFragment()
        fragment.arguments = Bundle(1).apply {
            putBoolean(VoiceNotesFragment.KEY_TEST_MODE, testMode)
        }
        if (testMode) {
            replaceFragment(requireActivity().supportFragmentManager, fragment, container)
        } else {
            requireActivity().supportFragmentManager.popBackStack()
            replaceFragment(requireActivity().supportFragmentManager, fragment, container)
        }

    }
}