document.addEventListener('DOMContentLoaded', function() {
    function updateCartItemQuantity(itemId, newQuantity) {
        fetch('/carrito/actualizar-cantidad', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `itemId=${itemId}&cantidad=${newQuantity}`
        })
        .then(response => {
            if (response.ok) {
                window.location.href = '/carrito?_=' + new Date().getTime(); // Redirect with cache-busting
            } else {
                response.text().then(text => {
                    alert('Error al actualizar la cantidad: ' + text);
                    window.location.href = '/carrito?_=' + new Date().getTime(); // Redirect with cache-busting
                });
            }
        })
        .catch(error => {
            console.error('Error en la petición AJAX:', error);
            alert('Error de conexión al actualizar la cantidad.');
            window.location.reload(); // Reload to revert to previous state
        });
    }

    window.aumentar = function(btn) {
        const input = btn.parentNode.querySelector('input[type="number"]');
        const itemId = input.dataset.itemId; // Assuming itemId is stored in a data attribute
        input.stepUp();
        updateCartItemQuantity(itemId, parseInt(input.value));
    };

    window.disminuir = function(btn) {
        const input = btn.parentNode.querySelector('input[type="number"]');
        const itemId = input.dataset.itemId; // Assuming itemId is stored in a data attribute
        if (input.value > 0) { // Allow quantity to go to 0 to trigger removal
            input.stepDown();
        }
        updateCartItemQuantity(itemId, parseInt(input.value));
    };

    // Also handle direct input changes
    document.querySelectorAll('.cart-quantity-input').forEach(input => {
        input.addEventListener('change', function() {
            const itemId = this.dataset.itemId;
            const newQuantity = parseInt(this.value);
            updateCartItemQuantity(itemId, newQuantity);
        });
    });
});