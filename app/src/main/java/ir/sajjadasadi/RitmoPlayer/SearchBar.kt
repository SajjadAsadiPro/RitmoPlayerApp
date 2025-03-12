package ir.sajjadasadi.RitmoPlayer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(isSearchVisible: Boolean, searchText: String, onSearchTextChange: (String) -> Unit) {
    AnimatedVisibility(visible = isSearchVisible) {
        TextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            placeholder = { Text("جستجوی موزیک...") },
            singleLine = true
        )
    }
}