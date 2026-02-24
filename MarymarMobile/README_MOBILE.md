# Marymar Mobile (Android - Kotlin)

MVP móvil para **CLIENTE** y **MESERO**:
- Registro (sin 2FA según tu backend)
- Login con **código 2FA por email**
- Listado de productos (menú)
- Carrito
- Crear pedido
- Historial de pedidos (cliente)

## 1) Backend (Spring Boot)
1. Ejecuta tu backend (perfil dev):
   - `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
2. Por defecto es `http://localhost:8080/`.

### Endpoints usados por la app
- `POST /api/auth/register`
- `POST /api/auth/login`  (dispara envío de código al email)
- `POST /api/auth/validate-code?email=...&code=...` (retorna token JWT)
- `POST /api/auth/verify-token` con `{ "token": "..." }` (para traer el id del usuario)
- `GET /api/productos`
- `POST /api/pedidos`
- `GET /api/pedidos/cliente/{clienteId}`

> Nota: en el ZIP del backend que me compartiste solo existían `AuthController` y `TestController`.
> Para que el móvil funcione con pedidos/productos, te dejé **un patch** de backend (controllers/service) en esta entrega.

## 2) Configurar la URL del backend en Android
En `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/\"")
```
- **Emulador Android**: `10.0.2.2` apunta a tu PC.
- **Celular físico**: usa `http://<IP_DE_TU_PC>:8080/`.

Si usas celular físico, asegúrate de:
- Backend escuchando en red (si hace falta): `server.address=0.0.0.0`
- Misma red WiFi
- Firewall permita el puerto 8080

## 3) Estructura (similar a tu backend por capas)
- `presentation/` -> UI (Compose) + ViewModels
- `domain/` -> modelos + repositorios (interfaces) + use cases
- `data/` -> llamadas Retrofit + DTOs + implementaciones de repositorio
- `core/` -> SessionStore (DataStore) + interceptores + utilidades

## 4) Flujo de login (2FA)
1. `POST /api/auth/login` => responde `requires2FA=true` y token `null`
2. Pantalla de código => `POST /api/auth/validate-code` => token JWT
3. `POST /api/auth/verify-token` para traer el `id` del usuario
4. Guardamos sesión y el token se agrega como `Authorization: Bearer <token>`

