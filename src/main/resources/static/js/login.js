// login.js - Login con JWT para toda la aplicación
document.addEventListener("DOMContentLoaded", function () {
  const loginForm = document.querySelector('form[action*="/auth/login"]');

  if (loginForm) {
    // Interceptar el submit del formulario
    loginForm.addEventListener("submit", async function (e) {
      e.preventDefault(); // Prevenir submit tradicional

      const email = document.getElementById("email").value;
      const password = document.getElementById("password").value;
      const submitBtn = loginForm.querySelector('button[type="submit"]');
      const alertDiv = document.querySelector(".alert");

      // Deshabilitar botón
      submitBtn.disabled = true;
      submitBtn.innerHTML =
        '<span class="spinner-border spinner-border-sm me-2"></span>Iniciando sesión...';

      try {
        const response = await fetch("/auth/login", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ email, password }),
        });

        if (response.ok) {
          const data = await response.json();

          // Guardar tokens en localStorage
          localStorage.setItem("accessToken", data.token);
          localStorage.setItem("refreshToken", data.refreshToken);
          localStorage.setItem("userEmail", data.email);
          localStorage.setItem("userName", data.nombre);
          localStorage.setItem("userRole", data.rol);

          // Redirigir según el rol
          if (data.rol === "ADMIN") {
            window.location.href = "/admin/dashboard";
          } else if (data.rol === "VENDEDOR") {
            window.location.href = "/vendedor/dashboard";
          } else {
            window.location.href = "/";
          }
        } else {
          const error = await response.json();

          // Mostrar error con SweetAlert2
          const isDarkMode =
            document.documentElement.getAttribute("data-theme") === "dark";

          Swal.fire({
            icon: "error",
            title: "Error de autenticación",
            text:
              error.message ||
              "Credenciales inválidas. Verifica tu correo y contraseña.",
            confirmButtonColor: "#dc3545",
            confirmButtonText: "Intentar de nuevo",
            background: isDarkMode ? "#1a1a1a" : "#fff",
            color: isDarkMode ? "#e0e0e0" : "#545454",
          });

          // Rehabilitar botón
          submitBtn.disabled = false;
          submitBtn.innerHTML = "Iniciar Sesión";
        }
      } catch (error) {
        console.error("Error durante el login:", error);

        // Mostrar error con SweetAlert2
        const isDarkMode =
          document.documentElement.getAttribute("data-theme") === "dark";

        Swal.fire({
          icon: "error",
          title: "Error de conexión",
          text: "No se pudo conectar con el servidor. Por favor, intenta de nuevo.",
          confirmButtonColor: "#dc3545",
          confirmButtonText: "Reintentar",
          background: isDarkMode ? "#1a1a1a" : "#fff",
          color: isDarkMode ? "#e0e0e0" : "#545454",
        });

        // Rehabilitar botón
        submitBtn.disabled = false;
        submitBtn.innerHTML = "Iniciar Sesión";
      }
    });
  }

  // Toggle de contraseña (mostrar/ocultar)
  const togglePassword = document.getElementById("togglePassword");
  const passwordInput = document.getElementById("password");

  if (togglePassword && passwordInput) {
    togglePassword.addEventListener("click", function () {
      // Cambiar tipo de input
      const type =
        passwordInput.getAttribute("type") === "password" ? "text" : "password";
      passwordInput.setAttribute("type", type);

      // Cambiar icono
      const icon = this.querySelector("i");
      if (icon) {
        if (type === "password") {
          icon.classList.remove("bi-eye-slash");
          icon.classList.add("bi-eye");
        } else {
          icon.classList.remove("bi-eye");
          icon.classList.add("bi-eye-slash");
        }
      }
    });
  }
});
