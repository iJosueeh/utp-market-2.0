document.addEventListener('DOMContentLoaded', function() {
    // --- Mantiene la funcionalidad de mostrar/ocultar contraseña ---
    const togglePassword = document.querySelector('#togglePassword');
    const passwordInput = document.querySelector('#password');
    if (togglePassword && passwordInput) {
        const icon = togglePassword.querySelector('i');
        togglePassword.addEventListener('click', () => {
            const type = passwordInput.type === 'password' ? 'text' : 'password';
            passwordInput.type = type;
            icon.classList.toggle('bi-eye');
            icon.classList.toggle('bi-eye-slash');
        });
    }
});