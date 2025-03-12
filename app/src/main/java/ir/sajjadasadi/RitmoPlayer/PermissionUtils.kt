package ir.sajjadasadi.RitmoPlayer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun getRequiredPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

fun checkPermission(context: Context): Boolean {
    return getRequiredPermissions().all {
        context.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED
    }
}