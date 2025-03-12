package ir.sajjadasadi.RitmoPlayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.coroutines.delay

@Composable
fun MusicControlBar(
    currentMusic: MusicItem,
    exoPlayer: ExoPlayer,
    musicList: List<MusicItem>,
    isLooping: Boolean,
    isShuffling: Boolean,
    onLoopToggle: () -> Unit,
    onShuffleToggle: () -> Unit,
    onMusicChanged: (MusicItem) -> Unit
) {
    var currentPosition by remember { mutableStateOf(0L) }
    var totalDuration by remember { mutableStateOf(0L) }
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var isControlVisible by remember { mutableStateOf(true) }

    var currentMusicState by remember { mutableStateOf(currentMusic) }

    LaunchedEffect(currentMusic) {
        currentMusicState = currentMusic
        isControlVisible = true // وقتی آهنگ جدید انتخاب میشه، کنترل باکس بالا میاد
    }

    val currentIndex = musicList.indexOf(currentMusicState)

    LaunchedEffect(currentMusicState, exoPlayer) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            totalDuration = exoPlayer.duration
            isPlaying = exoPlayer.isPlaying
            delay(100)
        }
    }

    exoPlayer.repeatMode = if (isLooping) ExoPlayer.REPEAT_MODE_ONE else ExoPlayer.REPEAT_MODE_OFF
    exoPlayer.shuffleModeEnabled = isShuffling

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(
            visible = !isControlVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                )
        ) {
            IconButton(
                onClick = { isControlVisible = !isControlVisible },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .size(48.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF8BC34A), Color(0xFFCDDC39)), // رنگ گرادینت
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        ), CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandLess,
                    contentDescription = "Show Controls",
                    tint = Color.White

                )
                
            }
        }

        AnimatedVisibility(
            visible = isControlVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier
                .animateContentSize(
                    animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
                )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth()
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { isControlVisible = !isControlVisible },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF6A1B9A),
                                        Color(0xFF8E24AA)
                                    ), // رنگ گرادینت
                                    start = Offset(0f, 0f),
                                    end = Offset(1000f, 1000f)
                                ), CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Hide Controls",
                            tint = Color.White
                        )
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.8f
                            )
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = currentMusicState.title,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Text(
                                        text = currentMusicState.artist,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.LightGray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = formatTime(currentPosition), color = Color.White)
                                Text(text = formatTime(totalDuration), color = Color.White)
                            }

                            Slider(
                                value = if (totalDuration > 0) currentPosition.toFloat() / totalDuration.toFloat() else 0f,
                                onValueChange = { exoPlayer.seekTo((it * totalDuration).toLong()) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = Color.White,
                                    activeTrackColor = Color.White,
                                    inactiveTrackColor = Color.Gray
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    if (currentIndex > 0) {
                                        currentMusicState = musicList[currentIndex - 1]
                                        val mediaItem = MediaItem.fromUri(currentMusicState.uri)
                                        exoPlayer.setMediaItem(mediaItem)
                                        exoPlayer.prepare()
                                        exoPlayer.play()
                                        onMusicChanged(currentMusicState)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.SkipPrevious,
                                        contentDescription = "Previous",
                                        tint = Color.White
                                    )
                                }

                                IconButton(onClick = {
                                    if (isPlaying) {
                                        exoPlayer.pause()
                                    } else {
                                        exoPlayer.play()
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = Color.White
                                    )
                                }

                                IconButton(onClick = {
                                    if (currentIndex < musicList.size - 1) {
                                        currentMusicState = musicList[currentIndex + 1]
                                        val mediaItem = MediaItem.fromUri(currentMusicState.uri)
                                        exoPlayer.setMediaItem(mediaItem)
                                        exoPlayer.prepare()
                                        exoPlayer.play()
                                        onMusicChanged(currentMusicState)
                                    }
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.SkipNext,
                                        contentDescription = "Next",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatTime(ms: Long): String {
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

