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
    data class Processing(val message: String) : ImageUiState()
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
    
    // Estado individual para cada imagen que se está procesando
    private val _processingImages = MutableStateFlow<Set<Int>>(emptySet())
    val processingImages: StateFlow<Set<Int>> = _processingImages.asStateFlow()

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
                        
                        // Recargar la lista de imágenes inmediatamente
                        loadUserImages()
                        
                        // Resetear el estado después de mostrar el mensaje
                        delay(3000)
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
                                
                                // Recargar la lista de imágenes inmediatamente
                                loadUserImages()
                                
                                // Resetear el estado después de mostrar el mensaje
                                delay(3000)
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
                // Agregar la imagen al set de procesamiento
                _processingImages.value = _processingImages.value + imageId
                _processState.value = ImageUiState.Loading
                
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    _processState.value = ImageUiState.Error("No hay token de autenticación")
                    _processingImages.value = _processingImages.value - imageId
                    return@launch
                }

                val result = imageRepository.processImage(token, imageId)
                result.fold(
                    onSuccess = { processResponse ->
                        Logger.d("ImageViewModel", "Respuesta del backend - processResponse: $processResponse")
                        Logger.d("ImageViewModel", "Imagen procesada exitosamente: ${processResponse.message}")
                        _processState.value = ImageUiState.Success("Imagen procesada correctamente")
                        
                        // Cambiar a estado de procesamiento
                        _processState.value = ImageUiState.Processing("Procesando imagen...")
                        
                        // Esperar a que la imagen esté realmente procesada en el servidor
                        waitForImageProcessing(imageId)
                        
                        // Resetear el estado después de mostrar el mensaje
                        delay(3000)
                        _processState.value = ImageUiState.Idle
                    },
                    onFailure = { exception ->
                        Logger.e("ImageViewModel", "Error al procesar imagen: ${exception.message}")
                        _processState.value = ImageUiState.Error("Error al procesar imagen: ${exception.message}")
                        
                        // Remover la imagen del set de procesamiento en caso de error
                        _processingImages.value = _processingImages.value - imageId
                        
                        // Resetear el estado después de mostrar el error
                        delay(3000)
                        _processState.value = ImageUiState.Idle
                    }
                )
            } catch (e: Exception) {
                Logger.e("ImageViewModel", "Excepción al procesar imagen: ${e.message}")
                _processState.value = ImageUiState.Error("Error inesperado: ${e.message}")
                // Remover la imagen del set de procesamiento en caso de excepción
                _processingImages.value = _processingImages.value - imageId
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
                        
                        // Recargar la lista de imágenes inmediatamente
                        loadUserImages()
                        
                        // Resetear el estado después de mostrar el mensaje
                        delay(3000)
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
    
    fun refreshImages() {
        loadUserImages()
    }
    
    private suspend fun waitForImageProcessing(imageId: Int) {
        var attempts = 0
        val maxAttempts = 60 // Aumentar a 60 intentos (60 segundos) para imágenes que tardan más
        
        Logger.d("ImageViewModel", "Iniciando polling para imagen $imageId - máximo $maxAttempts segundos")
        
        while (attempts < maxAttempts) {
            delay(1000) // Esperar 1 segundo entre intentos
            attempts++
            
            try {
                val token = userPreferencesRepository.authToken.firstOrNull()
                if (token.isNullOrEmpty()) {
                    Logger.e("ImageViewModel", "Token no disponible durante polling")
                    break
                }
                
                // Verificar si la imagen ya está procesada
                val result = imageRepository.getUserImages(token)
                result.fold(
                    onSuccess = { images ->
                        val targetImage = images.find { it.idImagen == imageId }
                        Logger.d("ImageViewModel", "Intento $attempts: Verificando imagen $imageId")
                        Logger.d("ImageViewModel", "urlProcesada: ${targetImage?.urlProcesada}")
                        Logger.d("ImageViewModel", "urlProcesada es null: ${targetImage?.urlProcesada == null}")
                        Logger.d("ImageViewModel", "urlProcesada está vacía: ${targetImage?.urlProcesada?.isEmpty()}")
                        
                        if (targetImage?.urlProcesada != null && targetImage.urlProcesada.isNotEmpty()) {
                            Logger.d("ImageViewModel", "¡ÉXITO! Imagen $imageId procesada encontrada después de $attempts segundos")
                            Logger.d("ImageViewModel", "URL procesada: ${targetImage.urlProcesada}")
                            // Remover la imagen del set de procesamiento
                            _processingImages.value = _processingImages.value - imageId
                            // Actualizar la lista de imágenes
                            _imageListState.value = ImageListUiState.Success(images)
                            return@waitForImageProcessing
                        }
                    },
                    onFailure = { exception ->
                        Logger.e("ImageViewModel", "Error durante polling: ${exception.message}")
                    }
                )
                
                Logger.d("ImageViewModel", "Intento $attempts/$maxAttempts: Imagen $imageId aún no procesada")
                
            } catch (e: Exception) {
                Logger.e("ImageViewModel", "Excepción durante polling: ${e.message}")
            }
        }
        
        // Si llegamos aquí, hacer una recarga final
        Logger.d("ImageViewModel", "Polling completado después de $maxAttempts segundos, recargando lista final")
        // Remover la imagen del set de procesamiento
        _processingImages.value = _processingImages.value - imageId
        loadUserImages()
    }
}
