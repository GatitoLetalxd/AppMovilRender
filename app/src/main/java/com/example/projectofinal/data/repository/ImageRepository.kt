package com.example.projectofinal.data.repository

import com.example.projectofinal.data.model.*
import com.example.projectofinal.data.network.ApiService
import com.example.projectofinal.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class ImageRepository(private val apiService: ApiService) {

    suspend fun uploadImage(token: String, image: MultipartBody.Part): Result<ImageData> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.uploadImage("Bearer $token", image)
            Logger.d("ImageRepository", "Código de respuesta: ${response.code()}")
            Logger.d("ImageRepository", "Cuerpo de respuesta: ${response.body()}")
            Logger.d("ImageRepository", "Cuerpo de error: ${response.errorBody()?.string()}")
            
            if (response.isSuccessful) {
                response.body()?.let { uploadResponse ->
                    Logger.d("ImageRepository", "Respuesta de subida recibida: $uploadResponse")
                    uploadResponse.image?.let { imageData ->
                        Logger.d("ImageRepository", "Datos de imagen extraídos: $imageData")
                        Result.success(imageData)
                    } ?: Result.failure(Exception("Datos de imagen no encontrados en la respuesta"))
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Logger.e("ImageRepository", "Error al subir imagen: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al subir imagen: ${response.code()}"))
            }
        } catch (e: Exception) {
            Logger.e("ImageRepository", "Excepción al subir imagen: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUserImages(token: String): Result<List<UserImage>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserImages("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.let { images ->
                    Result.success(images)
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Logger.e("ImageRepository", "Error al obtener imágenes: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al obtener imágenes: ${response.code()}"))
            }
        } catch (e: Exception) {
            Logger.e("ImageRepository", "Excepción al obtener imágenes: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun processImage(token: String, imageId: Int): Result<ProcessImageResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.processImage("Bearer $token", imageId)
            if (response.isSuccessful) {
                response.body()?.let { processResponse ->
                    Logger.d("ImageRepository", "Respuesta de procesamiento recibida: $processResponse")
                    Result.success(processResponse)
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Logger.e("ImageRepository", "Error al procesar imagen: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al procesar imagen: ${response.code()}"))
            }
        } catch (e: Exception) {
            Logger.e("ImageRepository", "Excepción al procesar imagen: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun downloadProcessedImage(token: String, imageId: Int): Result<ResponseBody> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.downloadProcessedImage("Bearer $token", imageId)
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    Result.success(responseBody)
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Logger.e("ImageRepository", "Error al descargar imagen: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al descargar imagen: ${response.code()}"))
            }
        } catch (e: Exception) {
            Logger.e("ImageRepository", "Excepción al descargar imagen: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteImage(token: String, imageId: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteImage("Bearer $token", imageId)
            if (response.isSuccessful) {
                response.body()?.let { deleteResponse ->
                    Result.success(deleteResponse.message)
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Logger.e("ImageRepository", "Error al eliminar imagen: ${response.code()} - $errorBody")
                Result.failure(Exception("Error al eliminar imagen: ${response.code()}"))
            }
        } catch (e: Exception) {
            Logger.e("ImageRepository", "Excepción al eliminar imagen: ${e.message}")
            Result.failure(e)
        }
    }
}
