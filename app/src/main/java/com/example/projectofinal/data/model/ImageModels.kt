package com.example.projectofinal.data.model

import com.google.gson.annotations.SerializedName

data class ImageUploadResponse(
    val message: String,
    val image: ImageData
)

data class ImageData(
    @SerializedName("id_imagen")
    val idImagen: Int,
    @SerializedName("nombre_archivo")
    val nombreArchivo: String,
    @SerializedName("ruta_archivo")
    val rutaArchivo: String,
    val url: String,
    @SerializedName("fecha_subida")
    val fechaSubida: String,
    @SerializedName("usuario_id")
    val usuarioId: Int
)

data class UserImage(
    @SerializedName("id_imagen")
    val idImagen: Int,
    @SerializedName("nombre_archivo")
    val nombreArchivo: String,
    @SerializedName("ruta_archivo")
    val rutaArchivo: String,
    @SerializedName("ruta_archivo_procesada")
    val rutaArchivoProcesada: String?,
    @SerializedName("fecha_subida")
    val fechaSubida: String,
    @SerializedName("usuario_id")
    val usuarioId: Int,
    val url: String,
    @SerializedName("url_procesada")
    val urlProcesada: String?
)

data class ProcessImageResponse(
    val message: String,
    @SerializedName("processedPath")
    val processedPath: String,
    @SerializedName("url_procesada")
    val urlProcesada: String
)

data class ProcessedImage(
    @SerializedName("id_imagen")
    val idImagen: Int,
    @SerializedName("nombre_archivo")
    val nombreArchivo: String,
    @SerializedName("ruta_archivo_procesada")
    val rutaArchivoProcesada: String,
    @SerializedName("url_procesada")
    val urlProcesada: String
)

data class GenericImageResponse(
    val message: String
)
