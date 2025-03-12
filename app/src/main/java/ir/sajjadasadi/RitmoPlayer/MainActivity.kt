package ir.sajjadasadi.RitmoPlayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import ir.sajjadasadi.RitmoPlayer.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                RequestPermissionAndShowMusicScreen()
            }
        }
    }
}

@Composable
fun RequestPermissionAndShowMusicScreen() {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(checkPermission(context)) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions -> permissionGranted = permissions.values.all { it } }

    if (permissionGranted) {
        MusicPlayerScreen(contentResolver = context.contentResolver)
    } else {
        PermissionDialog(onRequestPermission = {
            requestPermissionLauncher.launch(getRequiredPermissions())
        })
    }
}