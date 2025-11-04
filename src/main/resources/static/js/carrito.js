// Función para mostrar alertas de Bootstrap dinámicas
function mostrarAlerta(mensaje, tipo = 'success') {
    const alertContainer = document.getElementById('dynamicAlertContainer');
    if (!alertContainer) return;
    
    const alertId = 'alert-' + Date.now();
    
    const alertHTML = `
        <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show animate__animated animate__fadeInDown" role="alert">
            <i class="bi ${tipo === 'success' ? 'bi-check-circle-fill' : tipo === 'warning' ? 'bi-exclamation-circle-fill' : tipo === 'info' ? 'bi-info-circle-fill' : 'bi-exclamation-triangle-fill'} me-2"></i>
            <strong>${mensaje}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    
    alertContainer.insertAdjacentHTML('beforeend', alertHTML);
    
    // Hacer scroll suave hacia la alerta
    window.scrollTo({ top: 0, behavior: 'smooth' });
    
    // Auto-cerrar después de 4 segundos
    setTimeout(() => {
        const alertElement = document.getElementById(alertId);
        if (alertElement) {
            alertElement.classList.add('animate__fadeOutUp');
            setTimeout(() => {
                alertElement.remove();
            }, 500);
        }
    }, 4000);
}

document.addEventListener('DOMContentLoaded', function() {
    function updateCartItemQuantity(itemId, newQuantity) {
        // Mostrar alerta de procesamiento
        mostrarAlerta('Actualizando cantidad...', 'info');
        
        fetch('/carrito/actualizar-cantidad', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `itemId=${itemId}&cantidad=${newQuantity}`
        })
        .then(response => {
            if (response.ok) {
                mostrarAlerta('Cantidad actualizada correctamente', 'success');
                setTimeout(() => {
                    window.location.href = '/carrito?_=' + new Date().getTime();
                }, 1000);
            } else {
                response.text().then(text => {
                    mostrarAlerta('Error al actualizar: ' + text, 'danger');
                    setTimeout(() => {
                        window.location.href = '/carrito?_=' + new Date().getTime();
                    }, 2000);
                });
            }
        })
        .catch(error => {
            console.error('Error en la petición AJAX:', error);
            mostrarAlerta('Error de conexión. Intenta de nuevo', 'danger');
            setTimeout(() => {
                window.location.reload();
            }, 2000);
        });
    }

    window.aumentar = function(btn) {
        const input = btn.parentNode.querySelector('input[type="number"]');
        const itemId = input.dataset.itemId;
        const maxStock = parseInt(input.getAttribute('max')) || 999;
        
        if (parseInt(input.value) >= maxStock) {
            mostrarAlerta('Stock máximo alcanzado', 'warning');
            return;
        }
        
        input.stepUp();
        updateCartItemQuantity(itemId, parseInt(input.value));
    };

    window.disminuir = function(btn) {
        const input = btn.parentNode.querySelector('input[type="number"]');
        const itemId = input.dataset.itemId;
        
        if (input.value > 1) {
            input.stepDown();
            updateCartItemQuantity(itemId, parseInt(input.value));
        } else {
            mostrarAlerta('La cantidad mínima es 1. Para eliminar, usa el botón de eliminar', 'warning');
        }
    };

    // Handle direct input changes
    document.querySelectorAll('.cart-quantity-input').forEach(input => {
        input.addEventListener('change', function() {
            const itemId = this.dataset.itemId;
            let newQuantity = parseInt(this.value);
            
            if (newQuantity < 1) {
                mostrarAlerta('La cantidad debe ser mayor a 0', 'warning');
                this.value = 1;
                return;
            }
            
            updateCartItemQuantity(itemId, newQuantity);
        });
    });

    // Handle delete button with confirmation
    document.querySelectorAll('.btn-eliminar-item').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const itemId = this.dataset.itemId;
            const productoNombre = this.dataset.productoNombre;
            
            // Crear modal de confirmación con Bootstrap alert
            const confirmHTML = `
                <div id="confirmDelete" class="alert alert-warning alert-dismissible animate__animated animate__fadeInDown shadow-lg" role="alert" style="position: fixed; top: 100px; left: 50%; transform: translateX(-50%); z-index: 9999; max-width: 500px;">
                    <h5 class="alert-heading">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        ¿Confirmar eliminación?
                    </h5>
                    <p class="mb-3">¿Estás seguro de eliminar "<strong>${productoNombre}</strong>" del carrito?</p>
                    <hr>
                    <div class="d-flex gap-2 justify-content-end">
                        <button class="btn btn-sm btn-secondary" onclick="document.getElementById('confirmDelete').remove()">
                            Cancelar
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="eliminarItem(${itemId}, '${productoNombre}')">
                            <i class="bi bi-trash me-1"></i>Eliminar
                        </button>
                    </div>
                </div>
            `;
            
            document.body.insertAdjacentHTML('beforeend', confirmHTML);
        });
    });

    // Función global para eliminar item
    window.eliminarItem = function(itemId, productoNombre) {
        document.getElementById('confirmDelete')?.remove();
        mostrarAlerta('Eliminando producto...', 'info');
        
        window.location.href = `/carrito/eliminar/${itemId}`;
    };
});