package com.example.projectofinal.ui.auth

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.projectofinal.R
import com.example.projectofinal.data.model.UserProfile
import com.example.projectofinal.ui.uistate.ProfileUiState
import com.example.projectofinal.viewmodel.AuthViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.content.Context
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private const val IMAGE_BASE_URL = "http://100.73.162.98:5000"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    LaunchedEffect(key1 = true) {
        authViewModel.fetchUserProfile()
        authViewModel.getFriendsList()
        authViewModel.getPendingRequests()
    }

    val profileState by authViewModel.profileUiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mi Perfil") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = profileState) {
                is ProfileUiState.Loading -> CircularProgressIndicator()
                is ProfileUiState.Success -> {
                    ProfileContent(
                        userProfile = state.userProfile,
                        authViewModel = authViewModel,
                        onLogout = { authViewModel.logoutUser() }
                    )
                }
                is ProfileUiState.Error -> Text(text = "Error: ${state.message}")
                is ProfileUiState.Idle -> Text(text = "Bienvenido a tu perfil.")
            }
        }
    }
}

@Composable
fun ProfileContent(userProfile: UserProfile, authViewModel: AuthViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current

    // Estado local para edición de nombre y email
    var editedName by remember(userProfile.nombre) { mutableStateOf(userProfile.nombre ?: "") }
    var editedEmail by remember(userProfile.correo) { mutableStateOf(userProfile.correo ?: "") }

    // Selector de imagen
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val part = uriToMultipart(context, it, partName = "photo")
            if (part != null) {
                authViewModel.uploadProfilePhoto(part)
            }
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item { ProfileHeader(userProfile = userProfile, onChangePhoto = { photoPickerLauncher.launch("image/*") }) }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Formulario simple de edición de perfil con botón dinámico
            var isEditing by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = editedName,
                onValueChange = { editedName = it },
                label = { Text("Nombre") },
                singleLine = true,
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = editedEmail,
                onValueChange = { editedEmail = it },
                label = { Text("Correo") },
                singleLine = true,
                enabled = isEditing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (!isEditing) {
                        isEditing = true
                    } else {
                        authViewModel.updateUserProfile(editedName, editedEmail)
                        isEditing = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(if (!isEditing) "Editar perfil" else "Guardar cambios")
            }
        }
        item { Divider(modifier = Modifier.padding(vertical = 24.dp)) }
        item { UserInfoSection(userProfile = userProfile, onLogout = onLogout) }
        item { Divider(modifier = Modifier.padding(vertical = 24.dp)) }
        item { FriendsSection(authViewModel = authViewModel) }
    }
}

private fun uriToMultipart(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
    return try {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/*"

        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()
        inputStream.close()

        val requestBody: RequestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

        // Obtener nombre de archivo del uri
        var fileName = "upload.jpg"
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }

        MultipartBody.Part.createFormData(partName, fileName, requestBody)
    } catch (e: Exception) {
        null
    }
}

@Composable
fun ProfileHeader(userProfile: UserProfile, onChangePhoto: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))

    Box(contentAlignment = Alignment.BottomEnd) {
        val imageUrl = userProfile.fotoPerfilUrl?.let { path -> if(path.isNotBlank()) "$IMAGE_BASE_URL$path" else null }
        val placeholderPainter = painterResource(id = R.drawable.ic_person)

        val imagePainter = rememberAsyncImagePainter(
            model = imageUrl,
            placeholder = placeholderPainter,
            error = placeholderPainter
        )

        Image(
            painter = imagePainter,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Crop
        )

        IconButton(
            onClick = onChangePhoto,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .size(36.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "Cambiar foto de perfil",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = userProfile.nombre ?: "Sin nombre",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(4.dp))

    Text(
        text = userProfile.correo ?: "Sin correo",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserInfoSection(userProfile: UserProfile, onLogout: () -> Unit) {
    val formattedDate = userProfile.fechaRegistro?.let {
        try {
            val zonedDateTime = ZonedDateTime.parse(it)
            val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
            zonedDateTime.format(formatter)
        } catch (e: Exception) {
            "Fecha inválida"
        }
    } ?: "No disponible"

    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(horizontal = 16.dp)) {
        InfoCard(
            iconRes = R.drawable.ic_shield,
            label = "Rol",
            value = userProfile.rol?.replaceFirstChar { it.titlecase(Locale.getDefault()) } ?: "No disponible"
        )
        InfoCard(
            iconRes = R.drawable.ic_date_range,
            label = "Miembro desde",
            value = formattedDate
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* TODO: Navegar a pantalla de cambiar contraseña */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
            )
            Text("Cambiar Contraseña")
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
            )
            Text("Cerrar Sesión")
        }
    }
}

@Composable
fun InfoCard(@DrawableRes iconRes: Int, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.bodySmall)
                Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun FriendsSection(authViewModel: AuthViewModel) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Amigos", "Solicitudes", "Buscar")

    Column(modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)) {
        TabRow(selectedTabIndex = tabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index }
                )
            }
        }

        when (tabIndex) {
            0 -> FriendsListTab(authViewModel)
            1 -> RequestsListTab(authViewModel)
            2 -> SearchUsersTab(authViewModel)
        }
    }
}

@Composable
fun FriendsListTab(authViewModel: AuthViewModel) {
    val friendsState by authViewModel.friendsUiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("Pestaña de Amigos")
    }
}

@Composable
fun RequestsListTab(authViewModel: AuthViewModel) {
    val requestsState by authViewModel.friendRequestsUiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("Pestaña de Solicitudes")
    }
}

@Composable
fun SearchUsersTab(authViewModel: AuthViewModel) {
    val searchState by authViewModel.searchUiState.collectAsState()
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text("Pestaña de Búsqueda")
    }
}