package vn.aplus.smart.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import vn.aplus.smart.auth.UserSession
import java.io.IOException

private val Context.authDataStore by preferencesDataStore(name = "aplus_auth_session")

class SessionStore(private val context: Context) {
    private object Keys {
        val USER_ID = stringPreferencesKey("user_id")
        val TOKEN = stringPreferencesKey("token")
        val NAME = stringPreferencesKey("name")
        val ROLE = stringPreferencesKey("role")
        val LANGUAGE = stringPreferencesKey("language")
        val LAST_LOGIN_AT = longPreferencesKey("last_login_at")
    }

    val sessionFlow: Flow<UserSession?> = context.authDataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw error
        }
        .map { prefs ->
            val userId = prefs[Keys.USER_ID]
            val token = prefs[Keys.TOKEN]
            if (userId.isNullOrBlank() || token.isNullOrBlank()) {
                null
            } else {
                UserSession(
                    userId = userId,
                    token = token,
                    name = prefs[Keys.NAME].orEmpty().ifBlank { "Aplus User" },
                    role = prefs[Keys.ROLE].orEmpty().ifBlank { "owner" },
                    language = prefs[Keys.LANGUAGE].orEmpty().ifBlank { "vi" },
                    lastLoginAt = prefs[Keys.LAST_LOGIN_AT] ?: System.currentTimeMillis()
                )
            }
        }

    suspend fun saveSession(session: UserSession) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.USER_ID] = session.userId
            prefs[Keys.TOKEN] = session.token
            prefs[Keys.NAME] = session.name
            prefs[Keys.ROLE] = session.role
            prefs[Keys.LANGUAGE] = session.language
            prefs[Keys.LAST_LOGIN_AT] = session.lastLoginAt
        }
    }

    suspend fun saveLanguage(language: String) {
        context.authDataStore.edit { prefs -> prefs[Keys.LANGUAGE] = language }
    }

    suspend fun clearSessionKeepLanguage(language: String) {
        context.authDataStore.edit { prefs ->
            prefs.clear()
            prefs[Keys.LANGUAGE] = language
        }
    }
}
