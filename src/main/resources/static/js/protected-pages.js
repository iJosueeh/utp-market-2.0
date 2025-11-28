// protected-pages.js - Middleware para páginas protegidas con JWT
(function () {
  "use strict";

  // Configuración de rutas protegidas
  const PROTECTED_ROUTES = {
    admin: ["/admin"],
    vendedor: ["/vendedor"],
    authenticated: ["/perfil", "/carrito", "/pedidos", "/favoritos"],
  };

  // Verificar si la ruta actual es protegida
  function isProtectedRoute(path) {
    return Object.values(PROTECTED_ROUTES)
      .flat()
      .some((route) => path.startsWith(route));
  }

  // Verificar autenticación al cargar la página
  function checkAuthOnPageLoad() {
    const currentPath = window.location.pathname;

    if (!isProtectedRoute(currentPath)) {
      return; // Página pública, no hacer nada
    }

    const token = localStorage.getItem("accessToken");
    const userRole = localStorage.getItem("userRole");

    if (!token) {
      // No hay token, redirigir a login
      console.log("No token found, redirecting to login");
      window.location.href =
        "/auth/login?redirect=" + encodeURIComponent(currentPath);
      return;
    }

    // Verificar permisos por rol
    if (currentPath.startsWith("/admin") && userRole !== "ADMIN") {
      console.log("Access denied: Admin role required");
      window.location.href = "/?error=access_denied";
      return;
    }

    if (currentPath.startsWith("/vendedor") && userRole !== "VENDEDOR") {
      console.log("Access denied: Vendedor role required");
      window.location.href = "/?error=access_denied";
      return;
    }

    console.log("Access granted for:", currentPath);
  }

  // Interceptar clics en enlaces
  function interceptLinks() {
    document.addEventListener("click", async function (e) {
      const link = e.target.closest("a");
      if (!link) return;

      const href = link.getAttribute("href");
      if (!href || href.startsWith("#") || href.startsWith("javascript:"))
        return;

      // Verificar si es una ruta protegida
      if (!isProtectedRoute(href)) return;

      e.preventDefault();
      await loadProtectedPage(href);
    });
  }

  // Cargar página protegida con JWT (SIN REFRESH VISIBLE)
  async function loadProtectedPage(url) {
    const token = localStorage.getItem("accessToken");
    const userRole = localStorage.getItem("userRole");

    if (!token) {
      window.location.href = "/auth/login?redirect=" + encodeURIComponent(url);
      return;
    }

    // Verificar permisos por rol antes de hacer la petición
    if (url.startsWith("/admin") && userRole !== "ADMIN") {
      alert("No tienes permisos para acceder a esta página");
      return;
    }

    if (url.startsWith("/vendedor") && userRole !== "VENDEDOR") {
      alert("No tienes permisos para acceder a esta página");
      return;
    }

    // Mostrar indicador de carga
    showLoadingIndicator();

    try {
      const response = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          Accept: "text/html",
        },
      });

      if (response.status === 401) {
        console.log("Token expired, attempting refresh...");
        const refreshed = await refreshAccessToken();

        if (refreshed) {
          // Reintentar con nuevo token
          hideLoadingIndicator();
          return loadProtectedPage(url);
        } else {
          // Refresh falló, redirigir a login
          localStorage.clear();
          window.location.href =
            "/auth/login?redirect=" + encodeURIComponent(url);
        }
        return;
      }

      if (response.status === 403) {
        hideLoadingIndicator();
        alert("No tienes permisos para acceder a esta página");
        return;
      }

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const html = await response.text();

      // Extraer el contenido del body del HTML recibido
      const parser = new DOMParser();
      const doc = parser.parseFromString(html, "text/html");

      // Actualizar solo el contenido del body (SIN REFRESH)
      const newBody = doc.body;
      const currentBody = document.body;

      // Copiar atributos del body
      Array.from(newBody.attributes).forEach((attr) => {
        currentBody.setAttribute(attr.name, attr.value);
      });

      // Reemplazar contenido del body
      currentBody.innerHTML = newBody.innerHTML;

      // Actualizar título
      if (doc.title) {
        document.title = doc.title;
      }

      // Actualizar URL sin recargar
      window.history.pushState({ path: url }, "", url);

      // Re-inicializar scripts
      reinitializeScripts();

      // Ocultar indicador de carga
      hideLoadingIndicator();
    } catch (error) {
      console.error("Error loading protected page:", error);
      hideLoadingIndicator();
      alert("Error al cargar la página. Por favor, intenta de nuevo.");
    }
  }

  // Renovar access token usando refresh token
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

      if (!response.ok) {
        return false;
      }

      const data = await response.json();

      // Actualizar tokens
      localStorage.setItem("accessToken", data.accessToken);
      localStorage.setItem("refreshToken", data.refreshToken);

      console.log("Access token refreshed successfully");
      return true;
    } catch (error) {
      console.error("Error refreshing token:", error);
      return false;
    }
  }

  // Re-inicializar scripts después de actualizar el contenido
  function reinitializeScripts() {
    // Ejecutar scripts inline y externos
    const scripts = document.querySelectorAll("script");
    scripts.forEach((script) => {
      if (
        script.src &&
        !script.src.includes("bootstrap") &&
        !script.src.includes("navbar-jwt") &&
        !script.src.includes("protected-pages")
      ) {
        // Script externo - recargar (excepto los globales)
        const newScript = document.createElement("script");
        Array.from(script.attributes).forEach((attr) => {
          newScript.setAttribute(attr.name, attr.value);
        });
        script.parentNode.replaceChild(newScript, script);
      } else if (script.textContent && !script.src) {
        // Script inline - ejecutar
        try {
          const newScript = document.createElement("script");
          newScript.textContent = script.textContent;
          script.parentNode.replaceChild(newScript, script);
        } catch (e) {
          console.error("Error executing script:", e);
        }
      }
    });

    // Re-inicializar event listeners
    interceptLinks();
  }

  // Mostrar indicador de carga
  function showLoadingIndicator() {
    // Crear overlay si no existe
    if (!document.getElementById("jwt-loading-overlay")) {
      const overlay = document.createElement("div");
      overlay.id = "jwt-loading-overlay";
      overlay.style.cssText = `
                position: fixed;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: rgba(0, 0, 0, 0.3);
                display: flex;
                justify-content: center;
                align-items: center;
                z-index: 9999;
            `;
      overlay.innerHTML = `
                <div style="background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Cargando...</span>
                    </div>
                </div>
            `;
      document.body.appendChild(overlay);
    }
  }

  // Ocultar indicador de carga
  function hideLoadingIndicator() {
    const overlay = document.getElementById("jwt-loading-overlay");
    if (overlay) {
      overlay.remove();
    }
  }

  // Manejar botón "Atrás" del navegador
  window.addEventListener("popstate", function (e) {
    const currentPath = window.location.pathname;

    if (isProtectedRoute(currentPath)) {
      loadProtectedPage(currentPath);
    } else {
      window.location.reload();
    }
  });

  // Inicializar cuando el DOM esté listo
  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", function () {
      checkAuthOnPageLoad();
      interceptLinks();
    });
  } else {
    checkAuthOnPageLoad();
    interceptLinks();
  }
})();
