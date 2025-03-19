import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.google.android.exoplayer2.ExoPlayer
import ir.sajjadasadi.RitmoPlayer.R

@Composable
fun SpeedControlDialog(
    exoPlayer: ExoPlayer,
    showDialog: MutableState<Boolean>
) {
    if (showDialog.value) {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 2.25f, 2.5f)
        val currentSpeed = remember { mutableStateOf(1.0f) }
        val customSpeedDialog = remember { mutableStateOf(false) }
        val customSpeed = remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(
                    text = stringResource(id = R.string.Select_Playback_Speed),
                    style = MaterialTheme.typography.titleSmall
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in speeds.indices step 3) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (j in 0..2) {
                                if (i + j < speeds.size) {
                                    Button(
                                        onClick = {
                                            exoPlayer.setPlaybackSpeed(speeds[i + j])
                                            currentSpeed.value = speeds[i + j]
                                            showDialog.value = false
                                        },
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(1f)
                                    ) {43
                                        Text(text = "${speeds[i + j]}x", fontSize = 12.sp)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    Button(
                        onClick = { customSpeedDialog.value = true },
                        modifier = Modifier
                            .padding(4.dp)
                            .shadow(8.dp, shape = RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                    ) {
                        Text(
                            style = MaterialTheme.typography.titleSmall,
                            text = stringResource(id = R.string.CustomSpeed),
                            color = Color.White
                        )
                    }
                }
            },
            confirmButton = {},
            properties = DialogProperties(dismissOnClickOutside = true)
        )

        if (customSpeedDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    customSpeedDialog.value = false
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.Enter_Custom_Speed),
                        style = MaterialTheme.typography.titleSmall
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = customSpeed.value,
                            onValueChange = { customSpeed.value = it },
                            label = {
                                Text(stringResource(id = R.string.SpeedEn))

                            },
                            modifier = Modifier.padding(8.dp)
                        )
                        Button(
                            onClick = {
                                val speed = customSpeed.value.toFloatOrNull()
                                if (speed != null && speed > 0) {
                                    exoPlayer.setPlaybackSpeed(speed)
                                    currentSpeed.value = speed
                                    customSpeedDialog.value = false
                                    showDialog.value = false
                                }
                            },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.SetSpeed),
                                style = MaterialTheme.typography.titleSmall
                            )

                        }
                    }
                },
                confirmButton = {},
                properties = DialogProperties(dismissOnClickOutside = true)
            )
        }
    }
}
