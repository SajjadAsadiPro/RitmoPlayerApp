package ir.sajjadasadi.RitmoPlayer

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer

@Composable
fun MusicPlayerScreen(contentResolver: ContentResolver) {
    val initialMusicList = loadMusic(contentResolver).sortedBy { it.date }.toMutableList()
    var musicList by remember { mutableStateOf(initialMusicList) }
    val context = LocalContext.current
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    var currentMusic by remember { mutableStateOf<MusicItem?>(null) }
    var isLooping by remember { mutableStateOf(false) }
    var isShuffling by remember { mutableStateOf(false) }
    var isReversed by remember { mutableStateOf(false) }
    var shuffledMusicList by remember { mutableStateOf(musicList.toMutableList()) }
    var searchText by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var musicToDelete by remember { mutableStateOf<MusicItem?>(null) }
    var sortOption by remember { mutableStateOf(SortOption.BY_DATE) }

    val listState = rememberLazyListState()

    LaunchedEffect(currentMusic) {
        currentMusic?.let { music ->
            val index = shuffledMusicList.indexOf(music)
            if (index != -1) {
                listState.animateScrollToItem(index)
            }
        }
    }

    LaunchedEffect(isLooping) {
        exoPlayer.repeatMode =
            if (isLooping) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
    }

    LaunchedEffect(isShuffling) {
        shuffledMusicList =
            if (isShuffling) musicList.shuffled().toMutableList() else musicList.toMutableList()
    }

    LaunchedEffect(isReversed) {
        shuffledMusicList =
            if (isReversed) musicList.reversed().toMutableList() else musicList.toMutableList()
    }

    LaunchedEffect(sortOption) {
        shuffledMusicList = when (sortOption) {
            SortOption.BY_NAME -> musicList.sortedBy { it.title }.toMutableList()
            SortOption.BY_DATE -> musicList.sortedBy { it.date }.toMutableList()
            else -> musicList.toMutableList()
        }
    }

    DisposableEffect(Unit) {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    val filteredMusicList = getFilteredMusicList(shuffledMusicList, searchText)
                    val currentIndex = filteredMusicList.indexOf(currentMusic)
                    if (currentIndex != -1 && currentIndex < filteredMusicList.size - 1) {
                        val nextMusic = filteredMusicList[currentIndex + 1]
                        currentMusic = nextMusic
                        val mediaItem = MediaItem.fromUri(nextMusic.uri)
                        exoPlayer.setMediaItem(mediaItem)
                        exoPlayer.prepare()
                        exoPlayer.play()
                    }
                }
            }
        })
        onDispose { exoPlayer.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        currentMusic?.let {
            Crossfade(
                targetState = it.albumArtUri,
                animationSpec = tween(durationMillis = 1000)
            ) { albumArtUri ->
                AsyncImage(
                    model = albumArtUri ?: Uri.EMPTY,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.3f
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            TopBar(
                isShuffling = isShuffling,
                isReversed = isReversed,
                isLooping = isLooping,
                isSearchVisible = isSearchVisible,
                onShuffleToggle = {
                    isShuffling = !isShuffling
                    Toast.makeText(
                        context,
                        if (isShuffling) "Shuffle Enabled" else "Shuffle Disabled",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onReverseToggle = {
                    isReversed = !isReversed
                    Toast.makeText(
                        context,
                        if (isReversed) "Reverse Enabled" else "Reverse Disabled",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onLoopToggle = {
                    isLooping = !isLooping
                    Toast.makeText(
                        context,
                        if (isLooping) "Loop Enabled" else "Loop Disabled",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onSearchToggle = {
                    isSearchVisible = !isSearchVisible
                    Toast.makeText(
                        context,
                        if (isSearchVisible) "Search Enabled" else "Search Disabled",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onSortOptionSelected = { option ->
                    sortOption = option
                    Toast.makeText(context, "Sorted by ${option.name}", Toast.LENGTH_SHORT).show()
                }
            )

            SearchBar(
                isSearchVisible = isSearchVisible,
                searchText = searchText,
                onSearchTextChange = { searchText = it })

            val filteredMusicList = getFilteredMusicList(shuffledMusicList, searchText)

            LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
                items(filteredMusicList) { music ->
                    MusicItemView(
                        music = music,
                        isPlaying = currentMusic == music,
                        onClick = {
                            currentMusic = music
                            val mediaItem = MediaItem.fromUri(music.uri)
                            exoPlayer.setMediaItem(mediaItem)
                            exoPlayer.prepare()
                            exoPlayer.play()
                            isSearchVisible = false
                        },
                        onDelete = {
                            musicToDelete = music
                            showDeleteDialog = true
                        }
                    )
                }
            }

            currentMusic?.let {
                MusicControlBar(
                    currentMusic = it,
                    exoPlayer = exoPlayer,
                    musicList = shuffledMusicList,
                    isLooping = isLooping,
                    isShuffling = isShuffling,
                    onLoopToggle = { isLooping = !isLooping },
                    onShuffleToggle = { isShuffling = !isShuffling },
                    onMusicChanged = { newMusic -> currentMusic = newMusic }
                )
            }

            fun getContentUriFromFilePath(context: Context, filePath: String): Uri? {
                val contentResolver = context.contentResolver
                val projection = arrayOf(MediaStore.Audio.Media._ID)
                val selection = MediaStore.Audio.Media.DATA + "=?"
                val selectionArgs = arrayOf(filePath)

                val cursor = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
                )

                return cursor?.use {
                    if (it.moveToFirst()) {
                        val id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                        ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    } else {
                        null
                    }
                }
            }

            if (showDeleteDialog && musicToDelete != null) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        val music = musicToDelete
                        if (music != null) {
                            try {
                                val filePath = music.uri.path
                                if (filePath != null) {
                                    val contentUri = getContentUriFromFilePath(context, filePath)
                                    if (contentUri != null) {
                                        val deletedRows =
                                            contentResolver.delete(contentUri, null, null)
                                        if (deletedRows > 0) {
                                            Toast.makeText(
                                                context,
                                                "موزیک حذف شد",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val filteredMusicList =
                                                getFilteredMusicList(shuffledMusicList, searchText)

                                            val currentIndex = filteredMusicList.indexOf(music)
                                            musicList.remove(music)
                                            shuffledMusicList =
                                                if (isShuffling) musicList.shuffled()
                                                    .toMutableList() else musicList.toMutableList()
                                            shuffledMusicList = when (sortOption) {
                                                SortOption.BY_NAME -> shuffledMusicList.sortedBy { it.title }
                                                    .toMutableList()

                                                SortOption.BY_DATE -> shuffledMusicList.sortedBy { it.date }
                                                    .toMutableList()

                                                else -> shuffledMusicList
                                            }

                                            // اگر آهنگ حذف‌شده در حال پخش بود، آهنگ بعدی را پخش کن
                                            if (currentMusic == music) {
                                                val nextMusicIndex = currentIndex + 1
                                                if (nextMusicIndex < filteredMusicList.size) {
                                                    val nextMusic =
                                                        filteredMusicList[nextMusicIndex]
                                                    currentMusic = nextMusic
                                                    val mediaItem = MediaItem.fromUri(nextMusic.uri)
                                                    exoPlayer.setMediaItem(mediaItem)
                                                    exoPlayer.prepare()
                                                    exoPlayer.play()
                                                } else {
                                                    currentMusic = null
                                                    exoPlayer.stop()
                                                }
                                            }
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "خطا در حذف موزیک: دسترسی نامعتبر",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "خطا: فایل نامعتبر است",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "خطا: مسیر فایل نامعتبر است",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: SecurityException) {
                                Toast.makeText(
                                    context,
                                    "خطا: دسترسی لازم برای حذف وجود ندارد",
                                    Toast.LENGTH_LONG
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "خطا در حذف موزیک: ${e.localizedMessage}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }
        }
    }
}

fun getFilteredMusicList(shuffledMusicList: List<MusicItem>, searchText: String): List<MusicItem> {
    return if (searchText.isEmpty()) {
        shuffledMusicList
    } else {
        shuffledMusicList.filter {
            it.title.contains(searchText, ignoreCase = true) ||
                    it.artist.contains(searchText, ignoreCase = true)
        }
    }
}

enum class SortOption {
    NONE,
    BY_NAME,
    BY_DATE
}
