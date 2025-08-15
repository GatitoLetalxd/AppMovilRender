package com.example.projectofinal.ui.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.projectofinal.R
import com.example.projectofinal.data.model.UserImage
import com.example.projectofinal.ui.common.GlassmorphicBox
import com.example.projectofinal.ui.theme.GradientButton
import com.example.projectofinal.ui.theme.AppBrushes
import com.example.projectofinal.utils.Logger
import com.example.projectofinal.viewmodel.ImageViewModel
import com.example.projectofinal.viewmodel.ImageUiState
import com.example.projectofinal.viewmodel.ImageListUiState
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.TimeZone

@Composable
fun UploadScreen(
    onNavigateBack: () -> Unit,
    imageViewModel: ImageViewModel
) {
    val context = LocalContext.current
    val uploadState by imageViewModel.uploadState.collectAsState()
    val imageListState by imageViewModel.imageListState.collectAsState()
    val processState by imageViewModel.processState.collectAsState()
    val deleteState by imageViewModel.deleteState.collectAsState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf<UserImage?>(null) }
    var showImageDialog by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Convertir Uri a File y subir
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                
                // Detectar el tipo MIME y extensión correcta
                val mimeType = context.contentResolver.getType(uri)
                val extension = when (mimeType) {
                    "image/jpeg" -> "jpg"
                    "image/jpg" -> "jpg"
                    "image/png" -> "png"
                    "image/gif" -> "gif"
                    else -> "jpg" // fallback
                }
                
                val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.$extension")
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Pasar el tipo MIME correcto al ViewModel
                imageViewModel.uploadImage(file, mimeType ?: "image/jpeg")
            } catch (e: Exception) {
                Logger.e("UploadScreen", "Error al procesar imagen: ${e.message}")
                // Mostrar error al usuario
                // El error se manejará a través del estado del ViewModel
            }
        }
    }

    LaunchedEffect(uploadState) {
        if (uploadState is ImageUiState.Success) {
            selectedImageUri = null
            imageViewModel.resetUploadState()
        }
    }

    LaunchedEffect(processState) {
        if (processState is ImageUiState.Success) {
            imageViewModel.resetProcessState()
        }
    }

    LaunchedEffect(deleteState) {
        if (deleteState is ImageUiState.Success) {
            imageViewModel.resetDeleteState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBrushes.primaryGradientButton)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    
                    Text(
                        text = "Gestión de Imágenes",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }

            // Upload Section
            item {
                GlassmorphicBox(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Subir Nueva Imagen",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        
                        GradientButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            gradientBrush = AppBrushes.primaryGradientButton
                        ) {
                            if (uploadState is ImageUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.nubeicon),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Seleccionar Imagen")
                            }
                        }
                        
                        when (uploadState) {
                            is ImageUiState.Success -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (uploadState as ImageUiState.Success).message,
                                    color = Color.Green,
                                    fontSize = 14.sp
                                )
                            }
                            is ImageUiState.Error -> {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (uploadState as ImageUiState.Error).message,
                                    color = Color.Red,
                                    fontSize = 14.sp
                                )
                            }
                            else -> {}
                        }
                    }
                }
            }

            // Images List Section
            item {
                GlassmorphicBox(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Mis Imágenes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        when (imageListState) {
                            is ImageListUiState.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Color.White)
                                }
                            }
                            is ImageListUiState.Success -> {
                                val images = (imageListState as ImageListUiState.Success).images
                                if (images.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No tienes imágenes subidas",
                                            color = Color.White.copy(alpha = 0.7f),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                } else {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        images.forEach { image ->
                                            ImageCard(
                                                image = image,
                                                onProcess = { imageViewModel.processImage(image.idImagen) },
                                                onDelete = { showDeleteDialog = image },
                                                onImageClick = { imageUri -> showImageDialog = imageUri },
                                                processState = processState,
                                                deleteState = deleteState
                                            )
                                        }
                                    }
                                }
                            }
                            is ImageListUiState.Error -> {
                                Text(
                                    text = (imageListState as ImageListUiState.Error).message,
                                    color = Color.Red,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { image ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la imagen '${image.nombreArchivo}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        imageViewModel.deleteImage(image.idImagen)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Image Preview Dialog
    showImageDialog?.let { imageUrl ->
        AlertDialog(
            onDismissRequest = { showImageDialog = null },
            title = { Text("Vista previa de imagen") },
            text = {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUrl)
                        .build(),
                    contentDescription = "Vista previa de imagen",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Fit
                )
            },
            confirmButton = {
                TextButton(onClick = { showImageDialog = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun ImageCard(
    image: UserImage,
    onProcess: () -> Unit,
    onDelete: () -> Unit,
    onImageClick: (String) -> Unit,
    processState: ImageUiState,
    deleteState: ImageUiState
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    isoDateFormat.timeZone = TimeZone.getTimeZone("UTC")
    val isProcessing = processState is ImageUiState.Loading
    val isDeleting = deleteState is ImageUiState.Loading

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Image Info
            Text(
                text = image.nombreArchivo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Subida: ${
                    try {
                        val date = isoDateFormat.parse(image.fechaSubida)
                        dateFormat.format(date ?: Date())
                    } catch (e: Exception) {
                        "Fecha no disponible"
                    }
                }",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Images Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Original Image
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Original",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(image.url)
                            .build(),
                        contentDescription = "Imagen original",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onImageClick(image.url) },
                        contentScale = ContentScale.Crop
                    )
                }

                // Processed Image (if available)
                if (image.urlProcesada != null) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Procesada",
                            fontSize = 12.sp,
                            color = Color.Green,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(image.urlProcesada)
                                .build(),
                            contentDescription = "Imagen procesada",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onImageClick(image.urlProcesada!!) },
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    // Placeholder for unprocessed image
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Sin procesar",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Pendiente",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (image.urlProcesada == null) {
                    Button(
                        onClick = onProcess,
                        enabled = !isProcessing && !isDeleting,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue.copy(alpha = 0.8f)
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Procesar")
                    }
                }

                Button(
                    onClick = onDelete,
                    enabled = !isProcessing && !isDeleting,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red.copy(alpha = 0.8f)
                    )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}