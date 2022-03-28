package com.stacktivity.voicenotes.adapter

import android.os.CountDownTimer
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.stacktivity.voicenotes.databinding.VoiceNoteItemBinding
import com.stacktivity.voicenotes.model.VoiceNoteItem
import com.stacktivity.voicenotes.utils.PlayerProgressProvider

class VoiceNoteViewHolder(
    private val binding: VoiceNoteItemBinding,
) : ViewHolder(binding.root) {

    var item: VoiceNoteItem? = null
        private set
    val playButton get() = binding.btnPlay

    private var handleProgressTimer: CountDownTimer? = null


    fun onBind(item: VoiceNoteItem) {
        this.item = item

        binding.apply {
            title.text = item.title
            createTime.text = item.createTimeString
            totalTime.text = item.durationString
            btnPlay.isChecked = item.isPlaying
        }
    }

    fun handleMediaProgress(provider: PlayerProgressProvider) {
        val countDownInterval = item!!.durationMs / 25
        val allMs = item!!.durationMs
        val millisInFuture = allMs - provider.getCurrentTimeMs()

        handleProgressTimer = object : CountDownTimer(millisInFuture, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val currentTimeMs = provider.getCurrentTimeMs()
                binding.playerProgressBar.progress = provider.getCurrentProgress()
                binding.currentTime.text = VoiceNoteItem.timeSecondsToString(currentTimeMs / 1000)
            }

            override fun onFinish() { }

        }.start()

        binding.apply {
            currentTime.visibility = View.VISIBLE
            timeDelimiter.visibility = View.VISIBLE
            playerProgressBar.visibility = View.VISIBLE
        }
    }

    fun pauseHandleMediaProgress() {
        handleProgressTimer?.cancel()
        handleProgressTimer = null
    }

    fun stopHandleMediaProgress() {
        pauseHandleMediaProgress()

        binding.apply {
            currentTime.visibility = View.INVISIBLE
            timeDelimiter.visibility = View.INVISIBLE
            playerProgressBar.visibility = View.INVISIBLE
            btnPlay.isChecked = false
        }
    }
}
