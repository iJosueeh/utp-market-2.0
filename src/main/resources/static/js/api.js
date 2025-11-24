/**
 * Función centralizada para peticiones autenticadas con sesiones HTTP.
 * La aplicación usa Form Login con sesiones, NO JWT.
 */
async function fetchAuth(url, options = {}) {
  console.log("Fetching URL: ", url); // URL que se está intentando acceder
  // Realiza la petición - Spring Security maneja la autenticación con cookies de sesión
  const response = await fetch(url, {
    ...options,
    credentials: "same-origin", // Incluir cookies de sesión en la petición
  });

  // Si la respuesta es 401 (No Autorizado), la sesión ha expirado o el usuario no está autenticado
  if (response.status === 401) {
    console.error("Fetch failed with 401 for URL: ", url); // URL que falla con el error 401
    // Redirigir al login sin mostrar alert (mejor UX)
    window.location.href = "/auth/login";
    throw new Error("No autorizado");
  }

  // Si la respuesta es 403 (Forbidden), el usuario no tiene permisos
  if (response.status === 403) {
    throw new Error("No tienes permisos para realizar esta acción");
  }

  return response;
}
