package com.example.projectofinal.utils

import android.util.Patterns

object ValidationUtils {
    
    /**
     * Valida si un email tiene formato válido
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && 
               email.length <= AppConstants.Validation.MAX_EMAIL_LENGTH &&
               Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Valida si una contraseña cumple con los requisitos mínimos
     */
    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && 
               password.length >= AppConstants.Validation.MIN_PASSWORD_LENGTH
    }
    
    /**
     * Valida si un nombre de usuario es válido
     */
    fun isValidUsername(username: String): Boolean {
        return username.isNotBlank() && 
               username.length >= AppConstants.Validation.MIN_USERNAME_LENGTH &&
               username.length <= AppConstants.Validation.MAX_USERNAME_LENGTH
    }
    
    /**
     * Obtiene el mensaje de error para validación de email
     */
    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "El email no puede estar vacío"
            email.length > AppConstants.Validation.MAX_EMAIL_LENGTH -> 
                "El email no puede tener más de ${AppConstants.Validation.MAX_EMAIL_LENGTH} caracteres"
            !isValidEmail(email) -> "Formato de email inválido"
            else -> null
        }
    }
    
    /**
     * Obtiene el mensaje de error para validación de contraseña
     */
    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            password.length < AppConstants.Validation.MIN_PASSWORD_LENGTH -> 
                "La contraseña debe tener al menos ${AppConstants.Validation.MIN_PASSWORD_LENGTH} caracteres"
            else -> null
        }
    }
    
    /**
     * Obtiene el mensaje de error para validación de nombre de usuario
     */
    fun getUsernameErrorMessage(username: String): String? {
        return when {
            username.isBlank() -> "El nombre de usuario no puede estar vacío"
            username.length < AppConstants.Validation.MIN_USERNAME_LENGTH -> 
                "El nombre de usuario debe tener al menos ${AppConstants.Validation.MIN_USERNAME_LENGTH} caracteres"
            username.length > AppConstants.Validation.MAX_USERNAME_LENGTH -> 
                "El nombre de usuario no puede tener más de ${AppConstants.Validation.MAX_USERNAME_LENGTH} caracteres"
            else -> null
        }
    }
}
