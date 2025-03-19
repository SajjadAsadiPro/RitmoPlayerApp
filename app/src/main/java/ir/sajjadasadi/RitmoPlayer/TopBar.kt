package ir.sajjadasadi.RitmoPlayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    onSearchToggle: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

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

        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Sort Menu",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("ترتیب برعکس") }, onClick = {
                expanded = false
                onReverseToggle()
            }, leadingIcon = {
                Icon(Icons.Default.SwapVert, contentDescription = null)
            })
            DropdownMenuItem(text = { Text("ترتیب بر اساس نام") }, onClick = {
                expanded = false
                onSortOptionSelected(SortOption.BY_NAME)
            }, leadingIcon = {
                Icon(Icons.Default.SortByAlpha, contentDescription = null)
            })
            DropdownMenuItem(text = { Text("ترتیب بر اساس تاریخ") }, onClick = {
                expanded = false
                onSortOptionSelected(SortOption.BY_DATE)
            }, leadingIcon = {
                Icon(Icons.Default.DateRange, contentDescription = null)
            })
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(id = R.string.Programmer),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

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
