package com.example.projectofinal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectofinal.data.model.*
import com.example.projectofinal.data.repository.ImageRepository
import com.example.projectofinal.data.datastore.UserPreferencesRepository
import com.example.projectofinal.utils.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

sealed class ImageUiState {
    object Idle : ImageUiState()
    object Loading : ImageUiState()
    data class Success(val message: String) : ImageUiState()
    data class Error(val message: String) : ImageUiState()
}

sealed class ImageListUiState {
    object Loading : ImageListUiState()
    data class Success(val images: List<UserImage>) : ImageListUiState()
    data class Error(val message: String) : ImageListUiState()
}

class ImageViewModel(
    private val imageRepository: ImageRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uploadState = MutableStateFlow<ImageUiState>(ImageUiState.Idle)
    val uploadState: StateFlow<ImageUiState> = _uploadState.asStateFlow()

    private val _imageListState = MutableStateFlow<ImageListUiState>(ImageListUiState.Loading)
    val imageListState: StateFlow<ImageListUiState> = _imageListState.asStateFlow()

    private val _processState = MutableStateFlow<ImageUiState>(ImageUiState.Idle)
    val processState: StateFlow<ImageUiState> = _processState.asStateFlow()

    private val _deleteState = MutableStateFlow<ImageUiState>(ImageUiState.Idle)
    val deleteState: StateFlow<ImageUiState> = _deleteState.asStateFlow()

    init {
        loadUserImages()
    }

    fun uploadImage(imageFile: File, mimeType: String = "image/jpeg") {
        viewModelScope.launch {
            try {
                _uploadState.value = ImageUiState.Loading
                
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    _uploadState.value = ImageUiState.Error("No hay token de autenticación")
                    return@launch
                }

                // Crear MultipartBody.Part para la imagen con el tipo MIME correcto
                val requestBody = imageFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

                val result = imageRepository.uploadImage(token, imagePart)
                result.fold(
                    onSuccess = { imageData ->
                        Logger.d("ImageViewModel", "Imagen subida exitosamente: ${imageData.nombreArchivo}")
                        _uploadState.value = ImageUiState.Success("Imagen subida correctamente")
                        // Recargar la lista de imágenes
                        loadUserImages()
                        
                        // Resetear el estado después de un tiempo para mostrar el mensaje
                        delay(2000)
                        _uploadState.value = ImageUiState.Idle
                    },
                    onFailure = { exception ->
                        Logger.e("ImageViewModel", "Error al subir imagen: ${exception.message}")
                        when {
                            exception.message?.contains("Datos de imagen no encontrados") == true -> {
                                // Si el servidor no devuelve los datos de la imagen pero la subida fue exitosa,
                                // recargamos las imágenes de todas formas
                                Logger.d("ImageViewModel", "Recargando imágenes después de subida exitosa sin datos")
                                _uploadState.value = ImageUiState.Success("Imagen subida correctamente")
                                // Pequeño delay para asegurar que el servidor haya procesado la subida
                                delay(500)
                                loadUserImages()
                                
                                // Resetear el estado después de un tiempo para mostrar el mensaje
                                delay(2000)
                                _uploadState.value = ImageUiState.Idle
                            }
                            exception.message?.contains("Respuesta vacía") == true -> {
                                _uploadState.value = ImageUiState.Error("Error: El servidor no respondió correctamente")
                            }
                            else -> {
                                _uploadState.value = ImageUiState.Error("Error al subir imagen: ${exception.message}")
                            }
                        }
                    }
                )
            } catch (e: Exception) {
                Logger.e("ImageViewModel", "Excepción al subir imagen: ${e.message}")
                _uploadState.value = ImageUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    fun loadUserImages() {
        viewModelScope.launch {
            try {
                _imageListState.value = ImageListUiState.Loading
                
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    _imageListState.value = ImageListUiState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = imageRepository.getUserImages(token)
                result.fold(
                    onSuccess = { images ->
                        Logger.d("ImageViewModel", "Imágenes cargadas: ${images.size}")
                        _imageListState.value = ImageListUiState.Success(images)
                    },
                    onFailure = { exception ->
                        Logger.e("ImageViewModel", "Error al cargar imágenes: ${exception.message}")
                        _imageListState.value = ImageListUiState.Error("Error al cargar imágenes: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Logger.e("ImageViewModel", "Excepción al cargar imágenes: ${e.message}")
                _imageListState.value = ImageListUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    fun processImage(imageId: Int) {
        viewModelScope.launch {
            try {
                _processState.value = ImageUiState.Loading
                
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    _processState.value = ImageUiState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = imageRepository.processImage(token, imageId)
                result.fold(
                    onSuccess = { processedImage ->
                        Logger.d("ImageViewModel", "Imagen procesada exitosamente: ${processedImage.nombreArchivo}")
                        _processState.value = ImageUiState.Success("Imagen procesada correctamente")
                        // Recargar la lista de imágenes para mostrar la versión procesada
                        // Pequeño delay para asegurar que el servidor haya procesado la imagen
                        delay(2000)
                        loadUserImages()
                        
                        // Resetear el estado después de un tiempo para mostrar el mensaje
                        delay(2000)
                        _processState.value = ImageUiState.Idle
                    },
                    onFailure = { exception ->
                        Logger.e("ImageViewModel", "Error al procesar imagen: ${exception.message}")
                        _processState.value = ImageUiState.Error("Error al procesar imagen: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Logger.e("ImageViewModel", "Excepción al procesar imagen: ${e.message}")
                _processState.value = ImageUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    fun deleteImage(imageId: Int) {
        viewModelScope.launch {
            try {
                _deleteState.value = ImageUiState.Loading
                
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    _deleteState.value = ImageUiState.Error("No hay token de autenticación")
                    return@launch
                }

                val result = imageRepository.deleteImage(token, imageId)
                result.fold(
                    onSuccess = { message ->
                        Logger.d("ImageViewModel", "Imagen eliminada exitosamente: $message")
                        _deleteState.value = ImageUiState.Success("Imagen eliminada correctamente")
                        // Recargar la lista de imágenes
                        loadUserImages()
                        
                        // Resetear el estado después de un tiempo para mostrar el mensaje
                        delay(2000)
                        _deleteState.value = ImageUiState.Idle
                    },
                    onFailure = { exception ->
                        Logger.e("ImageViewModel", "Error al eliminar imagen: ${exception.message}")
                        _deleteState.value = ImageUiState.Error("Error al eliminar imagen: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                Logger.e("ImageViewModel", "Excepción al eliminar imagen: ${e.message}")
                _deleteState.value = ImageUiState.Error("Error inesperado: ${e.message}")
            }
        }
    }

    fun resetUploadState() {
        _uploadState.value = ImageUiState.Idle
    }

    fun resetProcessState() {
        _processState.value = ImageUiState.Idle
    }

    fun resetDeleteState() {
        _deleteState.value = ImageUiState.Idle
    }

    fun resetAllStates() {
        _uploadState.value = ImageUiState.Idle
        _processState.value = ImageUiState.Idle
        _deleteState.value = ImageUiState.Idle
    }
}
