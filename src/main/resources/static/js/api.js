// Función centralizada para peticiones autenticadas
async function fetchAuth(url, options = {}) {
    const token = localStorage.getItem('jwt');

    // Prepara los encabezados
    const headers = {
        ...options.headers,
    };

    // Si hay un token, lo añade a los encabezados
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    // Realiza la petición
    const response = await fetch(url, { ...options, headers });

    // --- INTERCEPTOR GLOBAL ---
    // Si la respuesta es 401 (No Autorizado), el token es inválido o ha expirado
    if (response.status === 401) {
        localStorage.removeItem('jwt'); // Limpia el token
        // Muestra un aviso y redirige al login
        alert('Tu sesión ha expirado. Por favor, inicia sesión de nuevo.');
        window.location.href = '/auth/login?session=expired';
        // Detiene la ejecución para no procesar una respuesta inválida
        throw new Error('Sesión expirada');
    }

    return response;
}
