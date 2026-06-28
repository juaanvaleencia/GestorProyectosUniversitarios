# Firebase 

## Importante: ¿dónde estás en la consola?

En tu captura **NO** estás en *Authentication*. Estás en:

**Configuración del proyecto** (icono engranaje) → pestaña **General**

Eso es **correcto** para copiar las claves del frontend. *Authentication* es otro menú (lo usaremos después para activar email/contraseña).

```
Menú izquierdo Firebase
├── Authentication     ← activar login email (paso B)
├── Configuración ⚙️   ← estás AQUÍ (paso A: claves)
│   └── General
└── ...
```

---

## Paso A — Copiar la configuración web (donde estás ahora)

1. En **Configuración del proyecto → General**, baja hasta **Tus apps**.
2. Haz clic en tu app web **GestionProyectosUniversitarios**.
3. En **Configuración del SDK**, pulsa el botón **`Config`** (no `npm`).
4. Verás un bloque como este:

```javascript
const firebaseConfig = {
  apiKey: "AIza....",
  authDomain: "gestionproyectosuniversitarios.firebaseapp.com",
  projectId: "gestionproyectosuniversitarios",
  storageBucket: "gestionproyectosuniversitarios.firebasestorage.app",
  messagingSenderId: "81497832481",
  appId: "1:81497832481:web:ce6674247d7e7cd8c137b0"
};
```

5. Crea el archivo `frontend-react/.env` (copia desde `.env.example`) y pega cada valor:

| En firebaseConfig | En tu archivo `.env` |
|-------------------|----------------------|
| `apiKey` | `VITE_FIREBASE_API_KEY` |
| `authDomain` | `VITE_FIREBASE_AUTH_DOMAIN` |
| `projectId` | `VITE_FIREBASE_PROJECT_ID` |
| `storageBucket` | `VITE_FIREBASE_STORAGE_BUCKET` |
| `messagingSenderId` | `VITE_FIREBASE_MESSAGING_SENDER_ID` |
| `appId` | `VITE_FIREBASE_APP_ID` |

6. Añade también:

```env
VITE_API_BASE_URL=http://localhost:8080
```

### Plantilla ya rellenada con tus datos (solo falta apiKey)

Copia esto en `frontend-react/.env` y sustituye `PEGAR_API_KEY_AQUI` por el `apiKey` del paso 4:

```env
VITE_FIREBASE_API_KEY=PEGAR_API_KEY_AQUI
VITE_FIREBASE_AUTH_DOMAIN=gestionproyectosuniversitarios.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=gestionproyectosuniversitarios
VITE_FIREBASE_STORAGE_BUCKET=gestionproyectosuniversitarios.firebasestorage.app
VITE_FIREBASE_MESSAGING_SENDER_ID=81497832481
VITE_FIREBASE_APP_ID=1:81497832481:web:ce6674247d7e7cd8c137b0
VITE_API_BASE_URL=http://localhost:8080
```

> Si `storageBucket` en Firebase es distinto (p. ej. termina en `.appspot.com`), usa **exactamente** el que te muestra `Config`, no el de esta plantilla.

---

## Paso B — Activar login (Authentication)

1. En el **menú izquierdo** de Firebase, entra en **Authentication** (icono de personas).
2. Si es la primera vez, pulsa **Comenzar** / **Get started**.
3. Pestaña **Método de acceso** / **Sign-in method**.
4. Entra en **Correo electrónico/Contraseña** / **Email/Password**.
5. **Activa** el interruptor y guarda.
6. (Opcional) Pestaña **Configuración** → **Dominios autorizados** → comprueba que aparece `localhost`.

Sin este paso, el `.env` estará bien pero el registro/login fallará.

---

## Paso C — Probar en tu app

```bash
cd frontend-react
npm install
npm run dev
```

1. Abre http://localhost:5173
2. **Registrarse** con un email y contraseña (mín. 6 caracteres).
3. Tras registrarte deberías entrar al dashboard.

Si algo falla, abre F12 → Consola y mira el error (suele ser dominio no autorizado o Email/Password no activado).

---

## Paso D — Backend (OIDC activo)

La API **exige** el token JWT de Firebase en todas las rutas `/api/*` (excepto health).
El proyecto está configurado como `gestionproyectosuniversitarios` en los `application.properties` de cada microservicio.

---

## Resumen visual

| Qué quieres | Dónde en Firebase |
|-------------|-------------------|
| Claves para React | ⚙️ Configuración → General → app web → **Config** |
| Permitir email/login | **Authentication** → Método de acceso → Email/Password |
| Ver usuarios registrados | **Authentication** → Users |

---

## Importante

Debes iniciar sesión con Firebase. Sin token válido la API responderá **401 Unauthorized**.
