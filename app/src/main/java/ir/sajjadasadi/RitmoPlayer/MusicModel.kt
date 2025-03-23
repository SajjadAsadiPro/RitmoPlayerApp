package ir.sajjadasadi.RitmoPlayer

import android.graphics.Bitmap
import java.util.Date

data class MusicItem(
    val id: Long,
    val title: String,
    val artist: String,
    val uri: String,
    val albumCover: Bitmap? = null,
    val date: Date
)