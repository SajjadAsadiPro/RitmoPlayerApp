package ir.sajjadasadi.RitmoPlayer

import android.content.ContentResolver
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
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MusicPlayerScreen(contentResolver: ContentResolver) {
    val musicList by remember { mutableStateOf(loadMusic(contentResolver)) }
    val context = LocalContext.current
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    var currentMusic by remember { mutableStateOf<MusicItem?>(null) }
    var isLooping by remember { mutableStateOf(false) }
    var isShuffling by remember { mutableStateOf(false) }
    var isReversed by remember { mutableStateOf(false) }
    var shuffledMusicList by remember { mutableStateOf(musicList) }
    var searchText by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }

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
        exoPlayer.repeatMode = if (isLooping) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
    }

    LaunchedEffect(isShuffling) {
        shuffledMusicList = if (isShuffling) musicList.shuffled() else musicList
    }

    LaunchedEffect(isReversed) {
        shuffledMusicList = if (isReversed) musicList.reversed() else musicList
    }

    exoPlayer.addListener(object : com.google.android.exoplayer2.Player.Listener {
        override fun onPlaybackStateChanged(state: Int) {
            if (state == com.google.android.exoplayer2.Player.STATE_ENDED) {
                if (!isLooping) {
                    val currentList = if (isShuffling) shuffledMusicList else musicList
                    val currentIndex = currentList.indexOf(currentMusic)
                    val nextIndex = (currentIndex + 1) % currentList.size
                    val nextMusic = currentList[nextIndex]

                    val mediaItem = MediaItem.fromUri(nextMusic.uri)
                    exoPlayer.setMediaItem(mediaItem)
                    exoPlayer.prepare()
                    exoPlayer.play()

                    CoroutineScope(Dispatchers.Main).launch {
                        while (exoPlayer.isLoading) {
                            delay(100)
                        }
                        currentMusic = nextMusic
                    }
                }
            }
        }
    })

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        currentMusic?.let {
            Crossfade(targetState = it.albumArtUri, animationSpec = tween(durationMillis = 1000)) { albumArtUri ->
                AsyncImage(
                    model = albumArtUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.3f // شفاف کردن پس زمینه
                )
            }
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            TopBar(
                isShuffling = isShuffling,
                isReversed = isReversed,
                isLooping = isLooping,
                isSearchVisible = isSearchVisible,
                onShuffleToggle = { isShuffling = !isShuffling },
                onReverseToggle = { isReversed = !isReversed },
                onLoopToggle = { isLooping = !isLooping },
                onSearchToggle = { isSearchVisible = !isSearchVisible }
            )

            SearchBar(isSearchVisible = isSearchVisible, searchText = searchText, onSearchTextChange = { searchText = it })

            val filteredMusicList = if (searchText.isEmpty()) {
                shuffledMusicList
            } else {
                shuffledMusicList.filter { it.title.contains(searchText, ignoreCase = true) || it.artist.contains(searchText, ignoreCase = true) }
            }

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
                            isSearchVisible = false // مخفی کردن TextField جستجو بعد از کلیک روی یک آیتم
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
        }
    }
}