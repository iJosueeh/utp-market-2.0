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

function agregarAFavoritos(button) {
    const isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
    if (!isLoggedIn) {
        mostrarAlerta('Debes iniciar sesión para añadir a favoritos', 'warning');
        return;
    }

    const productoId = button.dataset.productoId;
    const icon = button.querySelector('i');

    fetch(`/api/favoritos/toggle/${productoId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
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
            mostrarAlerta('Error: ' + data.message, 'danger');
        }
    })
    .catch(error => {
        console.error('Error en la petición AJAX de favoritos:', error);
        mostrarAlerta('Error de conexión. Intenta de nuevo', 'danger');
    });
}

function checkFavoriteStatus(productId, iconElement) {
    const isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
    if (!isLoggedIn) return;

    fetch(`/api/favoritos/isFavorito/${productId}`)
        .then(response => response.json())
        .then(data => {
            if (data.success && data.isFavorito) {
                iconElement.classList.remove('bi-heart');
                iconElement.classList.add('bi-heart-fill', 'text-danger');
            }
        })
        .catch(error => {
            console.error('Error al verificar estado de favoritos:', error);
        });
}

document.addEventListener('DOMContentLoaded', function() {
    const favoriteButton = document.querySelector('.favorite-btn');
    if (favoriteButton) {
        const productId = favoriteButton.dataset.productoId;
        const iconElement = favoriteButton.querySelector('i');
        checkFavoriteStatus(productId, iconElement);
    }

    // Validación del formulario de agregar al carrito
    const addToCartForm = document.querySelector('form[action*="/carrito/agregar"]');
    if (addToCartForm) {
        addToCartForm.addEventListener('submit', function(event) {
            const cantidadInput = document.getElementById('cantidad');
            const cantidad = parseInt(cantidadInput.value);
            const max = parseInt(cantidadInput.getAttribute('max'));
            const stock = max;

            // Validar cantidad
            if (isNaN(cantidad) || cantidad < 1) {
                event.preventDefault();
                mostrarAlerta('La cantidad debe ser mayor a 0', 'warning');
                cantidadInput.focus();
                return false;
            }

            if (cantidad > stock) {
                event.preventDefault();
                mostrarAlerta(`Stock insuficiente. Solo hay ${stock} unidades disponibles`, 'warning');
                cantidadInput.value = stock;
                cantidadInput.focus();
                return false;
            }

            if (stock === 0) {
                event.preventDefault();
                mostrarAlerta('Producto agotado. No hay unidades disponibles', 'danger');
                return false;
            }

            // Si todo está bien, mostrar mensaje de procesamiento
            mostrarAlerta('Agregando producto al carrito...', 'info');
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