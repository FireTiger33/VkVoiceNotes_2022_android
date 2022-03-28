package com.stacktivity.voicenotes.model

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import org.ocpsoft.prettytime.PrettyTime
import java.io.File
import java.util.Locale
import java.util.Date


data class VoiceNoteItem(
    val title: String,
    val createTime: Long,
    val durationMs: Long,
    val path: String
) {
    val durationString: String = timeSecondsToString((durationMs / 1000).toInt())
    val createTimeString: String = PrettyTime(Locale.getDefault()).format(Date(createTime))
    var isPlaying = false


    override fun equals(other: Any?): Boolean {
        return if (other is VoiceNoteItem) {
            return createTime == other.createTime
        } else false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    companion object {
        fun mapFromFile(file: File): VoiceNoteItem {
            val dataRetriever = MediaMetadataRetriever().apply {
                setDataSource(file.path)
            }
            val durationMs = dataRetriever.extractMetadata(METADATA_KEY_DURATION)!!.toLong()

            dataRetriever.release()

            return VoiceNoteItem(
                title = file.name,
                createTime = file.lastModified(),
                durationMs = durationMs,
                path = file.path
            )
        }

        fun timeSecondsToString(seconds: Int): String {
            val m = seconds / 60
            val s = seconds % 60
            return "$m:${if (s < 10) "0" else ""}$s"
        }
    }
}