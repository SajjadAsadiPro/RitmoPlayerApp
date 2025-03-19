package ir.sajjadasadi.RitmoPlayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MusicItemView(music: MusicItem, isPlaying: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    AnimatedVisibility(visible = true) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable { onClick() }
                .clip(RoundedCornerShape(12.dp))
                .alpha(0.8f), // شفاف کردن پس زمینه
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isPlaying) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AsyncImage(
                    model = music.albumArtUri ?: R.drawable.musicico, // در صورت عدم وجود Uri، تصویر پیش‌فرض نمایش داده شود
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    error = painterResource(id = R.drawable.musicico),
                    placeholder = painterResource(id = R.drawable.musicico)
                )
                Column(modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)) {
                    MarqueeText(
                        text = music.title,
                        style = MaterialTheme.typography.titleMedium, // استفاده از تایپوگرافی بزرگ‌تر برای عنوان
                        modifier = Modifier.fillMaxWidth()
                    )
                    MarqueeText(
                        text = music.artist,
                        style = MaterialTheme.typography.bodyMedium, // استفاده از تایپوگرافی کوچک‌تر برای نام خواننده
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                IconButton(onClick = { onDelete() }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun MarqueeText(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}
