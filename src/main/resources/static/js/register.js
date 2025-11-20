document.addEventListener('DOMContentLoaded', function() {
    // --- Lógica de validación de formulario de Bootstrap ---
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // --- Funcionalidad de alternar visibilidad de contraseña ---
    function setupPasswordToggle(toggleButtonId, passwordInputId) {
        const toggleButton = document.getElementById(toggleButtonId);
        const passwordInput = document.getElementById(passwordInputId);
        if (toggleButton && passwordInput) {
            const icon = toggleButton.querySelector('i');
            toggleButton.addEventListener('click', function () {
                const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
                passwordInput.setAttribute('type', type);
                icon.classList.toggle('bi-eye');
                icon.classList.toggle('bi-eye-slash');
            });
        }
    }

    setupPasswordToggle('togglePassword', 'password');
    setupPasswordToggle('toggleConfirmPassword', 'confirmPassword');

    // --- Nueva lógica para el envío del formulario de registro con JWT ---
    const registerForm = document.querySelector('form[th\\:action*="/auth/register"]');
    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault(); // Previene el envío tradicional del formulario

            const nombre = document.getElementById('nombre').value;
            const apellido = document.getElementById('apellido').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;

            const errorContainer = document.getElementById('client-error-alert') || createErrorAlertDiv();
            errorContainer.style.display = 'none'; // Oculta errores previos

            // Validaciones adicionales (además de las de Bootstrap)
            if (password !== confirmPassword) {
                displayError(errorContainer, 'Las contraseñas no coinciden.');
                return;
            }

            if (!registerForm.checkValidity()) {
                // Si la validación de Bootstrap falla, no hacemos la llamada AJAX
                registerForm.classList.add('was-validated');
                return;
            }

            try {
                const response = await fetchAuth('/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ nombre, apellido, email, password }),
                });

                if (response.ok) {
                    // Si el registro es exitoso, redirige al login con un mensaje de éxito
                    alert('Registro exitoso. Por favor, inicia sesión.');
                    window.location.href = '/auth/login?registerSuccess=true';
                } else {
                    const errorData = await response.text(); // El backend podría devolver un texto de error
                    displayError(errorContainer, errorData || 'Error desconocido durante el registro.');
                }
            } catch (error) {
                if (error.message !== 'Sesión expirada') { // fetchAuth ya maneja la expiración de sesión
                    console.error('Error en el proceso de registro:', error);
                    displayError(errorContainer, 'No se pudo conectar con el servidor. Inténtalo más tarde.');
                }
            }
        });
    }

    // --- Funciones de utilidad para alertas ---
    function createErrorAlertDiv() {
        const alertDiv = document.createElement('div');
        alertDiv.id = 'client-error-alert';
        alertDiv.className = 'alert alert-danger py-2 small animate__animated animate__fadeInDown';
        alertDiv.setAttribute('role', 'alert');
        alertDiv.style.display = 'none'; // Inicialmente oculto
        alertDiv.innerHTML = `<i class="bi bi-exclamation-triangle-fill me-2"></i><span></span>`;
        // Insertar antes del formulario
        const form = document.querySelector('form');
        if (form) {
            form.parentNode.insertBefore(alertDiv, form);
        }
        return alertDiv;
    }

    function displayError(container, message) {
        container.querySelector('span').textContent = message;
        container.style.display = 'block';
        container.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
});