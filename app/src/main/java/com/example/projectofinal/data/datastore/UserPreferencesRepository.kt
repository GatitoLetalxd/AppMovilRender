package com.example.projectofinal.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.projectofinal.utils.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(context: Context) {

    companion object {
        private const val TAG = "UserPreferencesRepo"
    }

    private val appContext = context.applicationContext

    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = stringPreferencesKey("user_id")
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
    }

    val authToken: Flow<String?> = appContext.dataStore.data
        .catch { exception ->
            Logger.e(TAG, "Error reading auth token: ${exception.message}", exception)
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN]
        }

    val userId: Flow<String?> = appContext.dataStore.data
        .catch { exception ->
            Logger.e(TAG, "Error reading user ID: ${exception.message}", exception)
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_ID]
        }

    val displayName: Flow<String?> = appContext.dataStore.data
        .catch { exception ->
            Logger.e(TAG, "Error reading display name: ${exception.message}", exception)
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.DISPLAY_NAME]
        }

    val userEmail: Flow<String?> = appContext.dataStore.data
        .catch { exception ->
            Logger.e(TAG, "Error reading user email: ${exception.message}", exception)
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_EMAIL]
        }

    suspend fun saveAuthToken(token: String) {
        try {
            appContext.dataStore.edit { preferences ->
                preferences[PreferencesKeys.AUTH_TOKEN] = token
            }
            Logger.d(TAG, "Auth token saved successfully")
        } catch (e: Exception) {
            Logger.e(TAG, "Error saving auth token: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveUserId(id: String) {
        try {
            appContext.dataStore.edit { preferences ->
                preferences[PreferencesKeys.USER_ID] = id
            }
            Logger.d(TAG, "User ID saved successfully: $id")
        } catch (e: Exception) {
            Logger.e(TAG, "Error saving user ID: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveDisplayName(name: String) {
        try {
            appContext.dataStore.edit { preferences ->
                preferences[PreferencesKeys.DISPLAY_NAME] = name
            }
            Logger.d(TAG, "Display name saved successfully: $name")
        } catch (e: Exception) {
            Logger.e(TAG, "Error saving display name: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveAuthDetails(
        token: String,
        userId: String,
        displayName: String,
        email: String? = null
    ) {
        try {
            appContext.dataStore.edit { preferences ->
                preferences[PreferencesKeys.AUTH_TOKEN] = token
                preferences[PreferencesKeys.USER_ID] = userId
                preferences[PreferencesKeys.DISPLAY_NAME] = displayName
                if (email != null) {
                    preferences[PreferencesKeys.USER_EMAIL] = email
                }
            }
            Logger.logDataOperation(TAG, "Save auth details", "user credentials", true)
        } catch (e: Exception) {
            Logger.e(TAG, "Error saving auth credentials: ${e.message}", e)
            throw e
        }
    }

    suspend fun clearAuthCredentials() {
        try {
            appContext.dataStore.edit { preferences ->
                preferences.remove(PreferencesKeys.AUTH_TOKEN)
                preferences.remove(PreferencesKeys.USER_ID)
                preferences.remove(PreferencesKeys.DISPLAY_NAME)
                preferences.remove(PreferencesKeys.USER_EMAIL)
            }
            Logger.logDataOperation(TAG, "Clear auth credentials", "user credentials", true)
        } catch (e: Exception) {
            Logger.e(TAG, "Error clearing auth credentials: ${e.message}", e)
            throw e
        }
    }
}