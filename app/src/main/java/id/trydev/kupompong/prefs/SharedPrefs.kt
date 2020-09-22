package id.trydev.kupompong.prefs

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {

    private val PREFS_NAME = "id.trydev.kupompong.prefs"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val USER_ID = "USER_ID"
    private val USER_EMAIL = "USER_EMAIL"

    var userId: String?
        get() = prefs.getString(USER_ID, null)
        set(value) = prefs.edit().putString(USER_ID, value).apply()

    var userEmail: String?
        get() = prefs.getString(USER_EMAIL, null)
        set(value) = prefs.edit().putString(USER_EMAIL, value).apply()

    fun resetPrefs() {
        prefs.edit().clear().apply()
    }
}