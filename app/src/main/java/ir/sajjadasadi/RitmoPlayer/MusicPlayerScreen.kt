package ir.sajjadasadi.RitmoPlayer

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import coil.compose.AsyncImage
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

@Composable
fun MusicPlayerScreen(context: Context, contentResolver: ContentResolver) {
    val initialMusicList = loadMusic(contentResolver, context).sortedBy { it.date }.toMutableList()
    var musicList by remember { mutableStateOf(initialMusicList) }
    var displayedMusicList by remember { mutableStateOf(initialMusicList.toMutableList()) }

    val preferences = context.getSharedPreferences("ritmo_player_prefs", Context.MODE_PRIVATE)
    val exoPlayer = remember { SimpleExoPlayer.Builder(context).build() }
    var currentMusic by remember { mutableStateOf<MusicItem?>(null) }
    var isLooping by remember { mutableStateOf(false) }
    var isShuffling by remember { mutableStateOf(false) }
    var isReversed by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var isSearchVisible by remember { mutableStateOf(false) }
    var sortOption by remember { mutableStateOf(SortOption.BY_DATE) }
    var musicToDelete by remember { mutableStateOf<MusicItem?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showRatingDialog by remember { mutableStateOf(false) }
    var hasUserGivenFeedback by remember { mutableStateOf(preferences.getBoolean("hasUserGivenFeedback", false)) }

    BackHandler(enabled = !hasUserGivenFeedback) {
        showRatingDialog = true
    }

    BackHandler(enabled = hasUserGivenFeedback) {
        (context as? Activity)?.finish()
    }

    RatingDialog(
        showDialog = showRatingDialog,
        onDismiss = { showRatingDialog = false },
        onFeedbackGiven = {
            hasUserGivenFeedback = true
            preferences.edit { putBoolean("hasUserGivenFeedback", true) }
        }
    )

    val listState = rememberLazyListState()

    LaunchedEffect(sortOption, isReversed, isShuffling) {
        val sortedList = when (sortOption) {
            SortOption.BY_NAME -> musicList.sortedBy { it.title?.trim() ?: "" }
            SortOption.BY_DATE -> musicList.sortedBy { it.date }
        }

        val finalList = if (isReversed) sortedList.reversed() else sortedList
        displayedMusicList =
            if (isShuffling) finalList.shuffled().toMutableList() else finalList.toMutableList()
    }

    LaunchedEffect(currentMusic) {
        currentMusic?.let {
            val index = displayedMusicList.indexOf(it)
            if (index != -1) {
                listState.animateScrollToItem(index)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        currentMusic?.let {
            Crossfade(
                targetState = it.albumCover,
                animationSpec = tween(durationMillis = 1000)
            ) { albumCover ->
                AsyncImage(
                    model = albumCover ?: R.drawable.musicico,
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
                onShuffleToggle = { isShuffling = !isShuffling },
                onReverseToggle = { isReversed = !isReversed },
                onLoopToggle = { isLooping = !isLooping },
                onSearchToggle = { isSearchVisible = !isSearchVisible },
                onSortOptionSelected = { option -> sortOption = option }
            )

            SearchBar(
                isSearchVisible = isSearchVisible,
                searchText = searchText,
                onSearchTextChange = { searchText = it })

            val filteredMusicList = getFilteredMusicList(displayedMusicList, searchText)

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

            if (showDeleteDialog) {
                DeleteConfirmationDialog(
                    onConfirm = {
                        if (musicToDelete == currentMusic) {
                            val currentIndex = displayedMusicList.indexOf(musicToDelete)
                            val nextMusic = displayedMusicList.getOrNull(currentIndex + 1) // گرفتن موزیک بعدی
                            currentMusic = nextMusic // تنظیم موزیک بعدی
                            nextMusic?.let {
                                val mediaItem = MediaItem.fromUri(it.uri)
                                exoPlayer.setMediaItem(mediaItem)
                                exoPlayer.prepare()
                                exoPlayer.play()
                            } ?: exoPlayer.stop() // اگر موزیک بعدی نبود، پخش متوقف شود
                        }

                        musicList = musicList.filter { it != musicToDelete }.toMutableList()
                        displayedMusicList = displayedMusicList.filter { it != musicToDelete }.toMutableList()
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }

            currentMusic?.let {
                MusicControlBar(
                    currentMusic = it,
                    exoPlayer = exoPlayer,
                    musicList = displayedMusicList,
                    isLooping = isLooping,
                    isShuffling = isShuffling,
                    onLoopToggle = { isLooping = !isLooping },
                    onShuffleToggle = { isShuffling = !isShuffling },
                    onMusicChanged = { newMusic ->
                        currentMusic = newMusic
                    }
                )
            }
        }
    }
}

@Composable
fun RatingDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onFeedbackGiven: () -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("امتیاز شما به برنامه") },
            text = {
                Column {
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (i <= rating) Color.Yellow else Color.Gray
                                )
                            }
                        }
                    }

                    if (rating in 1..4) {
                        OutlinedTextField(
                            value = feedback,
                            onValueChange = { feedback = it },
                            label = { Text("نظر خود را بنویسید") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (rating in 1..4) {
                        sendWhatsApp(context, feedback)
                    } else if (rating == 5) {
                        openBazaarReview(context)
                    }
                    onFeedbackGiven()
                    onDismiss()
                }) {
                    Text(if (rating in 1..4) "ارسال نظر" else "رفتن به کافه بازار")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onFeedbackGiven()
                        onDismiss()
                    }
                ) {
                    Text("انصراف", color = Color.White)
                }
            }
        )
    }
}

fun sendWhatsApp(context: Context, feedback: String) {
    val phoneNumber = "+989931443876"
    val message = feedback
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "واتساپ نصب نیست!", Toast.LENGTH_SHORT).show()
    }
}

fun openBazaarReview(context: Context) {
    val intent = Intent(Intent.ACTION_EDIT).apply {
        data = Uri.parse("bazaar://details?id=ir.sajjadasadi.RitmoPlayer")
        setPackage("com.farsitel.bazaar")
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "کافه بازار نصب نیست!", Toast.LENGTH_SHORT).show()
    }
}

enum class SortOption {
    BY_NAME,
    BY_DATE
}

fun getFilteredMusicList(displayedMusicList: List<MusicItem>, searchText: String): List<MusicItem> {
    return if (searchText.isEmpty()) {
        displayedMusicList
    } else {
        displayedMusicList.filter {
            it.title.contains(searchText, ignoreCase = true) ||
                    it.artist.contains(searchText, ignoreCase = true)
        }
    }
}