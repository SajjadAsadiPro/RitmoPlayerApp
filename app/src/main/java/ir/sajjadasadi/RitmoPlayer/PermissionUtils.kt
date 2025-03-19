package ir.sajjadasadi.RitmoPlayer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.net.toUri

fun getRequiredPermissions(): Array<String> {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        else -> {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}

fun checkPermission(context: Context): Boolean {
    return getRequiredPermissions().all {
        context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    } && hasManageStoragePermission(context)
}

// بررسی مجوز مدیریت فایل‌ها در اندروید 11 به بالا
fun hasManageStoragePermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        true
    }
}

// باز کردن صفحه تنظیمات برای فعال کردن دسترسی حذف فایل‌ها
fun requestManageStoragePermission(activity: Activity, requestCode: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = ("package:" + activity.packageName).toUri()
            activity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            activity.startActivityForResult(intent, requestCode)
        }
    }
}

fun onPermissionسGranted(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
    context.startActivity(intent)
}
