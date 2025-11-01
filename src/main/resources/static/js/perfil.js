document.addEventListener('DOMContentLoaded', function() {
    const urlParams = new URLSearchParams(window.location.search);
    const activeTabName = urlParams.get('tab'); // Busca el parámetro 'tab'

    // Si el parámetro existe (ej: ?tab=pedidos)
    if (activeTabName) {

        // 1. Desactivar la pestaña 'perfil' que está activa por defecto en el HTML
        const perfilTabButton = document.getElementById('perfil-tab');
        const perfilTabContent = document.getElementById('perfil');

        if (perfilTabButton) {
            perfilTabButton.classList.remove('active');
            perfilTabButton.setAttribute('aria-selected', 'false');
        }
        if (perfilTabContent) {
            perfilTabContent.classList.remove('show', 'active');
        }

        // 2. Activar la pestaña solicitada ('pedidos')
        const tabButton = document.getElementById(activeTabName + '-tab');
        const tabContent = document.getElementById(activeTabName);

        if (tabButton && tabContent) {
            tabButton.classList.add('active');
            tabButton.setAttribute('aria-selected', 'true');
            tabContent.classList.add('show', 'active');
        }
    }
});
