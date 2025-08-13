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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.projectofinal.utils.Logger
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
    val authToken by authViewModel.authTokenStream.collectAsState(initial = null)

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
        item { ProfileHeader(userProfile = userProfile, authToken = authToken, onChangePhoto = { photoPickerLauncher.launch("image/*") }) }
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
        item { AdminSection(userProfile = userProfile, authViewModel = authViewModel) }
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
fun ProfileHeader(userProfile: UserProfile, authToken: String?, onChangePhoto: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))

    Box(contentAlignment = Alignment.BottomEnd) {
        val imageUrl = userProfile.fotoPerfilUrl?.let { path -> if(path.isNotBlank()) "$IMAGE_BASE_URL$path" else null }
        Logger.d("ProfileImages", "Header fotoPerfilUrl=${userProfile.fotoPerfilUrl} url=$imageUrl")
        val placeholderPainter = painterResource(id = R.drawable.ic_person)

        val imagePainter = rememberAsyncImagePainter(
            model = imageUrl?.let {
                ImageRequest.Builder(LocalContext.current)
                    .data(it)
                    .apply {
                        if (!authToken.isNullOrBlank()) addHeader("Authorization", "Bearer $authToken")
                    }
                    .build()
            } ?: imageUrl,
            placeholder = placeholderPainter,
            error = placeholderPainter
        )

        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Foto de perfil",
                modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        }

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
fun AdminSection(userProfile: UserProfile, authViewModel: AuthViewModel) {
    val role = userProfile.rol?.lowercase() ?: "usuario"
    val isAdmin =
        role == "admin" || role == "superadmin" || role == "superadministrador" || role == "super-admin"
    val isSuperAdmin = role == "superadmin" || role == "superadministrador" || role == "super-admin"

    Spacer(modifier = Modifier.height(16.dp))
    when {
        !isAdmin -> {
            var motivo by remember { mutableStateOf("") }
            Text("Solicitar rol de Administrador", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                label = { Text("Motivo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { authViewModel.requestAdminRole(motivo) },
                    enabled = motivo.isNotBlank()
                ) { Text("Solicitar") }
            }
        }

        isAdmin -> {
            var selected by remember { mutableStateOf("none") }
            var filterAdminsOnly by remember { mutableStateOf(false) }
            Text("Panel de Administración", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            // Aquí podríamos mostrar solicitudes de admin y listado de usuarios
            // Se deja el layout base; las llamadas se hacen cuando implementemos los flows
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    selected = "requests"
                    authViewModel.loadAdminRequests()
                }) { Text("Solicitudes de admin") }
                OutlinedButton(onClick = {
                    selected = "users"
                    authViewModel.loadAllUsers()
                }) { Text("Usuarios registrados") }
            }
            if (isSuperAdmin) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Gestión de administradores (Superadmin)",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (selected != "none") {
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selected = "none" }) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_ocultar),
                                    contentDescription = "Ocultar",
                                    modifier = Modifier.fillMaxSize(),      // ocupa todo el fondo
                                    contentScale = ContentScale.Crop        // o ContentScale.Fit según el PNG
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                // Secciones dinámicas según selección
                when (selected) {
                    "requests" -> AdminRequestsPanel(authViewModel = authViewModel)
                    "users" -> {
                        // Filtro opcional para mostrar solo administradores
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = filterAdminsOnly,
                                onCheckedChange = { filterAdminsOnly = it })
                            Text("Solo administradores")
                        }
                        AdminUsersPanel(
                            authViewModel = authViewModel,
                            isSuperAdmin = isSuperAdmin,
                            filterAdminsOnly = filterAdminsOnly
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}
@Composable
private fun AdminRequestsPanel(authViewModel: AuthViewModel) {
    val requestsState by authViewModel.adminRequestsUiState.collectAsState()
    when (requestsState) {
        is com.example.projectofinal.ui.uistate.AdminRequestsUiState.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is com.example.projectofinal.ui.uistate.AdminRequestsUiState.Error -> {
            val msg = (requestsState as com.example.projectofinal.ui.uistate.AdminRequestsUiState.Error).message
            Text(text = msg, color = MaterialTheme.colorScheme.error)
        }
        is com.example.projectofinal.ui.uistate.AdminRequestsUiState.Success -> {
            Text("Solicitudes de administrador", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            val items = (requestsState as com.example.projectofinal.ui.uistate.AdminRequestsUiState.Success).requests
            if (items.isEmpty()) {
                Text("No hay solicitudes pendientes")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(items.size) { idx ->
                        val r = items[idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(r.nombre, style = MaterialTheme.typography.bodyLarge)
                                Text(r.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Motivo: ${r.reason}", style = MaterialTheme.typography.bodySmall)
                            }
                            OutlinedButton(onClick = { authViewModel.decideAdminRequest(r.requestId, false) }) { Text("Rechazar") }
                            Spacer(Modifier.width(8.dp))
                            Button(onClick = { authViewModel.decideAdminRequest(r.requestId, true) }) { Text("Aceptar") }
                        }
                        Divider()
                    }
                }
            }
        }
        else -> {}
    }
}

@Composable
private fun AdminUsersPanel(authViewModel: AuthViewModel, isSuperAdmin: Boolean, filterAdminsOnly: Boolean) {
    val usersState by authViewModel.adminUsersUiState.collectAsState()
    when (usersState) {
        is com.example.projectofinal.ui.uistate.AdminUsersUiState.Loading -> {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        is com.example.projectofinal.ui.uistate.AdminUsersUiState.Error -> {
            val msg = (usersState as com.example.projectofinal.ui.uistate.AdminUsersUiState.Error).message
            Text(text = msg, color = MaterialTheme.colorScheme.error)
        }
        is com.example.projectofinal.ui.uistate.AdminUsersUiState.Success -> {
            Text("Usuarios registrados", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            val itemsRaw = (usersState as com.example.projectofinal.ui.uistate.AdminUsersUiState.Success).users
            val items = if (filterAdminsOnly) itemsRaw.filter { it.rol.equals("admin", true) } else itemsRaw
            if (items.isEmpty()) {
                Text("No hay usuarios")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(items.size) { idx ->
                        val u = items[idx]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(u.nombre, style = MaterialTheme.typography.bodyLarge)
                                Text(u.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Rol: ${u.rol}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (isSuperAdmin && u.rol.equals("admin", ignoreCase = true)) {
                                OutlinedButton(onClick = { authViewModel.demoteAdmin(u.userId) }) { Text("Remover admin") }
                            }
                        }
                        Divider()
                    }
                }
            }
        }
        else -> {}
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
                modifier = Modifier.size(25.dp)
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
    when (friendsState) {
        is com.example.projectofinal.ui.uistate.FriendsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is com.example.projectofinal.ui.uistate.FriendsUiState.Error -> {
            val msg = (friendsState as com.example.projectofinal.ui.uistate.FriendsUiState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = msg)
            }
        }
        is com.example.projectofinal.ui.uistate.FriendsUiState.Idle -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sin datos. Desliza para refrescar.")
            }
        }
        is com.example.projectofinal.ui.uistate.FriendsUiState.Success -> {
            val friends = (friendsState as com.example.projectofinal.ui.uistate.FriendsUiState.Success).friends
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(friends.size) { index ->
                    val friend = friends[index]
                    FriendRow(name = friend.nombre, photoPath = friend.profilePictureUrl)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun RequestsListTab(authViewModel: AuthViewModel) {
    val requestsState by authViewModel.friendRequestsUiState.collectAsState()
    when (requestsState) {
        is com.example.projectofinal.ui.uistate.FriendRequestsUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is com.example.projectofinal.ui.uistate.FriendRequestsUiState.Error -> {
            val msg = (requestsState as com.example.projectofinal.ui.uistate.FriendRequestsUiState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = msg)
            }
        }
        is com.example.projectofinal.ui.uistate.FriendRequestsUiState.Idle -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sin solicitudes pendientes.")
            }
        }
        is com.example.projectofinal.ui.uistate.FriendRequestsUiState.Success -> {
            val requests = (requestsState as com.example.projectofinal.ui.uistate.FriendRequestsUiState.Success).requests
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(requests.size) { index ->
                    val req = requests[index]
                    FriendRequestRow(
                        name = req.nombre,
                        photoPath = req.profilePictureUrl,
                        onAccept = { authViewModel.respondToFriendRequest(req.userId, true) },
                        onReject = { authViewModel.respondToFriendRequest(req.userId, false) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun SearchUsersTab(authViewModel: AuthViewModel) {
    val searchState by authViewModel.searchUiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Buscar usuarios") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { authViewModel.searchUsers(query) }, enabled = query.isNotBlank()) {
                Text("Buscar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (searchState) {
            is com.example.projectofinal.ui.uistate.SearchUiState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is com.example.projectofinal.ui.uistate.SearchUiState.Error -> {
                val msg = (searchState as com.example.projectofinal.ui.uistate.SearchUiState.Error).message
                Text(text = msg)
            }
            is com.example.projectofinal.ui.uistate.SearchUiState.Idle -> {
                Text("Ingresa un nombre y presiona Buscar")
            }
            is com.example.projectofinal.ui.uistate.SearchUiState.Success -> {
                val results = (searchState as com.example.projectofinal.ui.uistate.SearchUiState.Success).results
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(results.size) { index ->
                        val item = results[index]
                        SearchResultRow(
                            name = item.nombre,
                            photoPath = item.profilePictureUrl,
                            state = item.estado,
                            onAdd = { authViewModel.sendFriendRequest(item.userId) }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendRow(name: String, photoPath: String?) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        val url = buildImageUrl(photoPath)
        Logger.d("ProfileImages", "FriendRow photoPath=$photoPath url=$url")
        val placeholder = painterResource(id = R.drawable.ic_person)
        val painter = rememberAsyncImagePainter(
            model = url?.let { ImageRequest.Builder(LocalContext.current).data(it).build() } ?: url,
            placeholder = placeholder,
            error = placeholder
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FriendRequestRow(name: String, photoPath: String?, onAccept: () -> Unit, onReject: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        val url = buildImageUrl(photoPath)
        Logger.d("ProfileImages", "FriendRequestRow photoPath=$photoPath url=$url")
        val placeholder = painterResource(id = R.drawable.ic_person)
        val painter = rememberAsyncImagePainter(
            model = url?.let { ImageRequest.Builder(LocalContext.current).data(it).build() } ?: url,
            placeholder = placeholder,
            error = placeholder
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        OutlinedButton(onClick = onReject) { Text("Rechazar") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onAccept) { Text("Aceptar") }
    }
}

@Composable
private fun SearchResultRow(name: String, photoPath: String?, state: String?, onAdd: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        val url = buildImageUrl(photoPath)
        Logger.d("ProfileImages", "SearchResultRow photoPath=$photoPath url=$url state=$state")
        val placeholder = painterResource(id = R.drawable.ic_person)
        val painter = rememberAsyncImagePainter(
            model = url?.let { ImageRequest.Builder(LocalContext.current).data(it).build() } ?: url,
            placeholder = placeholder,
            error = placeholder
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = name, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        when (state) {
            "aceptado" -> Text("Amigos", color = MaterialTheme.colorScheme.primary)
            "pendiente" -> Text("Pendiente", color = MaterialTheme.colorScheme.onSurfaceVariant)
            else -> Button(onClick = onAdd) { Text("Agregar") }
        }
    }
}

private fun buildImageUrl(path: String?): String? {
    if (path.isNullOrBlank()) return null
    if (path.startsWith("http")) return path
    // Si viene solo el nombre de archivo, prefijar carpeta esperada
    val fixed = when {
        path.contains("/uploads/") -> path
        path.startsWith("/") && path.contains("uploads/") -> path
        path.startsWith("/") -> "/uploads/profile$path"
        path.contains("/") -> "/$path"
        else -> "/uploads/profile/$path"
    }
    val full = "$IMAGE_BASE_URL$fixed"
    Logger.d("ProfileImages", "buildImageUrl in=$path out=$full")
    return full
}
