const API_BASE_URL = ""; // Usar ruta relativa
const TOKEN_KEY = "accessToken";
const REFRESH_TOKEN_KEY = "refreshToken";
const USER_EMAIL_KEY = "userEmail";
const USER_NAME_KEY = "userName";
const USER_ROLE_KEY = "userRole";

/**
 * Maneja el login del usuario
 */
async function handleLogin(email, password) {
  try {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });

    if (response.ok) {
      const data = await response.json();

      // Guardar tokens y datos del usuario
      localStorage.setItem(TOKEN_KEY, data.token);
      localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
      localStorage.setItem(USER_EMAIL_KEY, data.email);
      localStorage.setItem(USER_NAME_KEY, data.nombre);
      localStorage.setItem(USER_ROLE_KEY, data.rol);

      return { success: true, data };
    } else {
      const error = await response.json();
      return {
        success: false,
        message: error.message || "Credenciales inválidas",
      };
    }
  } catch (error) {
    console.error("Error en login:", error);
    return {
      success: false,
      message: "Error de conexión. Intenta nuevamente.",
    };
  }
}

/**
 * Redirige al usuario según su rol
 */
function redirectByRole(rol) {
  switch (rol) {
    case "ADMIN":
      window.location.href = "/admin/dashboard";
      break;
    case "VENDEDOR":
      window.location.href = "/vendedor/dashboard";
      break;
    default:
      window.location.href = "/";
  }
}

/**
 * Cierra sesión del usuario
 */
function logout() {
  localStorage.clear();
  window.location.href = "/";
}

/**
 * Verifica si el usuario está autenticado
 */
function isAuthenticated() {
  return localStorage.getItem(TOKEN_KEY) !== null;
}

/**
 * Obtiene los datos del usuario actual
 */
function getCurrentUser() {
  return {
    email: localStorage.getItem(USER_EMAIL_KEY),
    nombre: localStorage.getItem(USER_NAME_KEY),
    rol: localStorage.getItem(USER_ROLE_KEY),
  };
}

/**
 * Realiza una petición autenticada con manejo automático de refresh token
 */
async function fetchWithAuth(url, options = {}) {
  const token = localStorage.getItem(TOKEN_KEY);

  if (!token) {
    window.location.href = "/auth/login";
    return;
  }

  // Agregar token al header
  options.headers = {
    ...options.headers,
    Authorization: `Bearer ${token}`,
    "Content-Type": "application/json",
  };

  let response = await fetch(url, options);

  // Si el token expiró, intentar renovarlo
  if (response.status === 401) {
    const refreshed = await refreshAccessToken();
    if (refreshed) {
      // Reintentar con el nuevo token
      options.headers["Authorization"] = `Bearer ${localStorage.getItem(
        TOKEN_KEY
      )}`;
      response = await fetch(url, options);
    } else {
      // Refresh token también expiró
      localStorage.clear();
      window.location.href = "/auth/login";
      return;
    }
  }

  return response;
}

/**
 * Renueva el access token usando el refresh token
 */
async function refreshAccessToken() {
  const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY);

  if (!refreshToken) {
    return false;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ refreshToken }),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem(TOKEN_KEY, data.token);
      localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken);
      return true;
    }
  } catch (error) {
    console.error("Error renovando token:", error);
  }

  return false;
}

document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.getElementById("loginForm");

  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();

      const email = document.getElementById("email").value;
      const password = document.getElementById("password").value;
      const errorDiv = document.getElementById("errorMessage");
      const submitBtn = loginForm.querySelector('button[type="submit"]');

      // Deshabilitar botón mientras se procesa
      submitBtn.disabled = true;
      submitBtn.textContent = "Iniciando sesión...";

      const result = await handleLogin(email, password);

      if (result.success) {
        redirectByRole(result.data.rol);
      } else {
        errorDiv.textContent = result.message;
        errorDiv.style.display = "block";
        submitBtn.disabled = false;
        submitBtn.textContent = "Ingresar";
      }
    });
  }
});
