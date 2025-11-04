// Función para mostrar alertas de Bootstrap dinámicas
function mostrarAlerta(mensaje, tipo = 'success') {
    const alertContainer = document.getElementById('dynamicAlertContainer');
    const alertId = 'alert-' + Date.now();
    
    const alertHTML = `
        <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show animate__animated animate__fadeInDown" role="alert">
            <i class="bi ${tipo === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'} me-2"></i>
            <strong>${mensaje}</strong>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;
    
    alertContainer.insertAdjacentHTML('beforeend', alertHTML);
    
    // Hacer scroll suave hacia la alerta
    alertContainer.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    
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

// Función para agregar productos al carrito
function agregarAlCarrito(button) {
    const productoId = button.dataset.productoId;
    
    const formData = new URLSearchParams();
    formData.append('productoId', productoId);
    formData.append('cantidad', 1);

    fetch('/carrito/agregar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData
    })
    .then(response => {
        if (response.ok) {
            mostrarAlerta('¡Producto agregado al carrito exitosamente!', 'success');
        } else {
            return response.text().then(text => {
                mostrarAlerta('Error al agregar al carrito: ' + text, 'danger');
            });
        }
    })
    .catch(error => {
        console.error('Error en la petición AJAX:', error);
        mostrarAlerta('Error de conexión al agregar al carrito.', 'danger');
    });
}

// Función para agregar/quitar de favoritos
function agregarAFavoritos(button) {
    const isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
    if (!isLoggedIn) {
        mostrarAlerta('Debes iniciar sesión para añadir a favoritos.', 'warning');
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
                mostrarAlerta('Producto agregado a favoritos', 'success');
            } else {
                icon.classList.remove('bi-heart-fill', 'text-danger');
                icon.classList.add('bi-heart');
                mostrarAlerta('Producto eliminado de favoritos', 'info');
                // Recargar la página si estamos en la pestaña de favoritos
                const favoritosTab = document.getElementById('favoritos');
                if (favoritosTab && favoritosTab.classList.contains('active')) {
                    setTimeout(() => location.reload(), 1000);
                }
            }
            console.log(data.message);
        } else {
            console.error('Error al actualizar favoritos:', data.message);
            mostrarAlerta('Error al actualizar favoritos: ' + data.message, 'danger');
        }
    })
    .catch(error => {
        console.error('Error en la petición AJAX de favoritos:', error);
        mostrarAlerta('Error de conexión al actualizar favoritos.', 'danger');
    });
}

document.addEventListener("DOMContentLoaded", function () {
    const urlParams = new URLSearchParams(window.location.search);
    const activeTabName = urlParams.get("tab"); // Busca el parámetro 'tab'

    // Si el parámetro existe (ej: ?tab=pedidos)
    if (activeTabName) {
        // 1. Desactivar la pestaña 'perfil' que está activa por defecto en el HTML
        const perfilTabButton = document.getElementById("perfil-tab");
        const perfilTabContent = document.getElementById("perfil");

        if (perfilTabButton) {
            perfilTabButton.classList.remove("active");
            perfilTabButton.setAttribute("aria-selected", "false");
        }
        if (perfilTabContent) {
            perfilTabContent.classList.remove("show", "active");
        }

        // 2. Activar la pestaña solicitada ('pedidos')
        const tabButton = document.getElementById(activeTabName + "-tab");
        const tabContent = document.getElementById(activeTabName);

        if (tabButton && tabContent) {
            tabButton.classList.add("active");
            tabButton.setAttribute("aria-selected", "true");
            tabContent.classList.add("show", "active");
        }
    }

    // Refactorización de onclick a event listener para agregar a favoritos
    const addToFavoritesButtons = document.querySelectorAll(
        ".add-to-favorites-btn"
    );
    addToFavoritesButtons.forEach((button) => {
        button.addEventListener("click", function () {
            agregarAFavoritos(this);
        });
    });

    // Refactorización de onclick a event listener para agregar al carrito
    const addToCartButtons = document.querySelectorAll(".add-to-cart-btn");
    addToCartButtons.forEach((button) => {
        button.addEventListener("click", function () {
            agregarAlCarrito(this);
        });
    });
    const editReviewModal = document.getElementById("editReviewModal");
    if (editReviewModal) {
        editReviewModal.addEventListener("show.bs.modal", function (event) {
            const button = event.relatedTarget; // Button that triggered the modal
            const reviewId = button.getAttribute("data-review-id");
            const reviewPuntaje = button.getAttribute("data-review-puntaje");
            const reviewComentario = button.getAttribute("data-review-comentario");

            const modalReviewId = editReviewModal.querySelector("#editReviewId");
            const modalEditComentario =
                editReviewModal.querySelector("#editComentario");
            const modalEditRatingStars =
                editReviewModal.querySelector("#editRatingStars");

            modalReviewId.value = reviewId;
            modalEditComentario.value = reviewComentario;

            // Set the rating stars
            modalEditRatingStars
                .querySelectorAll('input[name="puntaje"]')
                .forEach((radio) => {
                    if (parseInt(radio.value) === parseInt(reviewPuntaje)) {
                        radio.checked = true;
                    } else {
                        radio.checked = false;
                    }
                });
        });
    }
});
