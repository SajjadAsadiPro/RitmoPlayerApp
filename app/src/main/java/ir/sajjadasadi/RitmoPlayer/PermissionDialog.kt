package ir.sajjadasadi.RitmoPlayer

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun PermissionDialog(onRequestPermission: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(id = R.string.permission_dialog_title),
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.permission_dialog_text),
                color = Color.LightGray
            )
        },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text(stringResource(id = R.string.permission_dialog_confirm), color = Color.Cyan)
            }
        },
        containerColor = Color.DarkGray,
        textContentColor = Color.White,
        tonalElevation = 8.dp
    )
}