package com.stacktivity.voicenotes.adapter

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.stacktivity.voicenotes.databinding.VoiceNoteItemBinding
import com.stacktivity.voicenotes.model.VoiceNoteItem
import com.stacktivity.voicenotes.utils.PlayerProgressProvider


class VoiceNoteListAdapter :
    ListAdapter<VoiceNoteItem, VoiceNoteViewHolder>(NewsItemDiffCallback()),
    MediaPlayer.OnCompletionListener,
    PlayerProgressProvider
{
    private var playingViewHolder: VoiceNoteViewHolder? = null
    private var playingItem: VoiceNoteItem? = null
    private var mediaPlayer: MediaPlayer? = MediaPlayer()


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): VoiceNoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = VoiceNoteItemBinding.inflate(inflater)

        return VoiceNoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: VoiceNoteViewHolder, position: Int) {
        val voiceNoteItem = currentList[position]

        holder.playButton.setOnClickListener {
            it.isEnabled = false
            onPlayButtonClicked(holder)
            it.isEnabled = true
        }

        if (voiceNoteItem == playingItem) {
            playingViewHolder?.stopHandleMediaProgress()
            voiceNoteItem.isPlaying = true
            holder.onBind(voiceNoteItem)
            holder.handleMediaProgress(this)
        } else {
            holder.stopHandleMediaProgress()
            holder.onBind(voiceNoteItem)
        }
    }

    override fun getItemCount() = currentList.size

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // MediaPlayer.OnCompletionListener
    override fun onCompletion(mediaPlayer: MediaPlayer) {
        playingViewHolder?.stopHandleMediaProgress()
        playingItem = null
        playingViewHolder = null
    }

    // PlayerProgressProvider
    override fun getCurrentTimeMs(): Int {
        return (mediaPlayer?.currentPosition ?: 0)
    }

    override fun getCurrentProgress(): Int {
        return (getCurrentTimeMs().toDouble() / (playingItem?.durationMs ?: 1) * 100).toInt()
    }


    private fun initMediaPlayer(): MediaPlayer {
        return mediaPlayer?.apply { stop(); reset() }
            ?: MediaPlayer().also { mediaPlayer = it }
    }

    private fun onPlayButtonClicked(viewHolder: VoiceNoteViewHolder) {
        if (viewHolder.item == playingItem) {
            val isPlaying = toggleMediaPlayer()

            if (isPlaying) {
                viewHolder.handleMediaProgress(this)
            } else {
                viewHolder.pauseHandleMediaProgress()
            }
            viewHolder.item?.isPlaying = isPlaying
        } else {
            playingViewHolder?.stopHandleMediaProgress()
            playingItem?.let { playingItem ->
                playingItem.isPlaying = false
            }

            playingViewHolder = viewHolder
            playingItem = viewHolder.item?.also { playingItem ->
                playingItem.isPlaying = true
                initMediaPlayer().apply {
                    setDataSource(playingItem.path)
                    setOnCompletionListener(this@VoiceNoteListAdapter)
                    prepare()
                    start()
                }
            }

            viewHolder.handleMediaProgress(this)
        }
    }

    private fun toggleMediaPlayer(): Boolean {
        return mediaPlayer?.let { mediaPlayer ->
            val toggleState = mediaPlayer.isPlaying.not()
            if (toggleState) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
            toggleState
        } ?: false
    }

}
