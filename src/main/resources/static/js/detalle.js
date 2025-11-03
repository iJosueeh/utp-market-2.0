function incrementarCantidad() {
    const input = document.getElementById('cantidad');
    const max = parseInt(input.getAttribute('max'));
    let value = parseInt(input.value);
    if (value < max) {
        input.value = value + 1;
    }
}

function decrementarCantidad() {
    const input = document.getElementById('cantidad');
    let value = parseInt(input.value);
    if (value > 1) {
        input.value = value - 1;
    }
}

function agregarAFavoritos(button) {
    const isLoggedIn = document.body.getAttribute('data-is-logged-in') === 'true';
    if (!isLoggedIn) {
        alert('Debes iniciar sesión para añadir a favoritos.');
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
            } else {
                icon.classList.remove('bi-heart-fill', 'text-danger');
                icon.classList.add('bi-heart');
            }
            console.log(data.message);
        } else {
            console.error('Error al actualizar favoritos:', data.message);
        }
    })
    .catch(error => {
        console.error('Error en la petición AJAX de favoritos:', error);
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
});
