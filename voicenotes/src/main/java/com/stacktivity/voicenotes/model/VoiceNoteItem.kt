package com.stacktivity.voicenotes.model

import android.media.MediaMetadataRetriever
import android.media.MediaMetadataRetriever.METADATA_KEY_DURATION
import androidx.recyclerview.widget.DiffUtil
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

    constructor(mediaFile: File) : this(
        title = mediaFile.nameWithoutExtension,
        createTime = mediaFile.lastModified(),
        durationMs = getDurationData(mediaFile),
        path = mediaFile.absolutePath
    )

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

            val durationMs = try {
                val dataRetriever = MediaMetadataRetriever().apply {
                    setDataSource(file.path)
                }
                dataRetriever.extractMetadata(METADATA_KEY_DURATION)!!.toLong()
                    .also { dataRetriever.release() }
            } catch (e: java.lang.RuntimeException) { 0 }

            return VoiceNoteItem(
                title = file.nameWithoutExtension,
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

        /**
         * Used to get duration data from a media file.
         * @return duration of [mediaFile] in ms
         */
        private fun getDurationData(mediaFile: File): Long {
            return try {
                val dataRetriever = MediaMetadataRetriever().apply {
                    setDataSource(mediaFile.inputStream().fd)
                }
                dataRetriever.extractMetadata(METADATA_KEY_DURATION)!!.toLong()
                    .also { dataRetriever.release() }
            } catch (e: RuntimeException) {
                0
            }
        }
    }
}