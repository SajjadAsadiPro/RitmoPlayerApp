    package ir.sajjadasadi.RitmoPlayer

    import android.content.ContentResolver
    import android.content.ContentUris
    import android.database.Cursor
    import android.net.Uri
    import android.provider.MediaStore
    import java.util.Date

    fun loadMusic(contentResolver: ContentResolver): List<MusicItem> {
        val musicList = mutableListOf<MusicItem>()

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
        val cursor: Cursor? = contentResolver.query(uri, projection, selection, null, sortOrder)

        cursor.use { cur ->
            if (cur != null && cur.moveToFirst()) {
                val idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID)
                val titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST)
                val dataColumn = cur.getColumnIndex(MediaStore.Audio.Media.DATA)
                val albumIdColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
                val dateColumn = cur.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

                do {
                    val id = cur.getLong(idColumn)
                    val title = cur.getString(titleColumn)
                    val artist = cur.getString(artistColumn)
                    val data = cur.getString(dataColumn)
                    val albumId = cur.getLong(albumIdColumn)
                    val dateAdded = cur.getLong(dateColumn) * 1000
                    val date = Date(dateAdded)

                    val songUri = Uri.parse(data)
                    val albumArtUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId)

                    musicList.add(MusicItem(id, title, artist, songUri, albumArtUri, date))
                } while (cur.moveToNext())
            }
        }

        return musicList
    }
