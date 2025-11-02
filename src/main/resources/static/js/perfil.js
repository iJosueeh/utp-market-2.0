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

    // Refactorización de onclick a event listener para agregar a favoritos
    const addToFavoritesButtons = document.querySelectorAll('.add-to-favorites-btn');
    addToFavoritesButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.dataset.productoId;
            // Asumiendo que agregarAFavoritos es una función global o definida en otro script
            if (typeof agregarAFavoritos === 'function') {
                agregarAFavoritos(this); // Pasa el elemento del botón si la función lo requiere
            } else {
                console.warn('La función agregarAFavoritos no está definida.');
                // Aquí podrías implementar la lógica directamente si agregarAFavoritos no es global
                // Por ejemplo, una llamada AJAX para añadir/quitar de favoritos
            }
        });
    });

    // Refactorización de onclick a event listener para agregar al carrito
    const addToCartButtons = document.querySelectorAll('.add-to-cart-btn');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.dataset.productoId;
            // Asumiendo que agregarAlCarrito es una función global o definida en otro script
            if (typeof agregarAlCarrito === 'function') {
                agregarAlCarrito(this); // Pasa el elemento del botón si la función lo requiere
            } else {
                console.warn('La función agregarAlCarrito no está definida.');
                // Aquí podrías implementar la lógica directamente si agregarAlCarrito no es global
                // Por ejemplo, una llamada AJAX para añadir al carrito
            }
        });
    });
});
