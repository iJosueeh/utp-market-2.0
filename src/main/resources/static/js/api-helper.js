// api-helper.js - Funciones helper para peticiones autenticadas con JWT

/**
 * Realiza una petición autenticada con manejo automático de refresh token
 */
async function fetchWithAuth(url, options = {}) {
  const token = localStorage.getItem("accessToken");

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

  // Si el token expiró (401), intentar renovarlo
  if (response.status === 401) {
    const refreshed = await refreshAccessToken();
    if (refreshed) {
      // Reintentar con el nuevo token
      options.headers["Authorization"] = `Bearer ${localStorage.getItem(
        "accessToken"
      )}`;
      response = await fetch(url, options);
    } else {
      // Refresh token también expiró, redirigir a login
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
  const refreshToken = localStorage.getItem("refreshToken");

  if (!refreshToken) {
    return false;
  }

  try {
    const response = await fetch("/auth/refresh", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ refreshToken }),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("accessToken", data.token);
      localStorage.setItem("refreshToken", data.refreshToken);
      return true;
    }
  } catch (error) {
    console.error("Error renovando token:", error);
  }

  return false;
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
  return localStorage.getItem("accessToken") !== null;
}

/**
 * Obtiene los datos del usuario actual
 */
function getCurrentUser() {
  return {
    email: localStorage.getItem("userEmail"),
    nombre: localStorage.getItem("userName"),
    rol: localStorage.getItem("userRole"),
  };
}

/**
 * Verifica si el usuario tiene un rol específico
 */
function hasRole(role) {
  const userRole = localStorage.getItem("userRole");
  return userRole === role;
}

// Ejemplo de uso en tus páginas:
//
// // Cargar datos protegidos
// async function loadFavoritos() {
//     const response = await fetchWithAuth('/api/favoritos');
//     if (response.ok) {
//         const data = await response.json();
//         console.log(data);
//     }
// }
//
// // Agregar favorito
// async function addFavorito(productoId) {
//     const response = await fetchWithAuth('/api/favoritos', {
//         method: 'POST',
//         body: JSON.stringify({ productoId })
//     });
//     if (response.ok) {
//         console.log('Favorito agregado');
//     }
// }
