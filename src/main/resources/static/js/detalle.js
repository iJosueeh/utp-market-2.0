function incrementarCantidad() {
    const input = document.getElementById('cantidad');
    const max = parseInt(input.getAttribute('max'));
    let value = parseInt(input.value);
    if (value < max) {
        input.value = value + 1;
    } else {
        mostrarAlerta(`Stock máximo disponible: ${max} unidades`, 'warning');
    }
}

function decrementarCantidad() {
    const input = document.getElementById('cantidad');
    let value = parseInt(input.value);
    if (value > 1) {
        input.value = value - 1;
    } else {
        mostrarAlerta('La cantidad mínima es 1', 'info');
    }
}

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
    
    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        const alertElement = document.getElementById(alertId);
        if (alertElement) {
            alertElement.classList.add('animate__fadeOutUp');
            setTimeout(() => {
                alertElement.remove();
            }, 500);
        }
    }, 5000);
}

async function agregarAFavoritos(button) {
    const isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
    if (!isLoggedIn) {
        mostrarAlerta('Debes iniciar sesión para añadir a favoritos', 'warning');
        return;
    }

    const productoId = button.dataset.productoId;
    const icon = button.querySelector('i');

    try {
        const response = await fetchAuth(`/api/favoritos/toggle/${productoId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        });
        const data = await response.json();

        if (response.ok && data.success) {
            if (data.added) {
                icon.classList.remove('bi-heart');
                icon.classList.add('bi-heart-fill', 'text-danger');
                mostrarAlerta('Producto agregado a tus favoritos', 'success');
            } else {
                icon.classList.remove('bi-heart-fill', 'text-danger');
                icon.classList.add('bi-heart');
                mostrarAlerta('Producto eliminado de favoritos', 'info');
            }
            console.log(data.message);
        } else {
            console.error('Error al actualizar favoritos:', data.message);
            mostrarAlerta('Error: ' + (data.message || 'Error desconocido'), 'danger');
        }
    } catch (error) {
        if (error.message !== 'Sesión expirada') {
            console.error('Error en la petición AJAX de favoritos:', error);
            mostrarAlerta('Error de conexión. Intenta de nuevo', 'danger');
        }
    }
}

async function checkFavoriteStatus(productId, iconElement) {
    const isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
    if (!isLoggedIn) return;

    try {
        const response = await fetchAuth(`/api/favoritos/isFavorito/${productId}`);
        const data = await response.json();
        
        if (response.ok && data.success && data.isFavorito) {
            iconElement.classList.remove('bi-heart');
            iconElement.classList.add('bi-heart-fill', 'text-danger');
        }
    } catch (error) {
        if (error.message !== 'Sesión expirada') {
            console.error('Error al verificar estado de favoritos:', error);
        }
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const favoriteButton = document.querySelector('.favorite-btn');
    if (favoriteButton) {
        const productId = favoriteButton.dataset.productoId;
        const iconElement = favoriteButton.querySelector('i');
        checkFavoriteStatus(productId, iconElement);
    }

    // Validación y envío del formulario de agregar al carrito
    const addToCartForm = document.querySelector('form[action*="/carrito/agregar"]');
    if (addToCartForm) {
        addToCartForm.addEventListener('submit', async function(event) {
            event.preventDefault(); // Prevenir el envío tradicional del formulario

            const cantidadInput = document.getElementById('cantidad');
            const productoIdInput = document.getElementById('productoId'); // Assuming there's a hidden input for productoId
            const cantidad = parseInt(cantidadInput.value);
            const productoId = productoIdInput ? productoIdInput.value : null;

            // Realizar validaciones existentes
            const max = parseInt(cantidadInput.getAttribute('max'));
            const stock = max; // En este contexto, 'max' es el stock disponible

            if (!productoId) {
                mostrarAlerta('ID de producto no encontrado.', 'danger');
                return;
            }

            if (isNaN(cantidad) || cantidad < 1) {
                mostrarAlerta('La cantidad debe ser mayor a 0', 'warning');
                cantidadInput.focus();
                return;
            }

            if (cantidad > stock) {
                mostrarAlerta(`Stock insuficiente. Solo hay ${stock} unidades disponibles`, 'warning');
                cantidadInput.value = stock;
                cantidadInput.focus();
                return;
            }

            if (stock === 0) {
                mostrarAlerta('Producto agotado. No hay unidades disponibles', 'danger');
                return;
            }

            // Si las validaciones pasan, enviar la petición con fetchAuth
            mostrarAlerta('Agregando producto al carrito...', 'info');
            try {
                const formData = new URLSearchParams();
                formData.append('productoId', productoId);
                formData.append('cantidad', cantidad);

                const response = await fetchAuth('/carrito/agregar', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: formData,
                });

                if (response.ok) {
                    // Asumimos que el backend envía un mensaje o redirige
                    mostrarAlerta('¡Producto agregado al carrito exitosamente!', 'success');
                    // Opcional: Actualizar algún contador del carrito en el header
                } else {
                    const errorText = await response.text();
                    mostrarAlerta(`Error al agregar al carrito: ${errorText}`, 'danger');
                }
            } catch (error) {
                if (error.message !== 'Sesión expirada') {
                    console.error('Error en la petición de agregar al carrito:', error);
                    mostrarAlerta('Error de conexión al agregar al carrito.', 'danger');
                }
            }
        });

        // Validación en tiempo real del input de cantidad
        const cantidadInput = document.getElementById('cantidad');
        if (cantidadInput) {
            cantidadInput.addEventListener('input', function() {
                const cantidad = parseInt(this.value);
                const max = parseInt(this.getAttribute('max'));

                if (cantidad > max) {
                    this.value = max;
                    mostrarAlerta(`Cantidad ajustada al stock disponible: ${max}`, 'info');
                } else if (cantidad < 1 && this.value !== '') {
                    this.value = 1;
                    mostrarAlerta('La cantidad mínima es 1', 'info');
                }
            });
        }
    }

    var editReviewModal = document.getElementById('editReviewModal');
    if (editReviewModal) {
        editReviewModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget;
            var reviewId = button.getAttribute('data-review-id');
            var reviewPuntaje = button.getAttribute('data-review-puntaje');
            var reviewComentario = button.getAttribute('data-review-comentario');

            var modalReviewId = editReviewModal.querySelector('#editReviewId');
            var modalComentario = editReviewModal.querySelector('#editComentario');
            var modalRatingStars = editReviewModal.querySelector('#editRatingStars');

            modalReviewId.value = reviewId;
            modalComentario.value = reviewComentario;

            var stars = modalRatingStars.querySelectorAll('input[name="puntaje"]');
            stars.forEach(function(star) {
                if (star.value == reviewPuntaje) {
                    star.checked = true;
                } else {
                    star.checked = false;
                }
            });
        });
    }
});