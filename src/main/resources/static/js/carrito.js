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

// Función para parsear la respuesta JSON de forma segura
async function parseJsonResponse(response) {
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
        return response.json();
    } else {
        // Si no es JSON, intentar leer como texto y lanzar un error
        const text = await response.text();
        throw new Error(`Respuesta inesperada del servidor. Tipo: ${contentType || 'desconocido'}. Contenido: ${text.substring(0, 100)}...`);
    }
}

// Función para actualizar la interfaz de usuario del carrito
function updateCartUI(carritoItems, subtotal, total) {
    const cartItemsContainer = document.getElementById('cart-items-list'); // Contenedor de los items del carrito
    const subtotalDisplay = document.getElementById('order-summary-subtotal');
    const shippingDisplay = document.getElementById('order-summary-shipping');
    const totalDisplay = document.getElementById('order-summary-total');
    const emptyCartMessage = document.querySelector('.text-center.py-5');

    // Actualizar subtotal y total
    if (subtotalDisplay) subtotalDisplay.textContent = `S/ ${subtotal.toFixed(2)}`;
    
    // Calcular y actualizar envío
    let shippingCost = 10.00; // Costo de envío por defecto
    if (subtotal > 100) {
        shippingCost = 0.00;
        if (shippingDisplay) shippingDisplay.textContent = 'GRATIS';
    } else {
        if (shippingDisplay) shippingDisplay.textContent = `S/ ${shippingCost.toFixed(2)}`;
    }

    // Actualizar total (ya viene del backend, pero se recalcula por si acaso)
    if (totalDisplay) totalDisplay.textContent = `S/ ${(subtotal + shippingCost).toFixed(2)}`;

    // Re-renderizar los items del carrito
    if (cartItemsContainer) {
        cartItemsContainer.innerHTML = ''; // Limpiar el contenedor antes de añadir nuevos items

        if (carritoItems && carritoItems.length > 0) {
            if (emptyCartMessage) emptyCartMessage.style.display = 'none'; // Ocultar mensaje de carrito vacío
            carritoItems.forEach(item => {
                const itemHTML = `
                    <div class="card mb-3 shadow-sm border-0">
                        <div class="row g-0 align-items-center">
                            <div class="col-md-3 text-center p-3">
                                <img src="${item.producto.imagenUrlPrincipal}" alt="Producto" class="img-fluid rounded" style="max-height:120px;">
                            </div>
                            <div class="col-md-4">
                                <div class="card-body">
                                    <h6 class="card-title mb-1">${item.producto.nombre}</h6>
                                    <p class="text-muted mb-0">S/ ${item.producto.precio.toFixed(2)}</p>
                                </div>
                            </div>
                            <div class="col-md-3 text-center">
                                <div class="d-flex justify-content-center align-items-center">
                                    <button class="btn btn-light border" onclick="disminuir(this)">-</button>
                                    <input type="number" min="0" class="form-control text-center mx-2 cart-quantity-input" style="width: 60px;" value="${item.cantidad}" data-item-id="${item.id}">
                                    <button class="btn btn-light border" onclick="aumentar(this)">+</button>
                                </div>
                            </div>
                            <div class="col-md-2 d-flex flex-column align-items-center justify-content-center px-2">
                                <button class="btn btn-sm btn-danger mb-2 ms-2 btn-eliminar-item" 
                                        data-item-id="${item.id}"
                                        data-producto-nombre="${item.producto.nombre}">
                                    <i class="bi bi-trash"></i>
                                </button>
                                <p class="fw-bold mt-2 ms-2">Subtotal: S/ ${(item.cantidad * item.producto.precio).toFixed(2)}</p>
                            </div>
                        </div>
                    </div>
                `;
                cartItemsContainer.insertAdjacentHTML('beforeend', itemHTML);
            });
            // Re-attach event listeners to new buttons
            attachEventListenersToCartItems();
        } else {
            if (emptyCartMessage) emptyCartMessage.style.display = 'block'; // Mostrar mensaje de carrito vacío
        }
    }
}

// Función para adjuntar event listeners a los botones de los items del carrito
function attachEventListenersToCartItems() {
    document.querySelectorAll('.cart-quantity-input').forEach(input => {
        input.removeEventListener('change', handleQuantityInputChange); // Remove old listeners
        input.addEventListener('change', handleQuantityInputChange); // Add new listeners
    });
    document.querySelectorAll('.btn-eliminar-item').forEach(button => {
        button.removeEventListener('click', handleDeleteButtonClick); // Remove old listeners
        button.addEventListener('click', handleDeleteButtonClick); // Add new listeners
    });
}

// Handler para cambios directos en el input de cantidad
function handleQuantityInputChange() {
    const itemId = this.dataset.itemId;
    let newQuantity = parseInt(this.value);
    
    if (newQuantity < 1) {
        mostrarAlerta('La cantidad debe ser mayor a 0', 'warning');
        this.value = 1; // Restablecer a 1
        newQuantity = 1;
    }
    
    updateCartItemQuantity(itemId, newQuantity);
}

// Handler para el botón de eliminar
function handleDeleteButtonClick(e) {
    e.preventDefault();
    const itemId = this.dataset.itemId;
    const productoNombre = this.dataset.productoNombre;
    
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
}


document.addEventListener('DOMContentLoaded', function() {
    function updateCartItemQuantity(itemId, newQuantity) {
        mostrarAlerta('Actualizando cantidad...', 'info');
        
        fetch('/carrito/actualizar-cantidad', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `itemId=${itemId}&cantidad=${newQuantity}`
        })
        .then(response => parseJsonResponse(response))
        .then(data => {
            if (data.success) {
                // mostrarAlerta(data.message, 'success'); // Eliminado
                updateCartUI(data.carritoItems, data.subtotal, data.total);
            } else {
                mostrarAlerta(data.message, 'danger');
            }
        })
        .catch(error => {
            console.error('Error en la petición AJAX:', error);
            mostrarAlerta('Error de conexión. Intenta de nuevo', 'danger');
            // En caso de error de red, recargar para asegurar consistencia
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
    // Se usa el handler separado para poder adjuntar/desadjuntar
    document.querySelectorAll('.cart-quantity-input').forEach(input => {
        input.addEventListener('change', handleQuantityInputChange);
    });

    // Handle delete button with confirmation
    // Se usa el handler separado para poder adjuntar/desadjuntar
    document.querySelectorAll('.btn-eliminar-item').forEach(button => {
        button.addEventListener('click', handleDeleteButtonClick);
    });

    // Función global para eliminar item (ahora con AJAX)
    window.eliminarItem = function(itemId, productoNombre) {
        document.getElementById('confirmDelete')?.remove();
        mostrarAlerta('Eliminando producto...', 'info');
        
        fetch(`/carrito/eliminar/${itemId}`, {
            method: 'GET', // O POST si el backend lo espera así
            headers: {
                'Content-Type': 'application/json',
            },
        })
        .then(response => parseJsonResponse(response))
        .then(data => {
            if (data.success) {
                // mostrarAlerta(data.message || 'Producto eliminado correctamente', 'success'); // Eliminado
                updateCartUI(data.carritoItems, data.subtotal, data.total);
            } else {
                mostrarAlerta(data.message || 'Error desconocido al eliminar', 'danger');
            }
        })
        .catch(error => {
            console.error('Error en la petición AJAX de eliminación:', error);
            mostrarAlerta(error.message || 'Error de conexión al eliminar. Intenta de nuevo', 'danger');
            setTimeout(() => {
                window.location.reload(); // Recargar en caso de error grave
            }, 2000);
        });
    };

    // Adjuntar listeners iniciales
    attachEventListenersToCartItems();
});