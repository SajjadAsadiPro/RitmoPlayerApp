package ir.sajjadasadi.RitmoPlayer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.DeleteMusic),
                style = MaterialTheme.typography.titleSmall
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.TextConfDel),
                style = MaterialTheme.typography.titleSmall
            )
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onConfirm) {
                    Text(
                        text = stringResource(id = R.string.btndelete),
                        style = MaterialTheme.typography.titleSmall
                    )
                }
                Button(onClick = onDismiss) {
                    Text(

                        text = stringResource(id = R.string.btncancel),
                        style = MaterialTheme.typography.titleSmall,

                        )
                }
            }
        }
    )
}