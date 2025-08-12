# API Documentation - RENDER-TGM Backend

## 📋 Información General

- **Base URL**: `http://100.73.162.98:5000`
- **Autenticación**: JWT Token (Bearer Token)
- **Formato de respuesta**: JSON
- **Encoding**: UTF-8

---

## 🔐 Autenticación

### 1. Registro de Usuario
**Endpoint**: `POST /api/auth/register`

**Body**:
```json
{
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "contraseña": "password123"
}
```

**Respuesta exitosa (201)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 123,
    "nombre": "Juan Pérez",
    "correo": "juan@example.com",
    "rol": "usuario"
  }
}
```

**Respuestas de error**:
```json
// 400 - Datos inválidos
{
  "message": "Todos los campos son requeridos"
}

// 400 - Correo ya existe
{
  "message": "El correo ya está registrado"
}

// 500 - Error del servidor
{
  "message": "Error en el servidor"
}
```

### 2. Inicio de Sesión
**Endpoint**: `POST /api/auth/login`

**Body**:
```json
{
  "correo": "juan@example.com",
  "contraseña": "password123"
}
```

**Respuesta exitosa (200)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 123,
    "nombre": "Juan Pérez",
    "correo": "juan@example.com",
    "rol": "usuario"
  }
}
```

**Respuestas de error**:
```json
// 400 - Datos faltantes
{
  "message": "Correo y contraseña son requeridos"
}

// 401 - Credenciales inválidas
{
  "message": "Credenciales inválidas"
}

// 500 - Error del servidor
{
  "message": "Error en el servidor"
}
```

---

## 👤 Usuarios

### 3. Obtener Perfil del Usuario
**Endpoint**: `GET /api/user/profile`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
{
  "id": 123,
  "nombre": "Juan Pérez",
  "correo": "juan@example.com",
  "rol": "usuario",
  "foto_perfil": "/uploads/profile/foto123.jpg",
  "fecha_registro": "2024-01-15T10:30:00.000Z"
}
```

**Respuestas de error**:
```json
// 401 - Token inválido
{
  "message": "Token inválido"
}

// 404 - Usuario no encontrado
{
  "message": "Usuario no encontrado"
}

// 500 - Error del servidor
{
  "message": "Error al obtener perfil"
}
```

### 4. Actualizar Perfil
**Endpoint**: `PUT /api/user/profile`

**Headers**: `Authorization: Bearer <token>`

**Body**:
```json
{
  "nombre": "Juan Carlos Pérez",
  "correo": "juancarlos@example.com"
}
```

**Respuesta exitosa (200)**:
```json
{
  "message": "Perfil actualizado correctamente"
}
```

**Respuestas de error**:
```json
// 400 - Datos inválidos
{
  "message": "Nombre y correo son requeridos"
}

// 400 - Correo ya existe
{
  "message": "El correo ya está en uso"
}

// 500 - Error del servidor
{
  "message": "Error al actualizar el perfil"
}
```

### 5. Actualizar Foto de Perfil
**Endpoint**: `POST /api/user/profile/photo`

**Headers**: `Authorization: Bearer <token>`

**Body**: `multipart/form-data`
- `photo`: Archivo de imagen (jpeg, jpg, png, máximo 2MB)

**Respuesta exitosa (200)**:
```json
{
  "message": "Foto de perfil actualizada",
  "foto_url": "/uploads/profile/profile-1705312345678-123456789.jpg"
}
```

**Respuestas de error**:
```json
// 400 - Sin archivo
{
  "message": "No se proporcionó ninguna imagen"
}

// 400 - Formato inválido
{
  "message": "Solo se permiten imágenes (jpeg, jpg, png)"
}

// 400 - Archivo muy grande
{
  "message": "El archivo es demasiado grande"
}

// 500 - Error del servidor
{
  "message": "Error al actualizar foto de perfil"
}
```

### 6. Obtener Todos los Usuarios
**Endpoint**: `GET /api/user/list`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
[
  {
    "id_usuario": 124,
    "nombre": "María García",
    "correo": "maria@example.com",
    "rol": "usuario",
    "fecha_registro": "2024-01-16T09:15:00.000Z"
  },
  {
    "id_usuario": 125,
    "nombre": "Carlos López",
    "correo": "carlos@example.com",
    "rol": "usuario",
    "fecha_registro": "2024-01-17T14:20:00.000Z"
  }
]
```

---

## 👥 Sistema de Amigos

### 7. Buscar Usuarios
**Endpoint**: `GET /api/user/search?query=nombre`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
[
  {
    "id_usuario": 124,
    "nombre": "María García",
    "foto_perfil": "/uploads/profile/foto124.jpg",
    "estado": "pendiente"
  },
  {
    "id_usuario": 125,
    "nombre": "Carlos López",
    "foto_perfil": null,
    "estado": null
  },
  {
    "id_usuario": 126,
    "nombre": "Ana Martínez",
    "foto_perfil": "/uploads/profile/foto126.jpg",
    "estado": "aceptado"
  }
]
```
```

**Estados posibles**:
- `"pendiente"` - Solicitud enviada pero no respondida
- `"aceptado"` - Ya son amigos
- `"rechazado"` - Solicitud rechazada
- `null` - No hay relación de amistad

### 8. Obtener Lista de Amigos
**Endpoint**: `GET /api/user/friends`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
[
  {
    "id_usuario": 124,
    "nombre": "María García",
    "foto_perfil": "/uploads/profile/foto124.jpg"
  },
  {
    "id_usuario": 126,
    "nombre": "Ana Martínez",
    "foto_perfil": "/uploads/profile/foto126.jpg"
  }
]
```

### 9. Obtener Solicitudes Pendientes
**Endpoint**: `GET /api/user/friends/pending`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
[
  {
    "id_usuario": 127,
    "nombre": "Pedro Sánchez",
    "foto_perfil": "/uploads/profile/foto127.jpg"
  },
  {
    "id_usuario": 128,
    "nombre": "Laura Torres",
    "foto_perfil": null
  }
]
```

### 10. Enviar Solicitud de Amistad
**Endpoint**: `POST /api/user/friends/request/:friendId`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
{
  "message": "Solicitud enviada correctamente"
}
```

**Respuestas de error**:
```json
// 400 - Ya existe solicitud
{
  "message": "Ya existe una solicitud de amistad"
}

// 500 - Error del servidor
{
  "message": "Error al enviar solicitud"
}
```

### 11. Aceptar Solicitud de Amistad
**Endpoint**: `POST /api/user/friends/accept/:friendId`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
{
  "message": "Solicitud aceptada"
}
```

**Respuestas de error**:
```json
// 500 - Error del servidor
{
  "message": "Error al aceptar solicitud"
}
```

### 12. Rechazar Solicitud de Amistad
**Endpoint**: `POST /api/user/friends/reject/:friendId`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
{
  "message": "Solicitud rechazada"
}
```

**Respuestas de error**:
```json
// 500 - Error del servidor
{
  "message": "Error al rechazar solicitud"
}
```

---

## 🖼️ Gestión de Imágenes

### 13. Subir Imagen
**Endpoint**: `POST /api/images/upload`

**Headers**: `Authorization: Bearer <token>`

**Body**: `multipart/form-data`
- `image`: Archivo de imagen (jpeg, jpg, png, máximo 10MB)

**Respuesta exitosa (201)**:
```json
{
  "message": "Imagen subida correctamente",
  "image": {
    "id_imagen": 456,
    "nombre_archivo": "123_1705312345678.jpg",
    "ruta_archivo": "uploads/users/123/123_1705312345678.jpg",
    "url": "http://100.73.162.98:5000/uploads/users/123/123_1705312345678.jpg",
    "fecha_subida": "2024-01-15T10:30:00.000Z",
    "usuario_id": 123
  }
}
```

**Respuestas de error**:
```json
// 400 - Sin archivo
{
  "message": "No se proporcionó ninguna imagen"
}

// 400 - Formato inválido
{
  "message": "Solo se permiten imágenes (jpeg, jpg, png)"
}

// 400 - Archivo muy grande
{
  "message": "El archivo es demasiado grande"
}

// 500 - Error del servidor
{
  "message": "Error al subir imagen"
}
```

### 14. Obtener Imágenes del Usuario
**Endpoint**: `GET /api/images`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
[
  {
    "id_imagen": 456,
    "nombre_archivo": "123_1705312345678.jpg",
    "ruta_archivo": "uploads/users/123/123_1705312345678.jpg",
    "ruta_archivo_procesada": "uploads/users/123/processed/123_1705312345678_processed.jpg",
    "fecha_subida": "2024-01-15T10:30:00.000Z",
    "usuario_id": 123,
    "url": "http://100.73.162.98:5000/uploads/users/123/123_1705312345678.jpg",
    "url_procesada": "http://100.73.162.98:5000/uploads/users/123/processed/123_1705312345678_processed.jpg"
  },
  {
    "id_imagen": 457,
    "nombre_archivo": "123_1705312456789.png",
    "ruta_archivo": "uploads/users/123/123_1705312456789.png",
    "ruta_archivo_procesada": null,
    "fecha_subida": "2024-01-15T10:35:00.000Z",
    "usuario_id": 123,
    "url": "http://100.73.162.98:5000/uploads/users/123/123_1705312456789.png",
    "url_procesada": null
  }
]
```

### 15. Procesar Imagen
**Endpoint**: `POST /api/images/:imageId/process`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
{
  "message": "Imagen procesada correctamente",
  "processedImage": {
    "id_imagen": 456,
    "nombre_archivo": "123_1705312345678.jpg",
    "ruta_archivo_procesada": "uploads/users/123/processed/123_1705312345678_processed.jpg",
    "url_procesada": "http://100.73.162.98:5000/uploads/users/123/processed/123_1705312345678_processed.jpg"
  }
}
```

**Respuestas de error**:
```json
// 404 - Imagen no encontrada
{
  "message": "Imagen no encontrada"
}

// 403 - No autorizado
{
  "message": "No tienes permisos para procesar esta imagen"
}

// 500 - Error del servidor
{
  "message": "Error al procesar imagen"
}
```

### 16. Descargar Imagen Procesada
**Endpoint**: `GET /api/images/:imageId/download`

**Headers**: `Authorization: Bearer <token>`

**Respuesta**: Archivo de imagen (binary)

**Respuestas de error**:
```json
// 404 - Imagen no encontrada
{
  "message": "Imagen no encontrada"
}

// 404 - Imagen no procesada
{
  "message": "La imagen no ha sido procesada"
}

// 403 - No autorizado
{
  "message": "No tienes permisos para descargar esta imagen"
}
```

### 17. Eliminar Imagen
**Endpoint**: `DELETE /api/images/:imageId`

**Headers**: `Authorization: Bearer <token>`

**Respuesta exitosa (200)**:
```json
{
  "message": "Imagen eliminada correctamente"
}
```

**Respuestas de error**:
```json
// 404 - Imagen no encontrada
{
  "message": "Imagen no encontrada"
}

// 403 - No autorizado
{
  "message": "No tienes permisos para eliminar esta imagen"
}

// 500 - Error del servidor
{
  "message": "Error al eliminar imagen"
}
```

---

## 🧪 Endpoints de Prueba

### 18. Test de API
**Endpoint**: `GET /api/test`

**Respuesta exitosa (200)**:
```json
{
  "message": "API funcionando correctamente",
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

---

## 📊 Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200 | OK - Solicitud exitosa |
| 201 | Created - Recurso creado exitosamente |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Token inválido o faltante |
| 403 | Forbidden - Sin permisos |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error del servidor |

---

## 🔧 Headers Requeridos

### Para endpoints protegidos:
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

### Para subida de archivos:
```
Authorization: Bearer <jwt_token>
Content-Type: multipart/form-data
```

---

## 📝 Notas Importantes

1. **URLs de imágenes**: Las URLs de imágenes incluyen el IP del servidor (`100.73.162.98:5000`)
2. **Fotos de perfil**: Pueden ser `null` si el usuario no ha subido una
3. **Imágenes procesadas**: Pueden ser `null` si la imagen no ha sido procesada
4. **Estados de amistad**: `pendiente`, `aceptado`, `rechazado`, `null`
5. **Límites de archivos**: 
   - Fotos de perfil: 2MB máximo
   - Imágenes: 10MB máximo
6. **Formatos soportados**: jpeg, jpg, png
7. **Autenticación**: Todos los endpoints excepto `/api/test` requieren token JWT

---

## 🚀 Ejemplos de Uso

### Flujo completo de registro y login:
```bash
# 1. Registrar usuario
curl -X POST http://100.73.162.98:5000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan Pérez","correo":"juan@example.com","contraseña":"password123"}'

# 2. Login
curl -X POST http://100.73.162.98:5000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"juan@example.com","contraseña":"password123"}'

# 3. Usar token para acceder a recursos protegidos
curl -X GET http://100.73.162.98:5000/api/user/profile \
  -H "Authorization: Bearer <token_obtenido_del_login>"
```

### Flujo completo de amistad:
```bash
# 1. Buscar usuarios
curl -X GET "http://100.73.162.98:5000/api/user/search?query=maria" \
  -H "Authorization: Bearer <token>"

# 2. Enviar solicitud
curl -X POST http://100.73.162.98:5000/api/user/friends/request/124 \
  -H "Authorization: Bearer <token>"

# 3. Ver solicitudes pendientes (usuario 124)
curl -X GET http://100.73.162.98:5000/api/user/friends/pending \
  -H "Authorization: Bearer <token_usuario_124>"

# 4. Aceptar solicitud
curl -X POST http://100.73.162.98:5000/api/user/friends/accept/123 \
  -H "Authorization: Bearer <token_usuario_124>"
```
