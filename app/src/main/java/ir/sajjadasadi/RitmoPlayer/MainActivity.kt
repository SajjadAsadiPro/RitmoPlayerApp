package ir.sajjadasadi.RitmoPlayer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.net.toUri
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
    var manageStoragePermissionGranted by remember { mutableStateOf(hasManageStoragePermission(context)) }

    // درخواست مجوز خواندن فایل‌ها
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionGranted = permissions.values.all { it }
    }

    // درخواست مجوز مدیریت همه فایل‌ها در اندروید ۱۱ و بالاتر
    val requestManageStorageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        manageStoragePermissionGranted = hasManageStoragePermission(context)
        if (manageStoragePermissionGranted) restartApp(context)
    }

    // بررسی و درخواست مجوزها
    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            requestPermissionLauncher.launch(getRequiredPermissions())
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !manageStoragePermissionGranted) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = ("package:" + context.packageName).toUri()
            }
            requestManageStorageLauncher.launch(intent)
        }
    }

    // نمایش صفحه پلیر در صورت دریافت مجوزها
    if (permissionGranted && (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || manageStoragePermissionGranted)) {
        MusicPlayerScreen(contentResolver = context.contentResolver)
    }
}

// راه‌اندازی مجدد اپلیکیشن
fun restartApp(context: android.content.Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
    intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
    Runtime.getRuntime().exit(0) // خروج کامل برای جلوگیری از کش شدن مجوزهای قبلی
}
