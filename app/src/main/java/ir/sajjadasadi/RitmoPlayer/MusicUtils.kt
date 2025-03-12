package ir.sajjadasadi.RitmoPlayer

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore

fun loadMusic(contentResolver: ContentResolver): List<MusicItem> {
    val musicList = mutableListOf<MusicItem>()
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val projection = arrayOf(
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID
    )

    val cursor = contentResolver.query(uri, projection, null, null, null)
    cursor?.use {
        val titleIndex = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
        val artistIndex = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
        val dataIndex = it.getColumnIndex(MediaStore.Audio.Media.DATA)
        val albumIdIndex = it.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

        while (it.moveToNext()) {
            val title = it.getString(titleIndex) ?: "Unknown Title"
            val artist = it.getString(artistIndex) ?: "Unknown Artist"
            val data = it.getString(dataIndex)
            val albumId = it.getLong(albumIdIndex)

            val albumArtUri = if (albumId != 0L) {
                Uri.parse("content://media/external/audio/albumart/$albumId")
            } else {
                null
            }

            musicList.add(MusicItem(title, artist, Uri.parse(data), albumArtUri))
        }
    }
    return musicList
}