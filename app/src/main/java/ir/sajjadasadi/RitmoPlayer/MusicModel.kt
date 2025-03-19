package ir.sajjadasadi.RitmoPlayer

import android.net.Uri
import java.util.Date

data class MusicItem(
    val id: Long,
    val title: String,
    val artist: String,
    val uri: Uri,
    val albumArtUri: Uri?,
    val date: Date
)
