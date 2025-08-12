package com.example.projectofinal.utils

import android.util.Log

object Logger {
    
    private const val MAX_TAG_LENGTH = 23 // Android limita la longitud del tag
    
    fun d(tag: String, message: String, throwable: Throwable? = null) {
        val truncatedTag = tag.take(MAX_TAG_LENGTH)
        if (throwable != null) {
            Log.d(truncatedTag, message, throwable)
        } else {
            Log.d(truncatedTag, message)
        }
    }
    
    fun i(tag: String, message: String, throwable: Throwable? = null) {
        val truncatedTag = tag.take(MAX_TAG_LENGTH)
        if (throwable != null) {
            Log.i(truncatedTag, message, throwable)
        } else {
            Log.i(truncatedTag, message)
        }
    }
    
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        val truncatedTag = tag.take(MAX_TAG_LENGTH)
        if (throwable != null) {
            Log.w(truncatedTag, message, throwable)
        } else {
            Log.w(truncatedTag, message)
        }
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val truncatedTag = tag.take(MAX_TAG_LENGTH)
        if (throwable != null) {
            Log.e(truncatedTag, message, throwable)
        } else {
            Log.e(truncatedTag, message)
        }
    }
    
    fun v(tag: String, message: String, throwable: Throwable? = null) {
        val truncatedTag = tag.take(MAX_TAG_LENGTH)
        if (throwable != null) {
            Log.v(truncatedTag, message, throwable)
        } else {
            Log.v(truncatedTag, message)
        }
    }
    
    // Métodos de conveniencia para logging de eventos comunes
    fun logNetworkCall(tag: String, endpoint: String, method: String) {
        d(tag, "Network call: $method $endpoint")
    }
    
    fun logNetworkSuccess(tag: String, endpoint: String, responseCode: Int) {
        i(tag, "Network success: $endpoint (Code: $responseCode)")
    }
    
    fun logNetworkError(tag: String, endpoint: String, error: String, throwable: Throwable? = null) {
        e(tag, "Network error: $endpoint - $error", throwable)
    }
    
    fun logUserAction(tag: String, action: String, userId: String? = null) {
        val userInfo = if (userId != null) " for user: $userId" else ""
        i(tag, "User action: $action$userInfo")
    }
    
    fun logDataOperation(tag: String, operation: String, entity: String, success: Boolean) {
        val status = if (success) "successful" else "failed"
        i(tag, "Data operation: $operation on $entity - $status")
    }
}
