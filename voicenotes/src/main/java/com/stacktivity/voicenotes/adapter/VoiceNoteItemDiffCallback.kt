package com.stacktivity.voicenotes.adapter

import androidx.recyclerview.widget.DiffUtil
import com.stacktivity.voicenotes.model.VoiceNoteItem

class NewsItemDiffCallback : DiffUtil.ItemCallback<VoiceNoteItem>() {
    override fun areItemsTheSame(oldItem: VoiceNoteItem, newItem: VoiceNoteItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: VoiceNoteItem, newItem: VoiceNoteItem): Boolean {
        return oldItem == newItem
    }
}
