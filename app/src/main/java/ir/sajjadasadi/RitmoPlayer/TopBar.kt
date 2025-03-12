package ir.sajjadasadi.RitmoPlayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ir.sajjadasadi.RitmoPlayer.ui.theme.Gold

@Composable
fun TopBar(
    isShuffling: Boolean,
    isReversed: Boolean,
    isLooping: Boolean,
    isSearchVisible: Boolean,
    onShuffleToggle: () -> Unit,
    onReverseToggle: () -> Unit,
    onLoopToggle: () -> Unit,
    onSearchToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onShuffleToggle) {
            Icon(
                imageVector = Icons.Default.Shuffle,
                contentDescription = "Shuffle",
                tint = if (isShuffling) Gold else MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onReverseToggle) {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = "Reverse",
                tint = if (isReversed) Gold else MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onSearchToggle) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (isSearchVisible) Gold else MaterialTheme.colorScheme.onSurface
            )
        }
        IconButton(onClick = onLoopToggle) {
            Icon(
                imageVector = Icons.Default.Loop,
                contentDescription = "Loop",
                tint = if (isLooping) Gold else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}