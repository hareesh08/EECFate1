package com.hd.eecfate.downloads.support

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.ContextCompat

fun isAndroid11OrAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

fun hasManageExternalStoragePermission(context: Context): Boolean {
    return if (isAndroid11OrAbove()) {
        Environment.isExternalStorageManager()
    } else {
        true
    }
}

fun requestManageExternalStoragePermission(context: Context) {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        data = Uri.parse("package:${context.packageName}")
    }
    (context as Activity).startActivityForResult(intent, 1001)
}

fun checkStoragePermissions(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
}
