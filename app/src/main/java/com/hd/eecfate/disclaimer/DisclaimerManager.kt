package com.hd.eecfate.disclaimer

import android.content.Context
import android.content.SharedPreferences

class DisclaimerManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

    fun setDisclaimerAccepted(accepted: Boolean) {
        sharedPreferences.edit().putBoolean("DISCLAIMER_ACCEPTED", accepted).apply()
    }

    fun isDisclaimerAccepted(): Boolean {
        return sharedPreferences.getBoolean("DISCLAIMER_ACCEPTED", false)
    }
}